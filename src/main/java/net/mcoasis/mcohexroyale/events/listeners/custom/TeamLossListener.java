package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.mcoasis.mcohexroyale.events.TeamLossEvent;
import net.mcoasis.mcohexroyale.events.TeamWonEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class TeamLossListener implements Listener {

    @EventHandler
    public void onTeamLoss(TeamLossEvent e) {
        HexTeam team = e.getTeam();

        team.setTeamAlive(false);
        Bukkit.broadcastMessage(ChatColor.BOLD + team.getTeamColor().getColor() +
                team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.DARK_PURPLE + " has lost the game!");

        team.getBaseTile().flagOwnershipGone(false);

        List<HexTeam> aliveTeams = new ArrayList<>();
        for (HexTeam otherTeam : HexManager.getInstance().getTeams()) {
            if (otherTeam == team) continue;
            if (otherTeam.isTeamAlive()) aliveTeams.add(otherTeam);
        }

        if (aliveTeams.size() > 1) return;

        if (!aliveTeams.isEmpty()) Bukkit.getPluginManager().callEvent(new TeamWonEvent(aliveTeams.getFirst(), false));
    }

}
