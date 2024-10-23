package de.dervonnebe.aps.commands.tpa;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class TPACommand implements CommandExecutor {

    private final APSurvival plugin;
    private final Messages msg;
    private TPA tpa;

    public TPACommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
        this.tpa = plugin.getTpa();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMessage("command.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("aps.command.tpa")) {
            player.sendMessage(msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.tpa"));
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(msg.getPlayerMessage(player, "command.player-not-found"));
                return true;
            }

            if (!target.hasPermission("aps.command.tpa.receive")) {
                player.sendMessage(msg.getPlayerMessage(player, "command.tpa.cannot-receive"));
                return true;
            }

            if (plugin.getDataManager().getBooleanData(target, "tpauto")) {
                player.sendMessage(msg.getPlayerMessage(player, "command.tpauto.sucsess"));
                player.teleport(target);
                return true;
            }

            Component requestMessage = Component.text(msg.getPlayerMessage(target, "command.tpa.request").replace("%player%", player.getName()));

            Component acceptButton = Component.text(msg.getPlayerMessage(target, "button.accept"))
                    .color(net.kyori.adventure.text.format.NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.runCommand("/tpaccept " + player.getName()))
                    .hoverEvent(HoverEvent.showText(Component.text("Klicke, um zu akzeptieren")));

            Component declineButton = Component.text(msg.getPlayerMessage(target, "button.decline"))
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED)
                    .clickEvent(ClickEvent.runCommand("/tpacancel " + player.getName()))
                    .hoverEvent(HoverEvent.showText(Component.text("Klicke, um abzulehnen")));

            requestMessage = requestMessage
                    .replaceText(Pattern.compile("%btn-accept%"), builder -> acceptButton)
                    .replaceText(Pattern.compile("%btn-decline%"), builder -> declineButton);

            target.sendMessage(requestMessage);

            target.sendMessage(msg.getPlayerMessage(target, "command.tpa.request")
                    .replace("%player%", player.getName()));

            player.sendMessage(msg.getPlayerMessage(player, "command.tpa.sent")
                    .replace("%target%", target.getName()));

            tpa.addTeleportRequest(player, target);
        } else {
            player.sendMessage(msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/tpa <player>"));
        }
        return true;
    }
}