package de.dervonnebe.aps;

import de.dervonnebe.aps.commands.*;
import de.dervonnebe.aps.commands.environment.*;
import de.dervonnebe.aps.commands.essential.*;
import de.dervonnebe.aps.commands.tpa.*;
import de.dervonnebe.aps.events.*;
import de.dervonnebe.aps.setup.DatabaseSetup;
import de.dervonnebe.aps.utils.*;
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
    @Getter
    DatabaseManager databaseManager;
    @Getter
    DatabaseSetup databaseSetup;

    @Override
    public void onEnable() {
        log("Starting APSurvival...");
        instance = this;
        messages = new Messages(this);
        dataManager = new PersistentDataManager(this);
        configManager = new ConfigManager(this);
        tpa = new TPA(this);
        databaseManager = new DatabaseManager(this);
        prefix = configManager.getString("prefix");

        setupDatabase(true);
        registerCommands();
        registerEvents();
        registerBStats();
        log("APSurvival started!");
    }

    @Override
    public void onDisable() {
        log("Stopping APSurvival...");
        databaseManager.closeConnection();
        log("APSurvival stopped!","BYE");
    }

    private void setupDatabase(Boolean rebuild) {
        log("Setting up database...");
        databaseSetup = new DatabaseSetup(this);
        databaseSetup.setupTables(rebuild);
        log("Database setup complete!");
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

        SunCommand sunCommand = new SunCommand(this);
        getCommand("sun").setExecutor(sunCommand);

        RainCommand rainCommand = new RainCommand(this);
        getCommand("rain").setExecutor(rainCommand);

        ThunderCommand thunderCommand = new ThunderCommand(this);
        getCommand("thunder").setExecutor(thunderCommand);

        DayCommand dayCommand = new DayCommand(this);
        getCommand("day").setExecutor(dayCommand);

        NightCommand nightCommand = new NightCommand(this);
        getCommand("night").setExecutor(nightCommand);

        SunriseCommand sunriseCommand = new SunriseCommand(this);
        getCommand("sunrise").setExecutor(sunriseCommand);

        SunsetCommand sunsetCommand = new SunsetCommand(this);
        getCommand("sunset").setExecutor(sunsetCommand);

        WeatherCommand weatherCommand = new WeatherCommand(this);
        getCommand("weather").setExecutor(weatherCommand);
        getCommand("weather").setTabCompleter(weatherCommand);

        TimeCommand timeCommand = new TimeCommand(this);
        getCommand("time").setExecutor(timeCommand);
        getCommand("time").setTabCompleter(timeCommand);

        HealCommand healCommand = new HealCommand(this);
        getCommand("heal").setExecutor(healCommand);
        getCommand("heal").setTabCompleter(healCommand);

        FeedCommand feedCommand = new FeedCommand(this);
        getCommand("feed").setExecutor(feedCommand);
        getCommand("feed").setTabCompleter(feedCommand);


        log("Commands registered!");
    }

    private void registerEvents() {
        log("Registering events...");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitEvent(this), this);
        pm.registerEvents(new CommandPreProcessEvent(this), this);
        pm.registerEvents(new CommandTabCompleteEvent(), this);

        log("Events registered!");
    }

    private void registerBStats() {
        log("Registering bStats...");
        if (configManager.getBoolean("bstats")) {
            log("bStats is disabled in the config. Skipping registration!");
            return;
        }
        Metrics metrics = new Metrics(this, 23707);

        // Optional: Add custom charts
        //metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> ""));
        log("bStats registered!");
    }

    // Console Logger
    public void log(String message, String... type) {
        String logType = (type != null && type.length > 0 && !type[0].isEmpty()) ? type[0] : "INFO";
        String messageColor = logType.equalsIgnoreCase("ERROR") ? "§4" : logType.equalsIgnoreCase("WARNING") ? "§6" : "§f";
        getServer().getConsoleSender().sendMessage(getPrefix() + "[" + logType + "] " + messageColor + message);
    }
}
