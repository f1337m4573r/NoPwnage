package cc.co.evenprime.bukkit.nopwnage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NoPwnageListener implements Listener {

    private final NoPwnage plugin;

    private String lastBanCausingMessage;
    private long lastBanCausingMessageTime;
    private String lastGlobalMessage;
    private long lastGlobalMessageTime;
    private int globalRepeated;

    public NoPwnageListener(NoPwnage instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void chat(PlayerChatEvent event) {

        Player player = event.getPlayer();
        NoPwnageConfiguration config = plugin.getNPconfig();

        if(event.isCancelled() || !plugin.enabled || player.hasPermission(Permissions.SPAM) || player.hasPermission(Permissions.ADMIN))
            return;

        PlayerData data = plugin.getData(player);
        long time = System.currentTimeMillis();
        Location location = player.getLocation();

        double suspicion = 0;

        StringBuilder reasons = new StringBuilder();

        if(data.location == null) {
            data.setLocation(location);
        } else if(!data.compareLocation(location)) {
            data.setLocation(location);
            data.lastMovedTime = time;
        }

        String message = event.getMessage();

        if(config.banned && time - lastBanCausingMessageTime < config.bannedTimeout && similar(message, lastBanCausingMessage)) {
            suspicion += config.bannedWeight;
            addReason(reasons, "banned message", config.bannedWeight);
        }

        if(config.first && time - data.joinTime <= config.firstTimeout) {
            suspicion += config.firstWeight;
            addReason(reasons, "first message", config.firstWeight);
        }

        if(config.global && time - lastGlobalMessageTime < config.globalTimeout && similar(message, lastGlobalMessage)) {
            globalRepeated++;
            if(globalRepeated > 3) {
                globalRepeated = 3;
            }
            int added = globalRepeated * config.globalWeight;
            suspicion += added;
            addReason(reasons, "global message repeat", added);
        } else {
            globalRepeated = 0;
        }

        if(config.speed && time - data.lastMessageTime <= config.speedTimeout) {
            suspicion += config.speedWeight;
            addReason(reasons, "message speed", config.speedWeight);
        }

        if(config.repeat && time - data.lastMessageTime <= config.repeatTimeout && similar(message, data.lastMessage)) {
            data.repeats++;
            if(data.repeats > 3) {
                data.repeats = 3;
            }
            int added = data.repeats * config.repeatWeight;
            suspicion += added;
            addReason(reasons, "player message repeat", added);
        } else {
            data.repeats = 0;
        }

        boolean warned = false;
        if(config.warnPlayers && time - data.lastWarningTime <= config.warnTimeout) {
            suspicion += 100;
            addReason(reasons, "warned", 100);
            warned = true;
        }

        if(config.move && time - data.lastMovedTime <= config.moveTimeout) {
            suspicion -= config.moveWeight;
            addReason(reasons, "moved", -config.moveWeight);
        } else {
            addReason(reasons, "not moved", config.moveWeight);
            suspicion += config.moveWeight;
        }

        plugin.log("Suspicion: " + reasons + ": " + suspicion);

        if(suspicion >= config.warnLevel && config.warnPlayers && !warned) {
            data.lastWarningTime = time;
            warnPlayer(player);
        } else if(suspicion >= config.banLevel) {
            lastBanCausingMessage = message;
            lastBanCausingMessageTime = time;
            data.lastWarningTime = time;
            if(config.warnOthers) {
                warnOthers(player);
            }
            banPlayer(player, "Spambotlike behaviour");
            plugin.log(player.getName() + " banned for " + reasons + ": " + suspicion);
            event.setCancelled(true);
            return;
        }

        data.lastMessage = message;
        data.lastMessageTime = time;

        lastGlobalMessage = message;
        lastGlobalMessageTime = time;
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        PlayerData data = plugin.getData(player);
        long now = System.currentTimeMillis();

        if(plugin.enabled && data.leaveTime != 0) {
            if(now - data.leaveTime <= 5000 && !player.hasPermission(Permissions.LOGIN)) {
                if(now - data.lastRelogWarningTime < 30000) {
                    banPlayer(player, "Relogged too fast!");
                    plugin.log("Banned " + player.getName() + " for relogging too fast. Possible hacks. IP: " + player.getAddress().toString().substring(1));
                    data.leaveTime = 0;
                } else {
                    player.sendMessage("[NoPwnage]: " + ChatColor.DARK_RED + "You relogged really fast! If you do it again, you're going to be banned.");
                    data.lastRelogWarningTime = now;
                }
            } else {
                plugin.log(player.getName() + " is suspicious for logging in too fast! Not banned due to permissions.");
            }

        }

        Location l = player.getLocation();
        data.setLocation(l);
        data.joinTime = now;
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        PlayerData data = plugin.getData(player);

        long now = System.currentTimeMillis();

        if(plugin.enabled) {
            if(now - data.joinTime <= 300) {
                banPlayer(player, "");
                plugin.log(player.getName() + " Disconnected in under 300 milliseconds; he's now banned.");
            }
        }

        data.leaveTime = now;

    }

    private void addReason(StringBuilder builder, String reason, int value) {
        if(builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(reason).append(" ").append(value);
    }

    private void warnOthers(Player player) {
        plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.DARK_RED + " has set off the autoban!");
        plugin.getServer().broadcastMessage(ChatColor.DARK_RED + " Please do not say anything similar to what the user said!");
    }

    private void warnPlayer(Player player) {
        player.sendMessage("[NoPwnage]: " + ChatColor.DARK_RED + "Our system has detected unusual bot activities coming from you.");
        player.sendMessage(ChatColor.DARK_RED + "Please be careful with what you say. DON'T repeat what you just said either, unless you want to be banned.");
    }

    private void banPlayer(Player player, String reason) {
        NoPwnageConfiguration config = plugin.getNPconfig();
        String name = player.getName();
        String ip = player.getAddress().toString().substring(1).split(":")[0];

        for(String command : config.commands) {
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[player]", name).replace("[ip]", ip).replace("[reason]", reason));
            } catch(Exception e) {

            }
        }
    }

    private int minimum(int a, int b, int c) {
        int mi;

        mi = a;
        if(b < mi) {
            mi = b;
        }
        if(c < mi) {
            mi = c;
        }
        return mi;

    }

    private boolean similar(String message1, String message2) {
        return message1 != null && message2 != null && (stringDifference(message1, message2) < 1 + (message1.length() / 10));
    }

    private int stringDifference(String s, String t) {
        int d[][];
        int n;
        int m;
        int i;
        int j;
        char s_i;
        char t_j;
        int cost;

        n = s.length();
        m = t.length();
        if(n == 0) {
            return m;
        }
        if(m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for(i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for(j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        for(i = 1; i <= n; i++) {

            s_i = s.charAt(i - 1);

            for(j = 1; j <= m; j++) {

                t_j = t.charAt(j - 1);

                if(s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                d[i][j] = minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);

            }

        }
        return d[n][m];

    }
}
