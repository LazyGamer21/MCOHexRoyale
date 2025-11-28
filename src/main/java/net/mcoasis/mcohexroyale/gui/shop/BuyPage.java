package net.mcoasis.mcohexroyale.gui.shop;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.ShopPage;
import net.mcoasis.mcohexroyale.util.ConfigUtil;
import net.mcoasis.mcohexroyale.util.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuyPage extends AbstractGuiPage {

    public static String pageId = "shop.buy";

    public BuyPage(JavaPlugin plugin) {
        super(plugin, true, false, ShopPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Buy Shop";
    }

    @Override
    protected int getRows() {
        return 5;
    }

    @Override
    protected void assignItems(UUID uuid) {
        List<ShopItem> shopItems = getShopItems();

        for (ShopItem shopItem : shopItems) {

            String name = formatMaterialName(shopItem.getItemStack().getType());

            assignItem(uuid, shopItem.getSlot(),
                    new GuiItem(shopItem.getItemStack(), e -> {

                        Player p = (Player) e.getWhoClicked();

                        // Build the full list of items to give
                        List<ItemStack> toGive = new ArrayList<>();
                        toGive.add(shopItem.getItemStack().clone()); // main item
                        shopItem.getExtraItems().forEach(i -> toGive.add(i.clone()));

                        // Inventory space check
                        if (!hasRoomFor(p, toGive.toArray(new ItemStack[0]))) return;

                        // Money check
                        if (!buyItem(p, shopItem.getCost())) return;

                        // Give everything
                        for (ItemStack item : toGive) {
                            p.getInventory().addItem(item);
                        }

                        p.sendMessage(ChatColor.GREEN +
                                "Purchased " + shopItem.getItemStack().getType() +
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

    private String formatMaterialName(Material material) {
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

    private List<ShopItem> getShopItems() {

        ConfigUtil shopConfigUtil = MCOHexRoyale.getInstance().getShopConfigUtil();
        shopConfigUtil.reload();

        FileConfiguration shopConfig = shopConfigUtil.getConfig();

        return List.of(
                new ShopItem(new ItemStack(Material.IRON_SHOVEL),
                        shopConfig.getInt("buy.IRON_SHOVEL", 150), 11),

                new ShopItem(new ItemStack(Material.IRON_PICKAXE),
                        shopConfig.getInt("buy.IRON_PICKAXE", 250), 20),

                new ShopItem(new ItemStack(Material.IRON_AXE),
                        shopConfig.getInt("buy.IRON_AXE", 500), 29),

                new ShopItem(new ItemStack(Material.COOKED_BEEF, 8),
                        shopConfig.getInt("buy.COOKED_BEEF", 100), 12),

                new ShopItem(new ItemStack(Material.MUSHROOM_STEW),
                        shopConfig.getInt("buy.MUSHROOM_STEW", 250), 21),

                new ShopItem(new ItemStack(Material.GOLDEN_APPLE),
                        shopConfig.getInt("buy.GOLDEN_APPLE", 350), 30),

                // --- PIG BUNDLE ---
                new ShopItem(new ItemStack(Material.CARROT_ON_A_STICK),
                        shopConfig.getInt("buy.CARROT_ON_A_STICK", 250), 13) {{
                    addExtraItems(
                            new ItemStack(Material.SADDLE),
                            new ItemStack(Material.PIG_SPAWN_EGG)
                    );
                }},

                // --- HORSE BUNDLE ---
                new ShopItem(new ItemStack(Material.SADDLE),
                        shopConfig.getInt("buy.SADDLE", 350), 22) {{
                    addExtraItems(
                            new ItemStack(Material.HORSE_SPAWN_EGG)
                    );
                }},

                new ShopItem(new ItemStack(Material.BOW),
                        shopConfig.getInt("buy.BOW", 500), 14),

                new ShopItem(new ItemStack(Material.ARROW, 8),
                        shopConfig.getInt("buy.ARROW", 250), 23),

                new ShopItem(new ItemStack(Material.SHIELD),
                        shopConfig.getInt("buy.SHIELD", 750), 32),

                new ShopItem(new ItemStack(Material.IRON_SWORD),
                        shopConfig.getInt("buy.IRON_SWORD", 250), 15),

                new ShopItem(new ItemStack(Material.DIAMOND_SWORD),
                        shopConfig.getInt("buy.DIAMOND_SWORD", 1000), 24),

                new ShopItem(new ItemStack(Material.BLAST_FURNACE),
                        shopConfig.getInt("buy.BLAST_FURNACE", 15), 33)
        );
    }

    private boolean buyItem(Player p, int cost) {
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

    public boolean hasRoomFor(Player p, ItemStack... items) {
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


    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons(UUID uuid) {
        return List.of();
    }
}
