package net.mcoasis.mcohexroyale.gui.main.teams;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RedTeamPage extends AbstractGuiPage {
    public RedTeamPage() {
        super(MCOHexRoyale.getInstance(), true, true);
    }

    @Override
    protected String getDisplayName() {
        return HexTeam.TeamColor.RED.getColor() + HexTeam.TeamColor.RED.getName();
    }

    @Override
    protected int getRows() {
        return 6;
    }

    @Override
    protected void assignItems() {

    }

    @Override
    public String getPageIdentifier() {
        return "";
    }

    @Override
    protected List<GuiItem> getListedButtons() {
        List<GuiItem> listedButtons = new ArrayList<>();

        /*for (Player p : HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).getMembers()) {

            listedButtons.add(new GuiItem(this, e -> {

            }));

        }*/

        return List.of();
    }
}
