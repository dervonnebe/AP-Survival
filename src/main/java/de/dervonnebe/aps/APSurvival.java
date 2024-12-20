package de.dervonnebe.aps;

import de.dervonnebe.aps.commands.*;
import de.dervonnebe.aps.commands.environment.*;
import de.dervonnebe.aps.commands.essential.*;
import de.dervonnebe.aps.commands.server.*;
import de.dervonnebe.aps.commands.tpa.*;
import de.dervonnebe.aps.events.*;
import de.dervonnebe.aps.setup.DatabaseSetup;
import de.dervonnebe.aps.utils.*;
import de.dervonnebe.aps.systems.SitLaySystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public final class APSurvival extends JavaPlugin {
    @Getter
    private Boolean debug = true; // Diese einstellung sollte auf false gesetzt werden, wenn das Plugin auf einem Produktivsystem läuft
    @Getter
    private HashMap<String, URI> serverLinks = new HashMap<>();


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
    @Getter
    LanguageManager languageManager;
    @Getter
    PluginManager pm;
    @Getter
    private LocationManager locationManager;
    @Getter
    private SitLaySystem sitLaySystem;
    @Getter
    private StatusManager statusManager;
    @Getter
    private ChatManager chatManager;

    @Override
    public void onEnable() {
        long now = System.currentTimeMillis();
        instance = this;
        configManager = new ConfigManager(this);
        prefix = configManager.getString("prefix");
        languageManager = new LanguageManager(this, debug, new String[]{"de.yml", "en.yml"});
        messages = new Messages(this);
        log("Starting APSurvival...");
        checkDependencies();
        if (debug) log("Debug mode is enabled!", "WARNING");
        dataManager = new PersistentDataManager(this);
        tpa = new TPA(this);
        databaseManager = new DatabaseManager(this);
        pm = getServer().getPluginManager();

        if (configManager.getBoolean("custom-commands.enabled")) {
            new CustomCommandManager(this);
        }

        loadServerLinks();
        setupDatabase(debug);
        registerCommands();
        registerEvents();
        registerBStats();
        locationManager = new LocationManager(this);

        if (configManager.getBoolean("sit-and-lay.enabled")) {
            sitLaySystem = new SitLaySystem(this);
            log("Sit/Lay System aktiviert!");
        }

        if (configManager.getBoolean("status.enabled")) {
            statusManager = new StatusManager(this);
            log("Status System aktiviert!");
        }

        // PlaceholderAPI Support
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new StatusPlaceholder(this).register();
            log("PlaceholderAPI Support aktiviert!");
        }

        chatManager = new ChatManager(this);

        getCommand("chatclear").setExecutor(new ChatClearCommand(this));
        getCommand("chatclear").setTabCompleter(new ChatClearCommand(this));

        HelpCommand helpCommand = new HelpCommand(this);
        getCommand("help").setExecutor(helpCommand);
        getCommand("help").setTabCompleter(helpCommand);

        log("APSurvival started! in §8" + (System.currentTimeMillis() - now) + "ms");
    }

    @Override
    public void onDisable() {
        long now = System.currentTimeMillis();
        log("Stopping APSurvival...");
        databaseManager.closeConnection();
        if (sitLaySystem != null) {
            sitLaySystem.cleanup();
        }
        log("APSurvival stopped! in §8" + (System.currentTimeMillis() - now) + "ms","BYE");
    }

    private void checkDependencies() {
        log("Checking dependencies...");
        log("No dependencies to check.");
        //log("Dependencies checked!");
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

        FlyCommand flyCommand = new FlyCommand(this);
        getCommand("fly").setExecutor(flyCommand);
        getCommand("fly").setTabCompleter(flyCommand);

        GodModeCommand godModeCommand = new GodModeCommand(this);
        getCommand("godmode").setExecutor(godModeCommand);
        getCommand("godmode").setTabCompleter(godModeCommand);

        RebootCommand rebootCommand = new RebootCommand(this);
        getCommand("reboot").setExecutor(rebootCommand);
        getCommand("reboot").setTabCompleter(rebootCommand);

        BroadcastCommand broadcastCommand = new BroadcastCommand(this);
        getCommand("broadcast").setExecutor(broadcastCommand);

        LanguageCommand languageCommand = new LanguageCommand(this);
        getCommand("language").setExecutor(languageCommand);

        InvseeCommand invseeCommand = new InvseeCommand(this);
        getCommand("invsee").setExecutor(invseeCommand);
        getCommand("invsee").setTabCompleter(invseeCommand);

        SpawnCommand spawnCommand = new SpawnCommand(this);
        getCommand("spawn").setExecutor(spawnCommand);
        getCommand("spawn").setTabCompleter(spawnCommand);

        WarpCommand warpCommand = new WarpCommand(this);
        getCommand("warp").setExecutor(warpCommand);
        getCommand("warp").setTabCompleter(warpCommand);

        SitLayCommand sitLayCommand = new SitLayCommand(this);
        getCommand("sitlay").setExecutor(sitLayCommand);
        getCommand("sitlay").setTabCompleter(sitLayCommand);

        SitCommand sitCommand = new SitCommand(this);
        getCommand("sit").setExecutor(sitCommand);

        LayCommand layCommand = new LayCommand(this);
        getCommand("lay").setExecutor(layCommand);

        StatusCommand statusCommand = new StatusCommand(this);
        getCommand("status").setExecutor(statusCommand);
        getCommand("status").setTabCompleter(statusCommand);

        ChatCommand chatCommand = new ChatCommand(this);
        getCommand("chat").setExecutor(chatCommand);
        getCommand("chat").setTabCompleter(chatCommand);

        log("Commands registered!");
    }

    private void registerEvents() {
        log("Registering events...");
        pm.registerEvents(new JoinQuitEvent(this), this);
        pm.registerEvents(new CommandPreProcessEvent(this), this);
        pm.registerEvents(new CommandTabCompleteEvent(), this);
        pm.registerEvents(new ServerLinksEvent(this), this);
        pm.registerEvents(new InvseeEvent(this), this);

        if (configManager.getBoolean("spawn-fly.enabled")) {
            pm.registerEvents(SpawnFlyEvents.create(this), this);
        }

        log("Events registered!");
    }

    private void registerBStats() {
        log("Registering bStats...");
        if (!configManager.getBoolean("bstats")) {
            log("bStats is disabled in the config. Skipping registration!");
            return;
        }
        Metrics metrics = new Metrics(this, 23707);

        // Optional: Add custom charts
        //metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> ""));
        log("bStats registered!");
    }

    public boolean isVersionGreaterOrEqual(String server, String required) {
        String[] serverParts = server.split("\\.");
        String[] requiredParts = required.split("\\.");

        for (int i = 0; i < Math.max(serverParts.length, requiredParts.length); i++) {
            int serverPart = (i < serverParts.length) ? Integer.parseInt(serverParts[i]) : 0;
            int requiredPart = (i < requiredParts.length) ? Integer.parseInt(requiredParts[i]) : 0;

            if (serverPart > requiredPart) {
                return true;
            } else if (serverPart < requiredPart) {
                return false;
            }
        }
        return true;
    }

    public void loadServerLinks() {
        serverLinks.clear();

        var serverLinksSection = getConfig().getConfigurationSection("serverLinks");
        if (serverLinksSection == null) {
            log("No server links found in configuration.");
            return;
        }

        var keys = serverLinksSection.getKeys(false);
        for (String key : keys) {
            String label = serverLinksSection.getString(key + ".label");
            String url = serverLinksSection.getString(key + ".url");
            try {
                serverLinks.put(label, new URI(url.replace("&", "§")));
            } catch (URISyntaxException e) {
                log("Invalid URI for key " + key + ": " + url, "WARNING");
            }
        }

        log("Loaded " + keys.size() + " server links.");
    }


    // Console Logger
    public void log(String message, String... type) {
        String logType = (type != null && type.length > 0 && !type[0].isEmpty()) ? type[0] : "INFO";
        String messageColor = logType.equalsIgnoreCase("ERROR") ? "§4" : logType.equalsIgnoreCase("WARNING") ? "§6" : "§f";
        getServer().getConsoleSender().sendMessage(getPrefix() + "[" + logType + "] " + messageColor + message);
    }

    public void updatePrefix() {
        this.prefix = configManager.getString("prefix");
    }
}
