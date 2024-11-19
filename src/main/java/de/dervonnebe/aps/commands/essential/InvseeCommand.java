package de.dervonnebe.aps.commands.essential;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.utils.ItemBuilder;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvseeCommand implements CommandExecutor, TabCompleter {
    APSurvival plugin;
    Messages msg;

    public InvseeCommand(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix() + msg.getMessage("command.only-players"));
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.invalid").replace("%command%", "/invsee <player>"));
            return true;
        }
        if (!player.hasPermission("aps.command.invsee")) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "no-perm").replace("%perm%", "aps.command.invsee"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.essential.invsee.target-not-found").replace("%target%", args[0]));
            return true;
        }
        if (target == player) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.essential.invsee.self"));
            return true;
        }
        if (!canAccessInventory(player, target)) {
            player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.essential.invsee.target-not-allowed").replace("%target%", target.getName()));
            return true;
        }

        Inventory gui = Bukkit.createInventory(player, 54, msg.getPlayerMessage(player, "command.essential.invsee.title").replace("%target%", target.getName()));
        gui.setContents(target.getInventory().getContents());
        setupArmorAndOffhandSlots(gui, target);
        setupInventoryBorders(gui);

        player.openInventory(gui);
        player.sendMessage(plugin.getPrefix() + msg.getPlayerMessage(player, "command.essential.invsee.success").replace("%target%", target.getName()));
        return true;
    }

    private void setupArmorAndOffhandSlots(Inventory gui, Player target) {
        gui.setItem(51, createArmorItem(target.getInventory().getBoots(),"Boots" , target.getDisplayName()));
        gui.setItem(50, createArmorItem(target.getInventory().getLeggings(), "Leggings", target.getDisplayName()));
        gui.setItem(48, createArmorItem(target.getInventory().getChestplate(), "Chestplate", target.getDisplayName()));
        gui.setItem(47, createArmorItem(target.getInventory().getHelmet(), "Helmet", target.getDisplayName()));
        gui.setItem(49, createArmorItem(target.getInventory().getItemInOffHand(), "Offhand", target.getDisplayName()));
    }

    private ItemStack createArmorItem(ItemStack item, String name, String playerName) {
        if (item == null || item.getType() == Material.AIR) {
            item = new ItemStack(Material.BARRIER);
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);
        meta.setLore(Arrays.asList(
                ChatColor.BLUE + "This slot belongs to " + playerName + "'s " + name.toLowerCase() + ".",
                ChatColor.BLUE + "Right-click to add an item here."
        ));
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }

    private void setupInventoryBorders(Inventory gui) {
        ItemStack backgroundPane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(" ")
                .setHideTooltip(true)
                .build();

        int[] backgroundSlots = {36, 37, 45, 46, 43, 44, 52, 53,};

        for (int slot : backgroundSlots) {
            gui.setItem(slot, backgroundPane);
        }
    }

    private boolean canAccessInventory(Player viewer, Player target) {
        int viewerLevel = getPermissionLevel(viewer, "command.essential.invsee.prevent.");
        int targetLevel = getPermissionLevel(target, "command.essential.invsee.prevent.");

        return viewerLevel >= targetLevel;
    }

    private int getPermissionLevel(Player player, String permissionPrefix) {
        int highestLevel = 0;
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            String permName = perm.getPermission();
            if (permName.startsWith(permissionPrefix)) {
                try {
                    int level = Integer.parseInt(permName.substring(permissionPrefix.length()));
                    highestLevel = Math.max(highestLevel, level);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return highestLevel;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        if (!commandSender.hasPermission("aps.command.invsee")) {
            return null;
        }
        return List.of();
    }}
