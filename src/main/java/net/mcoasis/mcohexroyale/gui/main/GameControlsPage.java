package net.mcoasis.mcohexroyale.gui.main;

import me.ericdavis.lazygui.item.*;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.items.CoinPouch;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.util.GameWorldMapRenderer;
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
        assignItem(playerId, 20, new GuiItem(Material.GREEN_CONCRETE, e -> {
            if (!gameCanStart()) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Unable to Start Game - Must have at least 2 teams!");
                return;
            }
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "Starting the Game...");
            GameManager.getInstance().startGame();
        }).setName(ChatColor.GREEN + "Start"));

        assignItem(playerId, 24, new GuiItem(Material.RED_CONCRETE, e -> {
            e.getWhoClicked().sendMessage(ChatColor.RED + "Stopping the Game...");
            GameManager.getInstance().endGame(true, false);
        }).setName(ChatColor.RED + "Stop"));

        assignItem(playerId, 31, new GuiItem(Material.RED_CONCRETE, e -> {
            e.getWhoClicked().sendMessage(ChatColor.GRAY + "Test");
            e.getWhoClicked().getInventory().addItem(CoinPouch.of(100));
            e.getWhoClicked().getInventory().addItem(CoinPouch.of(250));
        }).setName(ChatColor.RED + "Test"));
    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons(UUID playerId) {
        return null;
    }

    /***
     *
     * @return True if at least 2 {@link HexTeam}s have at least 1 person in them (so game won't immediately end)
     */
    private boolean gameCanStart() {
        HexTeam teamWithPeople = null;
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            if (team.getMembersAlive().isEmpty()) continue;
            if (teamWithPeople != null) return true;
            teamWithPeople = team;
        }
        return false;
    }
}
