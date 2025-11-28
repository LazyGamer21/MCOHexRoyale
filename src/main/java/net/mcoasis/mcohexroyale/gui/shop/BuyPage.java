package net.mcoasis.mcohexroyale.gui.shop;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.gui.ShopPage;
import net.mcoasis.mcohexroyale.util.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
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

                        /*if (p.hasPermission("hexroyale.admin")) {
                            if (e.getAction().)
                        }*/

                        // Build full list of items to give
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
        return List.of(
                new ShopItem(new ItemStack(Material.IRON_SHOVEL), 150, 11),
                new ShopItem(new ItemStack(Material.IRON_PICKAXE), 250, 20),
                new ShopItem(new ItemStack(Material.IRON_AXE), 500, 29),

                new ShopItem(new ItemStack(Material.COOKED_BEEF, 8), 100, 12),
                new ShopItem(new ItemStack(Material.MUSHROOM_STEW), 250, 21),
                new ShopItem(new ItemStack(Material.GOLDEN_APPLE), 350, 30),

                // --- PIG BUNDLE ---
                new ShopItem(new ItemStack(Material.CARROT_ON_A_STICK), 250, 13) {{
                    addExtraItems(
                            new ItemStack(Material.SADDLE),
                            new ItemStack(Material.PIG_SPAWN_EGG)
                    );
                }},

                // --- HORSE BUNDLE ---
                new ShopItem(new ItemStack(Material.SADDLE), 350, 22) {{
                    addExtraItems(
                            new ItemStack(Material.HORSE_SPAWN_EGG)
                    );
                }},

                new ShopItem(new ItemStack(Material.BOW), 500, 14),
                new ShopItem(new ItemStack(Material.ARROW, 8), 250, 23),
                new ShopItem(new ItemStack(Material.SHIELD), 750, 32),

                new ShopItem(new ItemStack(Material.IRON_SWORD), 250, 15),
                new ShopItem(new ItemStack(Material.DIAMOND_SWORD), 1000, 24),
                new ShopItem(new ItemStack(Material.BLAST_FURNACE), 15, 33)
        );
    }

    private void column1(UUID uuid) {
        int shovelCost = 150;
        assignItem(uuid, 11, new GuiItem(Material.IRON_SHOVEL, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.IRON_SHOVEL, 1);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, shovelCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Iron Shovel for " + shovelCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Iron Shovel")
                .setLore("Cost: " + shovelCost));

        int pickaxeCost = 250;
        assignItem(uuid, 20, new GuiItem(Material.IRON_PICKAXE, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.IRON_PICKAXE, 1);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, pickaxeCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Iron Pickaxe for " + pickaxeCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Iron Pickaxe")
                .setLore("Cost: " + pickaxeCost));

        int axeCost = 500;
        assignItem(uuid, 29, new GuiItem(Material.IRON_AXE, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.IRON_AXE, 1);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, axeCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Iron Axe for " + axeCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Iron Axe")
                .setLore("Cost: " + axeCost));
    }

    private void column2(UUID uuid) {
        int steakCost = 100;
        assignItem(uuid, 12, new GuiItem(new ItemStack(Material.COOKED_BEEF, 8), e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.COOKED_BEEF, 8);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, steakCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased 8 Cooked Beef for " + steakCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy 8 Cooked Beef")
                .setLore("Cost: " + steakCost));

        int mushroomStewCost = 250;
        assignItem(uuid, 21, new GuiItem(Material.MUSHROOM_STEW, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.MUSHROOM_STEW);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, mushroomStewCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Mushroom Stew for " + mushroomStewCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Mushroom Stew")
                .setLore("Cost: " + mushroomStewCost));

        int goldenAppleCost = 350;
        assignItem(uuid, 30, new GuiItem(Material.GOLDEN_APPLE, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.GOLDEN_APPLE);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, goldenAppleCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Golden Apple for " + goldenAppleCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Golden Apple")
                .setLore("Cost: " + goldenAppleCost));
    }

    private void column3(UUID uuid) {
        int pigCost = 250;
        assignItem(uuid, 13, new GuiItem(Material.CARROT_ON_A_STICK, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack[] purchasedItems = {
                    new ItemStack(Material.CARROT_ON_A_STICK),
                    new ItemStack(Material.SADDLE),
                    new ItemStack(Material.PIG_SPAWN_EGG)
            };
            // Clone items for inventory check
            ItemStack[] clones = Arrays.stream(purchasedItems)
                    .map(ItemStack::clone)
                    .toArray(ItemStack[]::new);
            if (!hasRoomFor(p, clones)) return;
            if (!buyItem(p, pigCost)) return;
            p.getInventory().addItem(purchasedItems);
            p.sendMessage(ChatColor.GREEN + "Purchased Pig for " + pigCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Pig + Saddle + Carrot on a Stick")
                .setLore("Cost: " + pigCost));

        int horseCost = 350;
        assignItem(uuid, 22, new GuiItem(Material.SADDLE, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack[] purchasedItems = {
                    new ItemStack(Material.HORSE_SPAWN_EGG),
                    new ItemStack(Material.SADDLE)
            };
            // Clone items for inventory check
            ItemStack[] clones = Arrays.stream(purchasedItems)
                    .map(ItemStack::clone)
                    .toArray(ItemStack[]::new);
            if (!hasRoomFor(p, clones)) return;
            if (!buyItem(p, horseCost)) return;
            p.getInventory().addItem(purchasedItems);
            p.sendMessage(ChatColor.GREEN + "Purchased Horse for " + horseCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Horse + Saddle")
                .setLore("Cost: " + horseCost));
    }

    private void column4(UUID uuid) {
        int bowCost = 500;
        assignItem(uuid, 14, new GuiItem(Material.BOW, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.BOW);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, bowCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Bow for " + bowCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Bow")
                .setLore("Cost: " + bowCost, "Arrows not included!"));

        int arrowsCost = 250;
        assignItem(uuid, 23, new GuiItem(new ItemStack(Material.ARROW, 8), e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.ARROW, 8);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, arrowsCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased 8 Arrows for " + arrowsCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy 8 Arrows")
                .setLore("Cost: " + arrowsCost));

        int shieldCost = 750;
        assignItem(uuid, 32, new GuiItem(Material.SHIELD, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.SHIELD);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, shieldCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Shield for " + shieldCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Shield")
                .setLore("Cost: " + shieldCost));
    }

    private void column5(UUID uuid) {
        int ironSwordCost = 250;
        assignItem(uuid, 15, new GuiItem(Material.IRON_SWORD, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.IRON_SWORD);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, ironSwordCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Iron Sword for " + ironSwordCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Iron Sword")
                .setLore("Cost: " + ironSwordCost));

        int diamondSwordCost = 1000;
        assignItem(uuid, 24, new GuiItem(Material.DIAMOND_SWORD, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.DIAMOND_SWORD);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, diamondSwordCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Diamond Sword for " + diamondSwordCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Diamond Sword")
                .setLore("Cost: " + diamondSwordCost));

        int furnaceCost = 15;
        assignItem(uuid, 33, new GuiItem(Material.BLAST_FURNACE, e -> {
            Player p = (Player) e.getWhoClicked();
            ItemStack purchasedItem = new ItemStack(Material.BLAST_FURNACE);
            if (!hasRoomFor(p, purchasedItem.clone())) return;
            if (!buyItem(p, furnaceCost)) return;
            p.getInventory().addItem(purchasedItem);
            p.sendMessage(ChatColor.GREEN + "Purchased Blast Furnace for " + furnaceCost + " coins!");
        }).setName(ChatColor.GOLD + "Buy Blast Furnace")
                .setLore("Cost: " + furnaceCost));
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
