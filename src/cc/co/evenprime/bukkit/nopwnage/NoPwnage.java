package cc.co.evenprime.bukkit.nopwnage;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NoPwnage extends JavaPlugin {

    public boolean enabled = true;

    private NoPwnageConfiguration config;
    private Map<String, PlayerData> playerData;

    private final NoPwnageListener listener = new NoPwnageListener(this);
    private final Logger logs = Logger.getLogger("Minecraft");

    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        this.playerData = new HashMap<String, PlayerData>();

        this.config = new NoPwnageConfiguration(this);
        NoPwnageConfiguration.writeInstructions(this.getDataFolder());

        pm.registerEvents(listener, this);

        for(Player p : this.getServer().getOnlinePlayers()) {
            PlayerData data = new PlayerData();
            playerData.put(p.getName(), data);
            Location l = p.getLocation();
            data.setLocation(l);
        }

        log("NoPwnage has been enabled!");
    }

    public void onDisable() {
        this.playerData = null;

        log("NoPwnage has been disabled!");
    }

    public void log(String message) {
        logs.info("[NoPwnage]: " + message);
    }

    public PlayerData getData(Player player) {
        return getData(player.getName());
    }

    private synchronized PlayerData getData(String playerName) {
        PlayerData data = playerData.get(playerName);
        if(data == null) {
            data = new PlayerData();
            playerData.put(playerName, data);
        }

        return data;
    }

    public NoPwnageConfiguration getNPconfig() {
        return this.config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(commandLabel.equalsIgnoreCase("pwnage")) {
            if(sender.hasPermission(Permissions.ADMIN)) {
                enabled = !enabled;
                if(enabled) {
                    this.config = new NoPwnageConfiguration(this);
                    sender.sendMessage("[NoPwnage] is now " + ChatColor.GREEN + "enabled");
                } else {
                    sender.sendMessage("[NoPwnage] is now " + ChatColor.DARK_RED + "disabled");
                }
            } else {
                sender.sendMessage("[NoPwnage]: " + ChatColor.DARK_RED + "Sorry, you can't do that!");
            }
        }
        return true;
    }
}
