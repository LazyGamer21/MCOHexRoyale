package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.kyori.adventure.platform.facet.Facet;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.TeamWonEvent;
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
            Bukkit.broadcastMessage(team.getTeamColor().getColor() + ChatColor.BOLD + team.getTeamColor().getName() + " Team's " + ChatColor.RESET + ChatColor.DARK_AQUA + "base tile was recaptured!");
            for (Player member : team.getMembersAlive().keySet()) {
                if (team.getMembersAlive().get((member))) continue;

                // set player to alive if they aren't and teleport them to base
                MCOHexRoyale.getInstance().resetPlayer(member);
                team.getBaseTile().teleportToBase(member);
                team.getMembersAlive().put(member, true);
            }
        } else Bukkit.broadcastMessage(color + ChatColor.BOLD + team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.GRAY
                + " has captured the point (" + color + e.getTile().getQ() + ", " + e.getTile().getR() + ChatColor.GRAY + ")!");

        if (tile.getHexFlag() != null) tile.getHexFlag().spawnFlag(false);

        // if it is the middle tile, the team wins the game
        if (tile.getQ() == 0 && tile.getR() == 0) {
            Bukkit.getPluginManager().callEvent(new TeamWonEvent(team, tile));
        }
    }

}