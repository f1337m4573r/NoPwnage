package cc.co.evenprime.bukkit.nopwnage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class NoPwnageConfiguration {

    private static final String WARNPLAYERS = "warnPlayers";
    private static final String WARNOTHERS = "warnOthers";
    private static final String WARNLEVEL = "warnLevel";
    private static final String WARNTIMEOUT = "warnTimeout";
    private static final String BANLEVEL = "banLevel";
    private static final String MOVE_ENABLED = "move.enabled";
    private static final String MOVE_WEIGHT = "move.weight";
    private static final String MOVE_TIMEOUT = "move.timeout";
    private static final String REPEAT_ENABLED = "messageRepeat.enabled";
    private static final String REPEAT_WEIGHT = "playerMessageRepeat.weight";
    private static final String REPEAT_TIMEOUT = "playerMessageRepeat.timeout";
    private static final String SPEED_ENABLED = "messageSpeed.enabled";
    private static final String SPEED_WEIGHT = "messageSpeed.weight";
    private static final String SPEED_TIMEOUT = "messageSpeed.timeout";
    private static final String FIRST_ENABLED = "messageFirst.enabled";
    private static final String FIRST_WEIGHT = "messageFirst.weight";
    private static final String FIRST_TIMEOUT = "messageFirst.timeout";
    private static final String GLOBAL_ENABLED = "globalMessageRepeat.enabled";
    private static final String GLOBAL_WEIGHT = "globalMessageRepeat.weight";
    private static final String GLOBAL_TIMEOUT = "globalMessageRepeat.timeout";
    private static final String BANNED_ENABLED = "bannedMessageRepeat.enabled";
    private static final String BANNED_WEIGHT = "bannedMessageRepeat.weight";
    private static final String BANNED_TIMEOUT = "globalMessageRepeat.timeout";
    private static final String COMMANDS = "commands";

    public final boolean warnPlayers;
    public final boolean warnOthers;
    public final int warnLevel;
    public final long warnTimeout;
    public final int banLevel;

    public final boolean move;
    public final int moveWeight;
    public final long moveTimeout;

    public final boolean speed;
    public final int speedWeight;
    public final long speedTimeout;

    public final boolean first;
    public final int firstWeight;
    public final long firstTimeout;

    public final boolean repeat;
    public final int repeatWeight;
    public final long repeatTimeout;

    public final boolean global;
    public final int globalWeight;
    public final long globalTimeout;

    public final boolean banned;
    public final int bannedWeight;
    public final long bannedTimeout;

    public final String[] commands;

    public NoPwnageConfiguration(Plugin plugin) {

        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        config.options().header("Set the options for the NoPwnage plugin");
        config.addDefault(WARNPLAYERS, true);
        config.addDefault(WARNOTHERS, true);
        this.warnPlayers = config.getBoolean(WARNPLAYERS);
        this.warnOthers = config.getBoolean(WARNOTHERS);

        config.addDefault(WARNLEVEL, 400);
        config.addDefault(WARNTIMEOUT, 30000);
        config.addDefault(BANLEVEL, 800);
        this.warnLevel = config.getInt(WARNLEVEL);
        this.warnTimeout = config.getLong(WARNTIMEOUT);
        this.banLevel = config.getInt(BANLEVEL);

        config.addDefault(MOVE_ENABLED, true);
        config.addDefault(MOVE_WEIGHT, -200);
        config.addDefault(MOVE_TIMEOUT, 30000);
        this.move = config.getBoolean(MOVE_ENABLED);
        this.moveWeight = config.getInt(MOVE_WEIGHT);
        this.moveTimeout = config.getLong(MOVE_TIMEOUT);

        config.addDefault(SPEED_ENABLED, true);
        config.addDefault(SPEED_WEIGHT, 200);
        config.addDefault(SPEED_TIMEOUT, 500);
        this.speed = config.getBoolean(SPEED_ENABLED);
        this.speedWeight = config.getInt(SPEED_WEIGHT);
        this.speedTimeout = config.getLong(SPEED_TIMEOUT);

        config.addDefault(FIRST_ENABLED, true);
        config.addDefault(FIRST_WEIGHT, 200);
        config.addDefault(FIRST_TIMEOUT, 3000);
        this.first = config.getBoolean(FIRST_ENABLED);
        this.firstWeight = config.getInt(FIRST_WEIGHT);
        this.firstTimeout = config.getLong(FIRST_TIMEOUT);

        config.addDefault(REPEAT_ENABLED, true);
        config.addDefault(REPEAT_WEIGHT, 150);
        config.addDefault(REPEAT_TIMEOUT, 5000);
        this.repeat = config.getBoolean(REPEAT_ENABLED);
        this.repeatWeight = config.getInt(REPEAT_WEIGHT);
        this.repeatTimeout = config.getLong(REPEAT_TIMEOUT);

        config.addDefault(GLOBAL_ENABLED, true);
        config.addDefault(GLOBAL_WEIGHT, 100);
        config.addDefault(GLOBAL_TIMEOUT, 5000);
        this.global = config.getBoolean(GLOBAL_ENABLED);
        this.globalWeight = config.getInt(GLOBAL_WEIGHT);
        this.globalTimeout = config.getLong(GLOBAL_TIMEOUT);

        config.addDefault(BANNED_ENABLED, true);
        config.addDefault(BANNED_WEIGHT, 200);
        config.addDefault(BANNED_TIMEOUT, 2000);
        this.banned = config.getBoolean(BANNED_ENABLED);
        this.bannedWeight = config.getInt(BANNED_WEIGHT);
        this.bannedTimeout = config.getLong(BANNED_TIMEOUT);

        config.addDefault(COMMANDS, "kick [player]; ban [player]; ban-ip [ip]");
        this.commands = config.getString(COMMANDS).split(";");

        for(int i = 0; i < this.commands.length; i++) {
            this.commands[i] = this.commands[i].trim();
        }

        config.options().copyDefaults(true);
        plugin.saveConfig();

    }
}
