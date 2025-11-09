package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.mcoasis.mcohexroyale.events.TeamLossEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamLossListener implements Listener {

    @EventHandler
    public void onTeamLoss(TeamLossEvent e) {
        HexTeam team = e.getTeam();

        team.setTeamAlive(false);
        Bukkit.broadcastMessage(ChatColor.BOLD + team.getTeamColor().getColor() + team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.DARK_PURPLE + " has lost the game!");
    }

}
