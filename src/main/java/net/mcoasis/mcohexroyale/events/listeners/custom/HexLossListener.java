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

        if (flag != null) flag.spawnFlag(false);

        for (HexTile teamTile : HexManager.getInstance().getHexGrid()) {
            if (teamTile.equals(tile)) continue;
            // if the tile is owned by "team", check if it still has a path to the base tile
            HexTeam currentTeam = teamTile.getCurrentTeam();
            if (currentTeam == null) continue;
            if (!teamTile.getCurrentTeam().equals(team)) continue;

            if (HexManager.getInstance().areConnected(teamTile, team.getBaseTile())) continue;

            teamTile.setCurrentTeam(null);
            teamTile.setCurrentTeamOwns(false);
            teamTile.setCapturePercentage(0.0);
            if (teamTile.getHexFlag() != null) teamTile.getHexFlag().spawnFlag(false);
        }

        if (tile.equals(team.getBaseTile())) {
            Bukkit.broadcastMessage(ChatColor.BOLD + team.getTeamColor().getColor() + team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.DARK_AQUA + " has lost their base tile!");
            team.checkTeamLoss();
        }

    }

}
