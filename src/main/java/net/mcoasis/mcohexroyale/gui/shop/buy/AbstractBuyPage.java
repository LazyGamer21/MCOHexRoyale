package net.mcoasis.mcohexroyale.gui.shop.buy;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.gui.shop.SellPage;
import net.mcoasis.mcohexroyale.util.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractBuyPage extends AbstractGuiPage {

    public AbstractBuyPage(JavaPlugin plugin, boolean fillBorder, boolean buttonsFollowListPages, String parentPageId, boolean autoGenBackButton) {
        super(plugin, fillBorder, buttonsFollowListPages, parentPageId, autoGenBackButton);
    }

    @Override
    protected abstract String getDisplayName(UUID uuid) ;

    @Override
    protected abstract int getRows();

    abstract List<ShopItem> getShopItems(UUID uuid);

    @Override
    protected void assignItems(UUID uuid) {
        for (ShopItem shopItem : getShopItems(uuid)) {

            String name = shopItem.getDisplayName() != null
                    ? shopItem.getDisplayName()
                    : formatMaterialName(shopItem.getItemStack().getType());

            assignItem(uuid, shopItem.getSlot(),
                    new GuiItem(shopItem.getItemStack().clone(), e -> {

                        Player p = (Player) e.getWhoClicked();

                        // Build the full list of items to give
                        List<ItemStack> toGive = new ArrayList<>();

                        ItemStack mainItem = shopItem.getItemStack().clone();
                        shopItem.applyGivenName(mainItem);
                        toGive.add(mainItem);

                        shopItem.getExtraItems().forEach(i -> {
                            ItemStack cloned = i.clone();
                            shopItem.applyGivenName(cloned);
                            toGive.add(cloned);
                        });

                        // Inventory space check
                        if (!hasRoomFor(p, toGive.toArray(new ItemStack[0]))) return;

                        // Money check
                        if (!buyItem(p, shopItem.getCost())) return;

                        // Give everything
                        for (ItemStack item : toGive) {
                            p.getInventory().addItem(item);
                        }

                        p.sendMessage(ChatColor.GREEN +
                                "Purchased " + name + ChatColor.GREEN +
                                " for " + shopItem.getCost() + " coins!");
                    })
                            .setName(ChatColor.GOLD + "Buy " + name)
                            .setLore("Cost: " + shopItem.getCost())
            );
        }

        // wallet viewer
        int coins = SellPage.coinAmounts.getOrDefault(uuid, 0);
        String endWord = coins > 1 || coins == 0 ? " Coins" : " Coin";
        assignItem(uuid, 18, new GuiItem(Material.KELP, e -> {
        }).setName(ChatColor.GOLD + "Wallet: " + coins + endWord));
    }

    @Override
    public abstract String getPageIdentifier();

    @Override
    protected abstract List<GuiItem> getListedButtons(UUID uuid);

    protected String formatMaterialName(Material material) {
        String raw = material.toString().toLowerCase().replace('_', ' ');
        String[] parts = raw.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

    protected boolean buyItem(Player p, int cost) {
        int playerCoins = SellPage.coinAmounts.getOrDefault(p.getUniqueId(), 0);
        if (playerCoins < cost) {
            p.sendMessage(ChatColor.RED + "You don't have enough coins to buy that!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return false;
        }
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        SellPage.coinAmounts.put(p.getUniqueId(), playerCoins - cost);
        GuiManager.getInstance().refreshPages();
        return true;
    }

    protected boolean hasRoomFor(Player p, ItemStack... items) {
        PlayerInventory inv = p.getInventory();

        // Create a temporary map of slots to amounts so we don't modify the real inventory
        int[] tempSlots = new int[36]; // slots 0-35 are main inventory + hotbar

        // Initialize with current amounts
        for (int slot = 0; slot <= 35; slot++) {
            ItemStack current = inv.getItem(slot);
            tempSlots[slot] = (current != null && current.getType() != Material.AIR) ? current.getAmount() : 0;
        }

        // Try to add each item
        for (ItemStack toAdd : items) {
            if (toAdd == null || toAdd.getType() == Material.AIR) continue;

            int remaining = toAdd.getAmount();

            // First try to fill existing stacks of same type
            for (int slot = 0; slot <= 35; slot++) {
                ItemStack current = inv.getItem(slot);

                if (current != null && current.isSimilar(toAdd)) {
                    int space = current.getMaxStackSize() - tempSlots[slot];
                    int added = Math.min(space, remaining);
                    remaining -= added;
                    tempSlots[slot] += added;

                    if (remaining <= 0) break;
                }
            }

            // Then try empty slots
            if (remaining > 0) {
                for (int slot = 0; slot <= 35; slot++) {
                    ItemStack current = inv.getItem(slot);
                    if (current == null || current.getType() == Material.AIR) {
                        int maxStack = toAdd.getMaxStackSize();
                        int added = Math.min(maxStack, remaining);
                        remaining -= added;
                        tempSlots[slot] += added;

                        if (remaining <= 0) break;
                    }
                }
            }

            // If there’s still remaining, player can't hold all items
            if (remaining > 0) {
                p.sendMessage(ChatColor.RED + "Not enough inventory space!");
                return false;
            }
        }

        return true; // All items fit
    }
}
