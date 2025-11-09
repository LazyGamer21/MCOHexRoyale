package net.mcoasis.mcohexroyale.gui.main;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColor;
import net.mcoasis.mcohexroyale.gui.main.teams.SingleTeamPage;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TeamsPage extends AbstractGuiPage {
    public static String pageId = "main.teams";

    public TeamsPage() {
        super(MCOHexRoyale.getInstance(), true, false, MainPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID playerId) {
        return ChatColor.DARK_AQUA + "Teams";
    }

    @Override
    protected int getRows() {
        return 6;
    }

    @Override
    protected void assignItems(UUID playerId) {
        assignItem(playerId, 20, new GuiItem(Material.RED_WOOL, e -> {
            SingleTeamPage.teamToOpen.put(playerId, HexTeam.TeamColor.RED);
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.RED.getColor() + HexTeam.TeamColor.RED.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).getMembersAlive().size()));

        assignItem(playerId, 24, new GuiItem(Material.LIME_WOOL, e -> {
            SingleTeamPage.teamToOpen.put(playerId, HexTeam.TeamColor.GREEN);
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.GREEN.getColor() + HexTeam.TeamColor.GREEN.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.GREEN).getMembersAlive().size()));

        assignItem(playerId, 29, new GuiItem(Material.YELLOW_WOOL, e -> {
            SingleTeamPage.teamToOpen.put(playerId, HexTeam.TeamColor.YELLOW);
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.YELLOW.getColor() + HexTeam.TeamColor.YELLOW.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.YELLOW).getMembersAlive().size()));

        assignItem(playerId, 31, new GuiItem(Material.STONE, e -> {
            //!DEBUG
            Bukkit.broadcastMessage("teams randomized");
            // for every player in lobby who does not have "nojoin" permission
            List<Player> playersToTeam = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getWorld().equals(WorldManager.getInstance().getLobbyWorld())) continue;
                if (p.hasPermission("hexroyale.nojoin")) continue;
                playersToTeam.add(p);
            }
            assignPlayersToTeams(playersToTeam);
        })
                .setName(ChatColor.GRAY + "Randomize Teams")
                .setLore("Sets all players in lobby world to a random team", "Only use while game is not running"));

        assignItem(playerId, 33, new GuiItem(Material.BLUE_WOOL, e -> {
            SingleTeamPage.teamToOpen.put(playerId, HexTeam.TeamColor.BLUE);
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.BLUE.getColor() + HexTeam.TeamColor.BLUE.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.BLUE).getMembersAlive().size()));
    }

    private void assignPlayersToTeams(List<Player> players) {
        int numTeams = 4;
        HexTeam.TeamColor[] teamNames = {TeamColor.RED, TeamColor.BLUE, TeamColor.GREEN, TeamColor.YELLOW};

        // Shuffle to randomize player order
        Collections.shuffle(players);

        // Split evenly
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int teamIndex = i % numTeams; // distributes evenly across 4 teams
            TeamColor team = teamNames[teamIndex];
            TeamColor teamColor;

            switch (team) {
                case TeamColor.RED:
                    teamColor = TeamColor.RED;
                    HexManager.getInstance().getTeam(teamColor).addMember(player);
                    Bukkit.broadcastMessage(teamColor.getColor() + player.getDisplayName() + " assigned to " + teamColor.getName() + " team");
                case TeamColor.BLUE:
                    teamColor = TeamColor.BLUE;
                    HexManager.getInstance().getTeam(teamColor).addMember(player);
                    Bukkit.broadcastMessage(teamColor.getColor() + player.getDisplayName() + " assigned to " + teamColor.getName() + " team");
                case TeamColor.GREEN:
                    teamColor = TeamColor.GREEN;
                    HexManager.getInstance().getTeam(teamColor).addMember(player);
                    Bukkit.broadcastMessage(teamColor.getColor() + player.getDisplayName() + " assigned to " + teamColor.getName() + " team");
                case TeamColor.YELLOW:
                    teamColor = TeamColor.YELLOW;
                    HexManager.getInstance().getTeam(teamColor).addMember(player);
                    Bukkit.broadcastMessage(teamColor.getColor() + player.getDisplayName() + " assigned to " + teamColor.getName() + " team");
            }
        }
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
