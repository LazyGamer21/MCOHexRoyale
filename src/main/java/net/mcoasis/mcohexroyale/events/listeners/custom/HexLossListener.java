package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.mcoasis.mcohexroyale.events.HexLossEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexFlag;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HexLossListener implements Listener {

    @EventHandler
    public void onHexLoss(HexLossEvent e) {
        HexTile tile = e.getTile();
        HexFlag flag = tile.getHexFlag();
        HexTeam team = e.getTeam();

        Bukkit.broadcastMessage(team.getTeamColor().getColor() + "team lost tile");

        if (flag != null) flag.spawnFlag(false);

        for (HexTile teamTile : HexManager.getInstance().getHexGrid()) {
            if (teamTile.equals(tile)) continue;
            Bukkit.broadcastMessage("1 --- " + ChatColor.YELLOW + teamTile.getQ() + ", " + teamTile.getR());
            // if the tile is owned by "team", check if it still has a path to the base tile
            HexTeam currentTeam = teamTile.getCurrentTeam();
            if (currentTeam == null || !teamTile.isCurrentTeamOwns()) continue;
            Bukkit.broadcastMessage("2 --- " + ChatColor.YELLOW + teamTile.getQ() + ", " + teamTile.getR());
            if (!teamTile.getCurrentTeam().equals(team)) continue;
            Bukkit.broadcastMessage("3 --- " + ChatColor.YELLOW + teamTile.getQ() + ", " + teamTile.getR());

            if (HexManager.getInstance().areConnected(teamTile, team.getBaseTile())) continue;
            Bukkit.broadcastMessage("4 --- " + ChatColor.YELLOW + teamTile.getQ() + ", " + teamTile.getR());

            teamTile.setCurrentTeam(null);
            teamTile.setCurrentTeamOwns(false);
            teamTile.setCapturePercentage(0.0);
            if (teamTile.getHexFlag() != null) teamTile.getHexFlag().spawnFlag(false);

            Bukkit.broadcastMessage(ChatColor.GRAY + "Tile lost: " + ChatColor.YELLOW + teamTile.getQ() + ", " + teamTile.getR());
        }
    }

}
