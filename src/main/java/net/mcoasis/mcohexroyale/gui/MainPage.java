package net.mcoasis.mcohexroyale.gui;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.item.ItemBuilder;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class MainPage extends AbstractGuiPage {

    public static String pageId = "main";

    public MainPage() {
        super(MCOHexRoyale.getInstance(), true, true);
    }

    @Override
    protected String getDisplayName() {
        return ChatColor.BOLD + "" + ChatColor.DARK_AQUA +  "Hex Royale";
    }

    @Override
    protected int getRows() {
        return 5;
    }

    @Override
    protected void assignItems() {
        itemToAssign = new ItemBuilder(Material.STONE)
                .setName(ChatColor.GOLD + "Tile Manager").build();
        new GuiItem(this, 20, e -> {
            Player player = (Player) e.getWhoClicked();
            GuiManager.getInstance().openPage(TilesPage.pageId, player);
        });

        itemToAssign = new ItemBuilder(Material.STONE)
                .setName(ChatColor.GOLD + "Teams").build();
        new GuiItem(this, 22, e -> {
            Player player = (Player) e.getWhoClicked();
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Not implemented yet...");
            //GuiManager.getInstance().openPage(TeamsPage.pageId, player);
        });

        itemToAssign = new ItemBuilder(Material.STONE)
                .setName(ChatColor.GOLD + "Game Controls").build();
        new GuiItem(this, 24, e -> {
            Player player = (Player) e.getWhoClicked();
            GuiManager.getInstance().openPage(GameControlsPage.pageId, player);
        });
    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
