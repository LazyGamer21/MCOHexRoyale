package net.mcoasis.mcohexroyale.gui;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.gui.shop.BuyPage;
import net.mcoasis.mcohexroyale.gui.shop.SellPage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ShopPage extends AbstractGuiPage {

    public static String pageId = "shop";


    public ShopPage(JavaPlugin plugin) {
        super(plugin, true, false);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Hex Royale Shop";
    }

    @Override
    protected int getRows() {
        return 5;
    }

    @Override
    protected void assignItems(UUID uuid) {
        assignItem(uuid, 21, new GuiItem(Material.STONE, e -> {
            Player player = (Player) e.getWhoClicked();
            GuiManager.getInstance().openPage(BuyPage.pageId, player);
        }).setName(ChatColor.GOLD + "Buy")
                .setLore("Purchased Items are Lost on Death!"));

        assignItem(uuid, 23, new GuiItem(Material.STONE, e -> {
            Player player = (Player) e.getWhoClicked();
            GuiManager.getInstance().openPage(SellPage.pageId, player);
        }).setName(ChatColor.GOLD + "Sell"));

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
}
