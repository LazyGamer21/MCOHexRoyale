package net.mcoasis.mcohexroyale.gui.main;

import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.kyori.adventure.platform.facet.Facet;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColor;
import net.mcoasis.mcohexroyale.gui.main.teams.SingleTeamPage;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.managers.GameManager;
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
            GameManager.GameState currentGameState = GameManager.getInstance().getGameState();
            if (currentGameState.equals(GameManager.GameState.STARTING) || currentGameState.equals(GameManager.GameState.INGAME)) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Cannot randomize teams while game is running! Use /setteam for specific players");
                e.getWhoClicked().closeInventory();
                return;
            }
            e.getWhoClicked().sendMessage(ChatColor.GRAY + "[HexRoyale] teams randomized");
            // for every player who does not have "nojoin" permission
            List<Player> playersToTeam = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("hexroyale.nojoin")) continue;
                playersToTeam.add(p);
            }
            assignPlayersToTeams(playersToTeam, true);
        })
                .setName(ChatColor.GRAY + "Randomize Teams (2)")
                .setLore("Sets all players in lobby world to a random team", "Only use while game is not running", "2 Teams"));

        assignItem(playerId, 22, new GuiItem(Material.STONE, e -> {
            GameManager.GameState currentGameState = GameManager.getInstance().getGameState();
            if (currentGameState.equals(GameManager.GameState.STARTING) || currentGameState.equals(GameManager.GameState.INGAME)) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Cannot randomize teams while game is running! Use /setteam for specific players");
                e.getWhoClicked().closeInventory();
                return;
            }
            e.getWhoClicked().sendMessage(ChatColor.GRAY + "[HexRoyale] teams randomized");
            // for every player who does not have "nojoin" permission
            List<Player> playersToTeam = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("hexroyale.nojoin")) continue;
                playersToTeam.add(p);
            }
            assignPlayersToTeams(playersToTeam, false);
        })
                .setName(ChatColor.GRAY + "Randomize Teams (4)")
                .setLore("Sets all players in lobby world to a random team", "Only use while game is not running", "4 Teams"));

        assignItem(playerId, 33, new GuiItem(Material.BLUE_WOOL, e -> {
            SingleTeamPage.teamToOpen.put(playerId, HexTeam.TeamColor.BLUE);
            GuiManager.getInstance().openPage(SingleTeamPage.pageId, (Player) e.getWhoClicked());
        })
                .setName(HexTeam.TeamColor.BLUE.getColor() + HexTeam.TeamColor.BLUE.getName())
                .setLore(ChatColor.GRAY + "Players: " + HexManager.getInstance().getTeam(HexTeam.TeamColor.BLUE).getMembersAlive().size()));
    }

    private void assignPlayersToTeams(List<Player> players, boolean twoTeams) {
        // Clear all teams first
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            team.getMembersAlive().clear();
        }
        int numTeams = twoTeams ? 2 : 4;
        HexTeam.TeamColor[] teamColors = {TeamColor.RED, TeamColor.BLUE, TeamColor.GREEN, TeamColor.YELLOW};

        // Shuffle to randomize player order
        Collections.shuffle(players);

        // Split evenly
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int teamIndex = i % numTeams; // distributes evenly across teams
            TeamColor team = teamColors[teamIndex];
            TeamColor teamColor;

            switch (team) {
                case TeamColor.RED:
                    teamColor = TeamColor.RED;
                    break;
                case TeamColor.BLUE:
                    teamColor = TeamColor.BLUE;
                    break;
                case TeamColor.GREEN:
                    teamColor = TeamColor.GREEN;
                    break;
                case TeamColor.YELLOW:
                    teamColor = TeamColor.YELLOW;
                    break;
                default:
                    continue;
            }

            HexManager.getInstance().getTeam(teamColor).addMember(player);
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
