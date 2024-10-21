package de.dervonnebe.aps;

import de.dervonnebe.aps.commands.APSurvivalCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class APSurvival extends JavaPlugin {

    @Getter
    String prefix = "§4§lAPS §8▶ §7";
    @Getter
    APSurvival instance;

    @Override
    public void onEnable() {
        log("Starting APSurvival...");
        instance = this;
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

        log("Commands registered!");
    }

    private void registerEvents() {
        log("Registering events...");

    }


    // Console Logger
    public void log(String message, String... type) {
        String logType = (type != null && type.length > 0 && !type[0].isEmpty()) ? type[0] : "INFO";
        String messageColor = logType.equalsIgnoreCase("ERROR") ? "§4" : logType.equalsIgnoreCase("WARNING") ? "§6" : "§f";
        getServer().getConsoleSender().sendMessage(getPrefix() + "[" + logType + "] " + messageColor + message);
    }
}
