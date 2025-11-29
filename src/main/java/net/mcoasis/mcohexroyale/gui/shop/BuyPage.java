package net.mcoasis.mcohexroyale.gui.shop;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.ShopPage;
import net.mcoasis.mcohexroyale.gui.shop.buy.FoodPage;
import net.mcoasis.mcohexroyale.gui.shop.buy.OtherPage;
import net.mcoasis.mcohexroyale.gui.shop.buy.WeaponsPage;
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
        // Weapons + Tools
        setShopButton(uuid, WeaponsPage.pageId, 20, Material.IRON_SWORD, "Weapons + Tools");

        // Food
        setShopButton(uuid, FoodPage.pageId, 21, Material.COOKED_BEEF, "Food");

        // Other
        setShopButton(uuid, OtherPage.pageId, 22, Material.BLAST_FURNACE, "Other");

        // wallet viewer
        int coins = SellPage.coinAmounts.getOrDefault(uuid, 0);
        String endWord = coins > 1 || coins == 0 ? " Coins" : " Coin";
        assignItem(uuid, 18, new GuiItem(Material.KELP, e -> {
        }).setName(ChatColor.GOLD + "Wallet: " + coins + endWord));
    }

    private void setShopButton(UUID uuid, String pageId, int slot, Material buttonMaterial, String name) {
        assignItem(uuid, slot, new GuiItem(buttonMaterial, e -> {
            Player player = (Player) e.getWhoClicked();
            GuiManager.getInstance().openPage(pageId, player);
        }).setName(ChatColor.GOLD + name));
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
