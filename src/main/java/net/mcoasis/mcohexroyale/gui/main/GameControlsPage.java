package net.mcoasis.mcohexroyale.gui.main;

import me.ericdavis.lazygui.item.*;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.managers.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GameControlsPage extends AbstractGuiPage {

    public static String pageId = "main.game-controls";

    public GameControlsPage() {
        super(MCOHexRoyale.getInstance(), true, false, MainPage.pageId, true);
    }

    @Override
    public String getDisplayName(UUID playerId) {
        return ChatColor.BLUE + "Game Controls";
    }

    @Override
    protected int getRows() {
        return 6;
    }

    @Override
    protected void assignItems(UUID playerId) {
        assignItem(playerId, 33, new GuiItem(Material.STONE, e -> {
            MCOHexRoyale.getInstance().restartRunnables();
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Restarted Flag BukkitTask!");
        }).setName(ChatColor.YELLOW + "Restart Tasks")
                .setLore(ChatColor.GRAY + "Restarts BukkitTasks to use new Config values"));

        assignItem(playerId, 29, new GuiItem(Material.STONE, e -> {
            MCOHexRoyale.getInstance().reloadConfig();
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Reloaded Config!");
        }).setName(ChatColor.YELLOW + "Reload Config"));

        assignItem(playerId, 20, new GuiItem(Material.GREEN_CONCRETE, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Starting the Game...");
            GameManager.getInstance().startGame();
        }).setName(ChatColor.GREEN + "Start"));

        assignItem(playerId, 24, new GuiItem(Material.RED_CONCRETE, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Stopping the Game...");
            GameManager.getInstance().endGame(true, false);
        }).setName(ChatColor.RED + "Stop"));

        assignItem(playerId, 40, new GuiItem(Material.BEDROCK, e -> {
            if (e.getWhoClicked() instanceof Player player) {
                GuiManager.getInstance().openPage(ResetTilesPage.pageId, player);
            }
        }).setName(ChatColor.DARK_RED + "RESET ALL TILES"));
    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons(UUID playerId) {
        return null;
    }
}
