package net.mcoasis.mcohexroyale.gui;

import me.ericdavis.lazygui.item.*;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GameControlsPage extends AbstractGuiPage {

    public static String pageId = "game_controls";

    public GameControlsPage() {
        super(MCOHexRoyale.getInstance(), true, false, MainPage.pageId, true);
    }

    @Override
    public String getDisplayName() {
        return ChatColor.BLUE + "Game Controls";
    }

    @Override
    protected int getRows() {
        return 6;
    }

    @Override
    protected void assignItems() {
        itemToAssign = new ItemBuilder(Material.GREEN_CONCRETE)
                .setName(ChatColor.GREEN + "Start")
                .build();
        new GuiItem(this, 20, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Starting the Game...");
        });

        itemToAssign = new ItemBuilder(Material.BARRIER)
                .setName(ChatColor.GREEN + "Set Pole")
                .build();
        new GuiItem(this, 30, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Testing...");
            e.getWhoClicked().closeInventory();
            HexManager.getInstance().setPlayerSettingFlag((Player) e.getWhoClicked(), HexManager.getInstance().getHexTile(0, 0), true);
        });

        itemToAssign = new ItemBuilder(Material.BARRIER)
                .setName(ChatColor.GREEN + "Set Flag")
                .build();
        new GuiItem(this, 32, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Testing...");
            e.getWhoClicked().closeInventory();
            HexManager.getInstance().setPlayerSettingFlag((Player) e.getWhoClicked(), HexManager.getInstance().getHexTile(0, 0), false);
        });

        itemToAssign = new ItemBuilder(Material.RED_CONCRETE)
                .setName(ChatColor.GREEN + "Stop")
                .build();
        new GuiItem(this, 24, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Stopping the Game...");
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
