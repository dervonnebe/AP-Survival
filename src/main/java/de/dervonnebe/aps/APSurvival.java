package de.dervonnebe.aps;

import de.dervonnebe.aps.commands.APSurvivalCommand;
import de.dervonnebe.aps.commands.GamemodeCommand;
import de.dervonnebe.aps.commands.MSGCommand;
import de.dervonnebe.aps.commands.TeleportCommand;
import de.dervonnebe.aps.commands.tpa.*;
import de.dervonnebe.aps.events.*;
import de.dervonnebe.aps.utils.ConfigManager;
import de.dervonnebe.aps.utils.Messages;
import de.dervonnebe.aps.utils.PersistentDataManager;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class APSurvival extends JavaPlugin {

    @Getter
    String prefix;
    @Getter
    APSurvival instance;
    @Getter
    PersistentDataManager dataManager;
    @Getter
    Messages messages;
    @Getter
    ConfigManager configManager;
    @Getter
    TPA tpa;

    @Override
    public void onEnable() {
        log("Starting APSurvival...");
        prefix = "§4§lAPS §8▶ §7";
        instance = this;
        messages = new Messages(this);
        dataManager = new PersistentDataManager(this);
        configManager = new ConfigManager(this);
        tpa = new TPA(this);

        registerCommands();
        registerEvents();
        log("APSurvival started!");
    }

    @Override
    public void onDisable() {
        log("Stopping APSurvival...");
        log("APSurvival stopped!","BYE");
    }

    private void registerCommands() {
        log("Registering commands...");

        APSurvivalCommand apSurvivalCommand = new APSurvivalCommand(this);
        getCommand("apsurvival").setExecutor(apSurvivalCommand);
        getCommand("apsurvival").setTabCompleter(apSurvivalCommand);

        GamemodeCommand gamemodeCommand = new GamemodeCommand(this);
        getCommand("gamemode").setExecutor(gamemodeCommand);
        getCommand("gamemode").setTabCompleter(gamemodeCommand);

        TeleportCommand teleportCommand = new TeleportCommand(this);
        getCommand("teleport").setExecutor(teleportCommand);
        getCommand("teleport").setTabCompleter(teleportCommand);

        MSGCommand msgCommand = new MSGCommand(this);
        getCommand("msg").setExecutor(msgCommand);
        getCommand("msg").setTabCompleter(msgCommand);
        getCommand("reply").setExecutor(msgCommand);
        getCommand("r").setExecutor(msgCommand);

        getCommand("tpall").setExecutor(new TPAllCommand(this));
        getCommand("tpaall").setExecutor(new TPAAllCommand(this));
        getCommand("tpahere").setExecutor(new TPAHereCommand(this));
        getCommand("tpacancel").setExecutor(new TPACancelCommand(this));
        getCommand("tpa").setExecutor(new TPACommand(this));
        getCommand("tpaccept").setExecutor(new TPAcceptCommand(this));
        getCommand("tpauto").setExecutor(new TPAAutoCommand(this));


        log("Commands registered!");
    }

    private void registerEvents() {
        log("Registering events...");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitEvent(this), this);
    }

    // Console Logger
    public void log(String message, String... type) {
        String logType = (type != null && type.length > 0 && !type[0].isEmpty()) ? type[0] : "INFO";
        String messageColor = logType.equalsIgnoreCase("ERROR") ? "§4" : logType.equalsIgnoreCase("WARNING") ? "§6" : "§f";
        getServer().getConsoleSender().sendMessage(getPrefix() + "[" + logType + "] " + messageColor + message);
    }
}
