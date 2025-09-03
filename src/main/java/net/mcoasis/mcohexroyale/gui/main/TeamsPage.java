package net.mcoasis.mcohexroyale.gui.main;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

public class TeamsPage extends AbstractGuiPage {
    public static String pageId = "main.teams";

    public TeamsPage() {
        super(MCOHexRoyale.getInstance(), true, true, MainPage.pageId, true);
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
    protected void assignItems() {
        assignItem(21, new GuiItem(Material.RED_WOOL, e -> {})
                .setName(HexTeam.TeamColor.RED.getColor() + HexTeam.TeamColor.RED.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).getMembers().size())
                .build());

        assignItem(23, new GuiItem(Material.LIME_WOOL, e -> {})
                .setName(HexTeam.TeamColor.GREEN.getColor() + HexTeam.TeamColor.GREEN.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).getMembers().size())
                .build());

        assignItem(30, new GuiItem(Material.YELLOW_WOOL, e -> {})
                .setName(HexTeam.TeamColor.YELLOW.getColor() + HexTeam.TeamColor.YELLOW.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).getMembers().size())
                .build());

        assignItem(32, new GuiItem(Material.BLUE_WOOL, e -> {})
                .setName(HexTeam.TeamColor.BLUE.getColor() + HexTeam.TeamColor.BLUE.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).getMembers().size())
                .build());
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
