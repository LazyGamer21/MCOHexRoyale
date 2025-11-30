package net.mcoasis.mcohexroyale.events.listeners.custom;

import net.kyori.adventure.platform.facet.Facet;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.TeamWonEvent;
import net.mcoasis.mcohexroyale.events.listeners.RespawnListener;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class HexCaptureListener implements Listener {

    public static BukkitRunnable getWinCountdown() {
        return winCountdown;
    }

    private static BukkitRunnable winCountdown;
    private int secondsNeeded;

    public static int getWinTimeLeft() {
        return winTimeLeft;
    }

    private static int winTimeLeft;

    // will only be non-null if a team currently owns the middle tile
    public static HexTeam middleTileTeam = null;

    private HexTeam teamWithMiddleTile = null;

    @EventHandler
    public void onHexCapture(HexCaptureEvent e) {
        HexTeam team = e.getTeam();
        HexTile tile = e.getTile();
        String color = team.getTeamColor().getColor();

        for (UUID memberId : team.getMembersAlive().keySet()) {
            Player member = Bukkit.getPlayer(memberId);
            member.playSound(member.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        if (team.getBaseTile().equals(tile)) {
            Bukkit.broadcastMessage(team.getTeamColor().getColor() + ChatColor.BOLD + team.getTeamColor().getName() + " Team's " + ChatColor.RESET + ChatColor.DARK_AQUA + "base tile was recaptured!");
            for (UUID memberId : team.getMembersAlive().keySet()) {
                Player member = Bukkit.getPlayer(memberId);
                if (team.getMembersAlive().get((memberId))) continue;

                RespawnListener.stuff(member);
            }
        } else Bukkit.broadcastMessage(color + ChatColor.BOLD + team.getTeamColor().getName() + " Team" + ChatColor.RESET + ChatColor.GRAY
                + " has captured the point (" + color + e.getTile().getQ() + ", " + e.getTile().getR() + ChatColor.GRAY + ")!");

        if (tile.getHexFlag() != null) tile.getHexFlag().spawnFlag(false);

        // if it is the middle tile, the team wins the game
        if (tile.getQ() == 0 && tile.getR() == 0) {
            teamWithMiddleTile = team;
            startWinCountdown(teamWithMiddleTile);
        }
    }

    void startWinCountdown(HexTeam team) {
        MCOHexRoyale plugin = MCOHexRoyale.getInstance();
        secondsNeeded = plugin.getConfig().getInt("middle-tile-win-timer", 120);

        Bukkit.broadcastMessage(ChatColor.BOLD + team.getTeamColor().getColor() + team.getTeamColor().getName() + " Team "
                + ChatColor.RESET + ChatColor.GRAY + "has captured the middle tile! Hold it for "
                + ChatColor.BOLD + ChatColor.YELLOW + secondsNeeded
                + ChatColor.RESET + ChatColor.GRAY + " seconds to win!");

        //play sounds for all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (HexManager.getInstance().getPlayerTeam(player) == team) {
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 3.0f, 1.0f);
                continue;
            }
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
        }

        // Start a repeating task to count down
        winTimeLeft = secondsNeeded;
        winCountdown = new org.bukkit.scheduler.BukkitRunnable() {
            int timeLeft = secondsNeeded;

            @Override
            public void run() {
                // If the team lost control of the middle tile, cancel the countdown
                HexTile middle = HexManager.getInstance().getHexTile(0, 0);
                if (!middle.teamOwns(teamWithMiddleTile)) {
                    cancel();
                    middleTileTeam = null;
                    return;
                }
                middleTileTeam = teamWithMiddleTile;

                // If the countdown has finished, fire the win event
                // If it is being captured by another team do not end
                if (timeLeft <= 0 && middle.getCurrentTeam() == teamWithMiddleTile) {
                    Bukkit.getPluginManager().callEvent(new TeamWonEvent(teamWithMiddleTile, true));
                    cancel();
                    return;
                }

                timeLeft--;
                if (timeLeft <= 0) timeLeft = 0;
                winTimeLeft = timeLeft;
            }
        };
        winCountdown.runTaskTimer(plugin, 20L, 20L); // 20 ticks = 1 second
    }


}