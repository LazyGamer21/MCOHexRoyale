package net.mcoasis.mcohexroyale.gui;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.item.ItemBuilder;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TilesPage extends AbstractGuiPage {

    public static String pageId = "tiles";

    int slot;

    public TilesPage() {
        super(MCOHexRoyale.getInstance(), false, true, MainPage.pageId, true);
    }

    @Override
    protected String getDisplayName() {
        return ChatColor.BLUE + "Hex Tiles";
    }

    @Override
    protected int getRows() {
        return 6;
    }

    @Override
    protected void assignItems() {
        slot = -2;

        //* row 1
        setFlagButton(-1, -2);
        setFlagButton(0, -2);
        setFlagButton(1, -2);
        setFlagButton(2, -2);
        setFlagButton(3, -2);

        //* row 2
        setFlagButton(-1, -1);
        setFlagButton(0, -1);
        setFlagButton(1, -1);
        setFlagButton(2, -1);

        //* row 3
        setFlagButton(-2, 0);
        setFlagButton(-1, 0);
        setFlagButton(0, 0);
        setFlagButton(1, 0);
        setFlagButton(2, 0);

        //* row 4
        setFlagButton(-2, 1);
        setFlagButton(-1, 1);
        setFlagButton(0, 1);
        setFlagButton(1, 1);

        //* row 5
        setFlagButton(-3, 2);
        setFlagButton(-2, 2);
        setFlagButton(-1, 2);
        setFlagButton(0, 2);
        setFlagButton(1, 2);
    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    private void setFlagButton(int q, int r) {
        slot += 2;

        Material material;
        HexTile tile = HexManager.getInstance().getHexTile(q, r);

        if (tile == null) {
            Bukkit.getLogger().warning("[MCOHexRoyale] TilesPage -- Could not retrieve tile (" + q + ", " + r + ")");
            return;
        }

        if (tile.getCurrentTeam() != null) {
            HexTeam.TeamColor teamColor = tile.getCurrentTeam().getTeamColor();

            if (!tile.isCurrentTeamOwns()) {

                material = Material.WHITE_WOOL;

            } else {

                if (teamColor.equals(HexTeam.TeamColor.RED)) material = Material.RED_WOOL;
                else if (teamColor.equals(HexTeam.TeamColor.BLUE)) material = Material.BLUE_WOOL;
                else if (teamColor.equals(HexTeam.TeamColor.GREEN)) material = Material.GREEN_WOOL;
                else if (teamColor.equals(HexTeam.TeamColor.YELLOW)) material = Material.YELLOW_WOOL;
                else  {
                    material = Material.WHITE_WOOL;
                }

            }

        } else {

            material = Material.WHITE_WOOL;
        }

        String color;
        String loreColor = tile.getCurrentTeam() == null ? ChatColor.GRAY + "" : tile.getCurrentTeam().getTeamColor().getColor();
        if (tile.getCurrentTeam() == null || !tile.isCurrentTeamOwns()) color = ChatColor.GRAY + "";
        else color = tile.getCurrentTeam().getTeamColor().getColor();

        itemToAssign = new ItemBuilder(material)
                .setName(color + q + ", " + r)
                //! tile.getCapturingPlayersAmount() does not update if nobody is in the circle so it stays 1, make it update for this
                .setLore(loreColor + tile.getCapturingPlayersAmount() + " : " + String.format("%.2f", tile.getCapturePercentage()))
                .build();

        new GuiItem(this, slot, e -> {
            e.getWhoClicked().closeInventory();
            HexManager.getInstance().setPlayerSettingFlag((Player) e.getWhoClicked(), tile, true);
        });
    }
}
