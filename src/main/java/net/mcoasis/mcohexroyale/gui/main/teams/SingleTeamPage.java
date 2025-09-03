package net.mcoasis.mcohexroyale.gui.main.teams;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.main.TeamsPage;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SingleTeamPage extends AbstractGuiPage {
    public static String pageId = "main.teams.single-team";
    public static HexTeam.TeamColor teamToOpen = HexTeam.TeamColor.RED;

    public SingleTeamPage() {
        super(MCOHexRoyale.getInstance(), true, true, TeamsPage.pageId, true);
    }

    @Override
    protected String getDisplayName() {
        return teamToOpen.getColor() + teamToOpen.getName();
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
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons() {
        List<GuiItem> listedButtons = new ArrayList<>();

        listedButtons.add(new GuiItem(Material.GUNPOWDER, e -> {})
                .setName("ts is gunpowder, bruh"));
        listedButtons.add(new GuiItem(Material.GUNPOWDER, e -> {})
                .setName("ts is gunpowder, bruh"));
        listedButtons.add(new GuiItem(Material.GUNPOWDER, e -> {})
                .setName("ts is gunpowder, bruh"));

        for (Player p : HexManager.getInstance().getTeam(teamToOpen).getMembers()) {

            listedButtons.add(new GuiItem(Material.PLAYER_HEAD, e -> {

            }).setName(p.getName()).setSkullOwner(p));

        }

        return listedButtons;
    }
}
