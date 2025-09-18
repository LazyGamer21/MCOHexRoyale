package net.mcoasis.mcohexroyale.gui.main;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ResetTilesPage extends AbstractGuiPage {

    public static String pageId = "main.game-controls.reset-tiles";

    public ResetTilesPage() {
        super(MCOHexRoyale.getInstance(), true, false, GameControlsPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID playerId) {
        return ChatColor.DARK_RED + "RESET ALL TILES";
    }

    @Override
    protected int getRows() {
        return 3;
    }

    @Override
    protected void assignItems(UUID playerId) {
        assignItem(playerId, 13, new GuiItem(Material.BEDROCK, e -> {
            HexManager.getInstance().resetAllTiles();
        }).setName(ChatColor.DARK_RED + "RESET ALL TILES")
                .setLore("Are you SURE you wish to reset all tiles?", "Doing so will set all tiles back to their default state"));
    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons() {
        return List.of();
    }
}
