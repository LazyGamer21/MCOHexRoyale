package net.mcoasis.mcohexroyale.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HexCaptureListener implements Listener {

    @EventHandler
    public void onHexCapture(HexCaptureEvent e) {
        Player p = e.getPlayer();
        HexTeam team = e.getTeam();

        Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + p.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + " has captured the point!");
        if (team != null) Bukkit.broadcastMessage(team.getTeamColor().getColor() + ChatColor.BOLD + team.getTeamColor().getName() + ChatColor.RESET + ChatColor.YELLOW + " now owns the tile!");
    }

}