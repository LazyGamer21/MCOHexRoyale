package net.mcoasis.mcohexroyale.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopItem {

    private final ItemStack itemStack;
    private final int cost;
    private final int slot;
    private final List<ItemStack> extraItems = new ArrayList<>();

    public ShopItem(ItemStack itemStack, int cost, int slot) {
        this.itemStack = itemStack;
        this.cost = cost;
        this.slot = slot;
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
