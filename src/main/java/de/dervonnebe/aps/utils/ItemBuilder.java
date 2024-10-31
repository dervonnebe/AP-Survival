package de.dervonnebe.aps.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        setMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        setMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        setMeta();
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setDamage(int damange) {
        ((Damageable) itemMeta).setDamage(damange);

        return this;
    }

    public ItemBuilder setDurability(int durability) {
        ((Damageable) itemMeta).setDamage(itemStack.getType().getMaxDurability() - durability);
        return this;
    }

    public ItemBuilder setFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        itemMeta.setDisplayName(color(displayName));
        return this;
    }

    public ItemBuilder removeDisplayName() {
        itemMeta.setDisplayName(" ");
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        itemStack.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        List<String> lores = new ArrayList<>();
        for (String string : lore) {
            lores.add(color(string));
        }
        itemMeta.setLore(lores);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        List<String> lores = new ArrayList<>();
        for (String string : lore) {
            lores.add(color(string));
        }
        itemMeta.setLore(lores);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        if (lore != null)
            lore.add(color(line));
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder setHeadOwner(Player player) {
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwningPlayer(player);
        itemStack.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder setPersistentData(JavaPlugin plugin, String key, PersistentDataType dataType, Object value) {
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), dataType, value);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        itemMeta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder setHideTooltip(Boolean enabled) {
        itemMeta.setHideTooltip(enabled);
        return this;
    }

    public ItemBuilder setEnchantmentGlow(Boolean enabled) {
        itemMeta.setEnchantmentGlintOverride(enabled);
        return this;
    }

    public ItemBuilder setItemFlag(ItemFlag flag) {
        itemMeta.addItemFlags(flag);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private void setMeta() {
        this.itemMeta = this.itemStack.getItemMeta();
    }
}
