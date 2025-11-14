package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangeWorldListener implements Listener {

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        // check if player is going to the game world
        if (!e.getPlayer().getWorld().equals(WorldManager.getInstance().getGameWorld())) return;

        // check if the game is starting or running
        if (!GameManager.getInstance().getGameState().equals(GameManager.GameState.STARTING)
                && !GameManager.getInstance().getGameState().equals(GameManager.GameState.INGAME)) return;

        Player p = e.getPlayer();
        HexTeam team = HexManager.getInstance().getPlayerTeam(p);

        // if the player doesn't have a team, ignore
        if (team == null) return;

        GameManager.getInstance().teleportAndResetPlayer(team, p);
    }

}
