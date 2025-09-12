package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HexCaptureListener implements Listener {

    @EventHandler
    public void onHexCapture(HexCaptureEvent e) {
        HexTeam team = e.getTeam();
        String color = team.getTeamColor().getColor();

        Bukkit.broadcastMessage(color + ChatColor.BOLD + team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.GRAY + " has captured the point (" + color + e.getTile().getQ() + ", " + e.getTile().getR() + ChatColor.GRAY + ")!");
    }

}