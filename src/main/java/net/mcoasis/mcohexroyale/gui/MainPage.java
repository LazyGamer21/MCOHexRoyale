package net.mcoasis.mcohexroyale.gui;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.main.GameControlsPage;
import net.mcoasis.mcohexroyale.gui.main.TeamsPage;
import net.mcoasis.mcohexroyale.gui.main.TilesPage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

public class MainPage extends AbstractGuiPage {

    public static String pageId = "main";

    public MainPage() {
        super(MCOHexRoyale.getInstance(), true, false);
    }

    @Override
    protected String getDisplayName(UUID playerId) {
        return ChatColor.BOLD + "" + ChatColor.DARK_AQUA +  "Hex Royale";
    }

    @Override
    protected int getRows() {
        return 5;
    }

    @Override
    protected void assignItems(UUID playerId) {
        assignItem(playerId, 20, new GuiItem(Material.STONE, e -> {
            Player player = (Player) e.getWhoClicked();
            GuiManager.getInstance().openPage(TilesPage.pageId, player);
        }).setName(ChatColor.GOLD + "Tile Manager"));

        assignItem(playerId, 22, new GuiItem(Material.STONE, e -> {
            Player player = (Player) e.getWhoClicked();
            GuiManager.getInstance().openPage(TeamsPage.pageId, player);
        }).setName(ChatColor.GOLD + "Teams"));

        assignItem(playerId, 24, new GuiItem(Material.STONE, e -> {
            Player player = (Player) e.getWhoClicked();
            GuiManager.getInstance().openPage(GameControlsPage.pageId, player);
        }).setName(ChatColor.GOLD + "Game Controls"));
    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons() {
        return null;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
