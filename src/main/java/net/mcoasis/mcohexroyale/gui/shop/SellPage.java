package net.mcoasis.mcohexroyale.gui.shop;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.gui.ShopPage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SellPage extends AbstractGuiPage {

    public static String pageId = "shop.sell";

    public static HashMap<UUID, Integer> coinAmounts = new HashMap<>();

    public SellPage(JavaPlugin plugin) {
        super(plugin, true, false, ShopPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Sell Shop";
    }

    @Override
    protected int getRows() {
        return 5;
    }

    @Override
    protected void assignItems(UUID uuid) {
        setCobblestoneButtons(uuid);
        setCoalButtons(uuid);
        setIronButtons(uuid);
        setGoldButtons(uuid);
        setDiamondButtons(uuid);

        // wallet viewer
        int coins = SellPage.coinAmounts.getOrDefault(uuid, 0);
        String endWord = coins > 1 || coins == 0 ? " Coins" : " Coin";
        assignItem(uuid, 18, new GuiItem(Material.KELP, e -> {
        }).setName(ChatColor.GOLD + "Wallet: " + coins + endWord));
    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons(UUID uuid) {
        return List.of();
    }

    private boolean sellItems(Player p, Material material, int amount, int sellCost) {
        PlayerInventory pInv = p.getInventory();

        int total = 0;
        for (ItemStack item : pInv.getContents()) {
            if (item != null && item.getType() == material) {
                total += item.getAmount();
            }
        }

        if (total < amount) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            p.sendMessage(ChatColor.RED + "You don't have enough of that to sell!");
            return false;
        }

        int remaining = amount;
        for (ItemStack item : pInv.getContents()) {
            if (item != null && item.getType() == material) {
                int stackAmount = item.getAmount();
                if (stackAmount <= remaining) {
                    pInv.remove(item);
                    remaining -= stackAmount;
                } else {
                    item.setAmount(stackAmount - remaining);
                    remaining = 0;
                }

                if (remaining <= 0) break;
            }
        }

        coinAmounts.put(p.getUniqueId(), coinAmounts.getOrDefault(p.getUniqueId(), 0) + (amount * sellCost));
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        p.sendMessage(ChatColor.GREEN + "You sold " + amount + " " + material.name().toLowerCase().replace("_", " ") +
                " for " + (amount * sellCost) + " coins!");
        GuiManager.getInstance().refreshPages();

        return true;
    }


    private void setCobblestoneButtons(UUID uuid) {
        Material material = Material.COBBLESTONE;
        int sellCost = 1;
        String itemName = "Cobblestone";
        int startingSlot = 11;

        setButtons(uuid, material, sellCost, itemName, startingSlot);
    }

    private void setCoalButtons(UUID uuid) {
        Material material = Material.COAL;
        int sellCost = 3;
        String itemName = "Coal";
        int startingSlot = 12;

        setButtons(uuid, material, sellCost, itemName, startingSlot);
    }

    private void setIronButtons(UUID uuid) {
        Material material = Material.IRON_INGOT;
        int sellCost = 30;
        String itemName = "Iron";
        int startingSlot = 13;

        setButtons(uuid, material, sellCost, itemName, startingSlot);
    }

    private void setGoldButtons(UUID uuid) {
        Material material = Material.GOLD_INGOT;
        int sellCost = 60;
        String itemName = "Gold";
        int startingSlot = 14;

        setButtons(uuid, material, sellCost, itemName, startingSlot);
    }

    private void setDiamondButtons(UUID uuid) {
        Material material = Material.DIAMOND;
        int sellCost = 100;
        String itemName = "Diamond";
        int startingSlot = 15;

        setButtons(uuid, material, sellCost, itemName, startingSlot);
    }

    private void setButtons(UUID uuid, Material material, int sellCost, String itemName, int startingSlot) {
        assignItem(uuid, startingSlot, new GuiItem(new ItemStack(material), e -> {
            sellItems((Player) e.getWhoClicked(), material, 1, sellCost);
        }).setName(ChatColor.YELLOW + "Sell 1 " + itemName)
                .setLore("Total Price: " + sellCost));

        assignItem(uuid, startingSlot + 9, new GuiItem(new ItemStack(material, 8), e -> {
            sellItems((Player) e.getWhoClicked(), material, 8, sellCost);
        }).setName(ChatColor.YELLOW + "Sell 8 " + itemName)
                .setLore("Total Price: " + sellCost * 8));

        assignItem(uuid, startingSlot + 18, new GuiItem(new ItemStack(material, 16), e -> {
            sellItems((Player) e.getWhoClicked(), material, 16, sellCost);
        }).setName(ChatColor.YELLOW + "Sell 16 " + itemName)
                .setLore("Total Price: " + sellCost * 16));
    }
}
