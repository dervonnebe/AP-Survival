package de.dervonnebe.aps.commands;

import de.dervonnebe.aps.APSurvival;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class HelpCommand implements CommandExecutor, TabCompleter {
    private final APSurvival plugin;
    private final Map<String, CommandCategory> categories;

    public HelpCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.categories = new LinkedHashMap<>();
        initializeCategories();
    }

    private void initializeCategories() {
        // Teleportation Kategorie
        categories.put("teleport", new CommandCategory(
            new LocalizedString("Teleportation", "Teleportation"),
            Arrays.asList(
                new CommandInfo("tp", 
                    new LocalizedString("Teleportiere dich zu einem Spieler oder Koordinaten", 
                                      "Teleport to a player or coordinates"),
                    "/tp <spieler|x y z>"),
                new CommandInfo("tpa", 
                    new LocalizedString("Sende eine Teleport-Anfrage", 
                                      "Send a teleport request"),
                    "/tpa <spieler>"),
                new CommandInfo("tpahere", 
                    new LocalizedString("Bitte einen Spieler, sich zu dir zu teleportieren", 
                                      "Request a player to teleport to you"),
                    "/tpahere <spieler>")
            )
        ));

        // Chat Kategorie
        categories.put("chat", new CommandCategory(
            new LocalizedString("Chat", "Chat"),
            Arrays.asList(
                new CommandInfo("msg", 
                    new LocalizedString("Sende private Nachrichten", 
                                      "Send private messages"),
                    "/msg <spieler> <nachricht>"),
                new CommandInfo("reply", 
                    new LocalizedString("Antworte auf die letzte private Nachricht", 
                                      "Reply to the last private message"),
                    "/reply <nachricht>"),
                new CommandInfo("chatclear", 
                    new LocalizedString("Leere den Chat", 
                                      "Clear the chat"),
                    "/chatclear [-l <sprache>] [-t <sekunden>]")
            )
        ));

        // Essentials Kategorie
        categories.put("essentials", new CommandCategory(
            new LocalizedString("Essentials", "Essentials"),
            Arrays.asList(
                new CommandInfo("gamemode", 
                    new LocalizedString("Ändere den Spielmodus", 
                                      "Change gamemode"),
                    "/gamemode <mode> [spieler]"),
                new CommandInfo("invsee", 
                    new LocalizedString("Schaue in das Inventar eines Spielers", 
                                      "View a player's inventory"),
                    "/invsee <spieler>"),
                new CommandInfo("sit", 
                    new LocalizedString("Setze dich hin", 
                                      "Sit down"),
                    "/sit"),
                new CommandInfo("lay", 
                    new LocalizedString("Lege dich hin", 
                                      "Lay down"),
                    "/lay")
            )
        ));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            showMainMenu(sender);
            return true;
        }

        String category = args[0].toLowerCase();
        if (categories.containsKey(category)) {
            if (args.length == 1) {
                showCategory(sender, category);
            } else {
                showCommandHelp(sender, category, args[1]);
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + "§cUnbekannte Kategorie! Nutze /help für eine Übersicht.");
        }

        return true;
    }

    private void showMainMenu(CommandSender sender) {
        sender.sendMessage(Component.text("\n".repeat(20)));
        
        String language = getPlayerLanguage(sender);
        
        TextComponent.Builder message = Component.text()
            .append(Component.text("=== " + 
                (language.equals("de") ? "Hilfe-Menü" : "Help Menu") + 
                " ===", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.newline());

        for (Map.Entry<String, CommandCategory> entry : categories.entrySet()) {
            message.append(Component.newline())
                .append(Component.text("➤ ", NamedTextColor.GRAY))
                .append(Component.text(entry.getValue().getName(language), NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.runCommand("/help " + entry.getKey()))
                    .hoverEvent(HoverEvent.showText(Component.text(
                        language.equals("de") ? 
                        "Klicke für mehr Infos zu " + entry.getValue().getName(language) :
                        "Click for more info about " + entry.getValue().getName(language)))));
        }

        sender.sendMessage(message.build());
    }

    private void showCategory(CommandSender sender, String category) {
        CommandCategory cat = categories.get(category);
        if (cat == null) return;

        sender.sendMessage(Component.text("\n".repeat(20)));
        
        String language = getPlayerLanguage(sender);
        
        TextComponent.Builder message = Component.text()
            .append(Component.text("=== " + cat.getName(language) + " ===", 
                NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.newline());

        for (CommandInfo cmd : cat.getCommands()) {
            message.append(Component.newline())
                .append(Component.text("• ", NamedTextColor.GRAY))
                .append(Component.text(cmd.getName(), NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.suggestCommand("/" + cmd.getName()))
                    .hoverEvent(HoverEvent.showText(Component.text(
                        cmd.getDescription(language) + "\n" +
                        (language.equals("de") ? "Verwendung: " : "Usage: ") + 
                        cmd.getUsage()))));
        }

        message.append(Component.newline())
            .append(Component.newline())
            .append(Component.text(language.equals("de") ? "« Zurück" : "« Back", 
                NamedTextColor.RED)
                .clickEvent(ClickEvent.runCommand("/help"))
                .hoverEvent(HoverEvent.showText(Component.text(
                    language.equals("de") ? 
                    "Zurück zum Hauptmenü" : 
                    "Back to main menu"))));

        sender.sendMessage(message.build());
    }

    private void showCommandHelp(CommandSender sender, String category, String commandName) {
        CommandCategory cat = categories.get(category);
        if (cat == null) return;

        Optional<CommandInfo> cmdInfo = cat.getCommands().stream()
            .filter(cmd -> cmd.getName().equalsIgnoreCase(commandName))
            .findFirst();

        if (cmdInfo.isEmpty()) {
            sender.sendMessage(plugin.getPrefix() + "§cBefehl nicht gefunden!");
            return;
        }

        sender.sendMessage(Component.text("\n".repeat(10)));
        
        CommandInfo cmd = cmdInfo.get();
        TextComponent.Builder message = Component.text()
            .append(Component.text("=== Befehl: " + cmd.getName() + " ===", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.newline());

        message.append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Beschreibung: ", NamedTextColor.YELLOW))
            .append(Component.text(cmd.getDescription(getPlayerLanguage(sender)), NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("Verwendung: ", NamedTextColor.YELLOW))
            .append(Component.text(cmd.getUsage(), NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("« Zurück", NamedTextColor.RED)
                .clickEvent(ClickEvent.runCommand("/help " + category))
                .hoverEvent(HoverEvent.showText(Component.text("Zurück zur Kategorie"))));

        sender.sendMessage(message.build());
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return categories.keySet().stream()
                .filter(cat -> cat.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            CommandCategory category = categories.get(args[0].toLowerCase());
            if (category != null) {
                return category.getCommands().stream()
                    .map(CommandInfo::getName)
                    .filter(cmd -> cmd.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }

    private String getPlayerLanguage(CommandSender sender) {
        if (sender instanceof Player) {
            String lang = plugin.getDataManager().getStringData((Player)sender, "language");
            return lang != null ? lang : "en";
        }
        return "en";
    }

    private static class LocalizedString {
        private final String de;
        private final String en;

        public LocalizedString(String de, String en) {
            this.de = de;
            this.en = en;
        }

        public String get(String language) {
            return language.equals("de") ? de : en;
        }
    }

    private static class CommandCategory {
        private final LocalizedString name;
        private final List<CommandInfo> commands;

        public CommandCategory(LocalizedString name, List<CommandInfo> commands) {
            this.name = name;
            this.commands = commands;
        }

        public String getName(String language) {
            return name.get(language);
        }

        public List<CommandInfo> getCommands() {
            return commands;
        }
    }

    private static class CommandInfo {
        private final String name;
        private final LocalizedString description;
        private final String usage;

        public CommandInfo(String name, LocalizedString description, String usage) {
            this.name = name;
            this.description = description;
            this.usage = usage;
        }

        public String getName() {
            return name;
        }

        public String getDescription(String language) {
            return description.get(language);
        }

        public String getUsage() {
            return usage;
        }
    }
} 