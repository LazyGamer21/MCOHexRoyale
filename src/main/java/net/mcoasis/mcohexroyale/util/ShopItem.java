package net.mcoasis.mcohexroyale.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopItem {

    private final ItemStack itemStack;
    private final int cost;
    private final int slot;
    private final List<ItemStack> extraItems = new ArrayList<>();

    private String displayName = null;  // name used on the button
    private String givenName = null;    // name applied to the item given

    public ShopItem(ItemStack itemStack, int cost, int slot) {
        this.itemStack = itemStack;
        this.cost = cost;
        this.slot = slot;
    }

    public ShopItem setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ShopItem setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void applyGivenName(ItemStack stack) {
        if (givenName == null) return;
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(givenName);
        stack.setItemMeta(meta);
    }

    public void addExtraItems(ItemStack... itemsToAdd) {
        extraItems.addAll(Arrays.stream(itemsToAdd).toList());
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getCost() {
        return cost;
    }

    public int getSlot() {
        return slot;
    }

    public List<ItemStack> getExtraItems() {
        return Collections.unmodifiableList(extraItems);
    }

}
