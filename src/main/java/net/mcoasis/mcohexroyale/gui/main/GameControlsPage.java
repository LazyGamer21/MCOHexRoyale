package net.mcoasis.mcohexroyale.gui.main;

import me.ericdavis.lazygui.item.*;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.MainPage;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class GameControlsPage extends AbstractGuiPage {

    public static String pageId = "main.game-controls";

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
    protected void assignItems(UUID playerId) {
        assignItem(playerId, 20, new GuiItem(Material.GREEN_CONCRETE, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Starting the Game...");
        }).setName(ChatColor.GREEN + "Start"));

        assignItem(playerId, 24, new GuiItem(Material.RED_CONCRETE, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Stopping the Game...");
        }).setName(ChatColor.RED + "Stop"));
    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons() {
        return null;
    }
}
