package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HexCaptureListener implements Listener {

    @EventHandler
    public void onHexCapture(HexCaptureEvent e) {
        HexTeam team = e.getTeam();
        HexTile tile = e.getTile();
        String color = team.getTeamColor().getColor();

        if (team.getBaseTile().equals(tile)) {
            Bukkit.broadcastMessage(ChatColor.GRAY + team.getTeamColor().getName() + "'s base tile was recaptured!");
            for (Player member : team.getMembersAlive().keySet()) {
                if (team.getMembersAlive().get((member))) continue;

                // set player to alive if they aren't and teleport them to base
                MCOHexRoyale.getInstance().resetPlayer(member);
                team.getBaseTile().teleportToBase(member);
                team.getMembersAlive().put(member, true);
            }
        }

        tile.getHexFlag().spawnFlag(false);

        Bukkit.broadcastMessage(color + ChatColor.BOLD + team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.GRAY + " has captured the point (" + color + e.getTile().getQ() + ", " + e.getTile().getR() + ChatColor.GRAY + ")!");
    }

}