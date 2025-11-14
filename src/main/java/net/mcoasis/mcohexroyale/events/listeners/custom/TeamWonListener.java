package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.mcoasis.mcohexroyale.events.TeamWonEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamWonListener implements Listener {

    @EventHandler
    public void onTeamWon(TeamWonEvent e) {
        HexTeam team = e.getTeam();

        Bukkit.broadcastMessage(ChatColor.BOLD + team.getTeamColor().getColor() + team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.GOLD + " has won the game!");

        String subText = e.isMiddleTile() ? "by holding the middle tile!" : "by eliminating all other teams!";

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(team.getTeamColor().getColor() + team.getTeamColor().getName().toUpperCase() + " WINS!", subText, 10, 70, 20);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }
    }

}
