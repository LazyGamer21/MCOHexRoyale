package net.mcoasis.mcohexroyale.gui.main;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.gui.main.teams.SingleTeamPage;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TeamsPage extends AbstractGuiPage {
    public static String pageId = "main.teams";

    public TeamsPage() {
        super(MCOHexRoyale.getInstance(), true, false, MainPage.pageId, true);
    }

    @Override
    protected String getDisplayName() {
        return ChatColor.DARK_AQUA + "Teams";
    }

    @Override
    protected int getRows() {
        return 6;
    }

    @Override
    protected void assignItems(UUID playerId) {
        assignItem(playerId, 21, new GuiItem(Material.RED_WOOL, e -> {
            SingleTeamPage.teamToOpen = HexTeam.TeamColor.RED;
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.RED.getColor() + HexTeam.TeamColor.RED.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).getMembersAlive().size()));

        assignItem(playerId, 23, new GuiItem(Material.LIME_WOOL, e -> {
            SingleTeamPage.teamToOpen = HexTeam.TeamColor.GREEN;
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.GREEN.getColor() + HexTeam.TeamColor.GREEN.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.GREEN).getMembersAlive().size()));

        assignItem(playerId, 30, new GuiItem(Material.YELLOW_WOOL, e -> {
            SingleTeamPage.teamToOpen = HexTeam.TeamColor.YELLOW;
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.YELLOW.getColor() + HexTeam.TeamColor.YELLOW.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.YELLOW).getMembersAlive().size()));

        assignItem(playerId, 32, new GuiItem(Material.BLUE_WOOL, e -> {
            SingleTeamPage.teamToOpen = HexTeam.TeamColor.BLUE;
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.BLUE.getColor() + HexTeam.TeamColor.BLUE.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.BLUE).getMembersAlive().size()));
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
