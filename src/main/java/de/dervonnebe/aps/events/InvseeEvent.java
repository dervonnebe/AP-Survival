package de.dervonnebe.aps.events;

import de.dervonnebe.aps.APSurvival;
import de.dervonnebe.aps.commands.essential.InvseeCommand;
import de.dervonnebe.aps.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class InvseeEvent implements Listener {
    APSurvival plugin;
    Messages msg;

    public InvseeEvent(APSurvival plugin) {
        this.plugin = plugin;
        this.msg = plugin.getMessages();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        String title = event.getView().getTitle();

        // Überprüfen, ob das geklickte Inventar das Invsee-Inventar ist
        if (clickedInventory != null && title.startsWith("Inventory of: ")) {
            String targetName = title.substring("Inventory of: ".length());
            Player target = Bukkit.getPlayer(targetName);

            if (target != null && clickedInventory.equals(event.getView().getTopInventory())) {
                // Änderungen live im Zielinventar des Spielers übernehmen
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ItemStack[] liveContents = event.getView().getTopInventory().getContents();

                    // Setzt nur die ersten 36 Slots des Hauptinventars
                    ItemStack[] targetContents = new ItemStack[target.getInventory().getSize()];
                    System.arraycopy(liveContents, 0, targetContents, 0, Math.min(targetContents.length, 36));

                    target.getInventory().setContents(targetContents);
                }, 1L); // Verzögerung von 1 Tick, um das Inventar stabil zu aktualisieren
            }
        }
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity viewer = event.getPlayer();
        Inventory closedInventory = event.getInventory();

        // Überprüfen, ob das geschlossene Inventar das Invsee-Inventar ist
        String title = event.getView().getTitle();
        if (closedInventory != null && title.startsWith("Inventory of: ")) {
            String targetName = title.substring("Inventory of: ".length());
            Player target = Bukkit.getPlayer(targetName);

            if (target != null) {
                // Änderungen im Inventar speichern
                ItemStack[] updatedContents = closedInventory.getContents();
                target.getInventory().setContents(updatedContents);
                viewer.sendMessage(ChatColor.GREEN + "Inventory changes saved for " + target.getName() + ".");
            } else {
                viewer.sendMessage(ChatColor.RED + "Target player not found. Inventory changes were not saved.");
            }
        }
    }

}