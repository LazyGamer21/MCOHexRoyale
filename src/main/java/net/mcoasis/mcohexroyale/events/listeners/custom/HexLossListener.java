package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.mcoasis.mcohexroyale.events.HexLossEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexFlag;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

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
            for (UUID memberId : team.getMembersAlive().keySet()) {
                Player member = Bukkit.getPlayer(memberId);
                // send a title message saying "Base Lost!" and send dragon sound
                member.sendTitle(ChatColor.RED + "Base Lost!", ChatColor.GRAY + "You can no longer respawn!", 10, 70, 20);
                member.playSound(member.getLocation(), "minecraft:entity.ender_dragon.growl", 1.0f, 1.0f);
            }
            team.checkTeamLoss(false);
            return;
        }

        if (tile.getQ() == 0 && tile.getR() == 0) {
            BukkitRunnable winCountdown = HexCaptureListener.getWinCountdown();
            if (winCountdown != null) winCountdown.cancel();

            Bukkit.broadcastMessage(ChatColor.BOLD + team.getTeamColor().getColor() + team.getTeamColor().getName() + " Team "
                    + ChatColor.RESET + ChatColor.GRAY + "has lost the middle tile!");
            HexCaptureListener.middleTileTeam = null;
            return;
        }

    }

}
