package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.mcoasis.mcohexroyale.events.TeamWonEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamWonListener implements Listener {

    @EventHandler
    public void onTeamWon(TeamWonEvent e) {
        HexTeam team = e.getTeam();

        Bukkit.broadcastMessage(ChatColor.BOLD + team.getTeamColor().getColor() + team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.GOLD + " has won the game!");
    }

}
