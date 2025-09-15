package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final int respawnTimer = 5;

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        HexTeam team = HexManager.getInstance().getPlayerTeam(p);

        if (team == null) return;
        if (team.hasBaseCaptured()) {
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(ChatColor.GRAY + "Respawning in " + respawnTimer + " seconds!");
            Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> {
                team.getBaseTile().teleportToBase(p);
                p.setGameMode(GameMode.SURVIVAL);
                team.getMembersAlive().put(p, true);
            }, 20L * respawnTimer);
            return;
        }

        p.setGameMode(GameMode.SPECTATOR);
        team.getMembersAlive().put(p, false);
        p.sendMessage(ChatColor.RED + "Your team does not have their flag! Wait to respawn until it is recaptured!");
    }

}