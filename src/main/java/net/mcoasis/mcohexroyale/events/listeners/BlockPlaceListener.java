package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        // only track blocks placed during the game
        if (!GameManager.getInstance().getGameState().equals(GameManager.GameState.INGAME)) return;

        // only track blocks placed by players in teams
        Player p = e.getPlayer();
        if (HexManager.getInstance().getPlayerTeam(p) == null) return;

        WorldManager.getInstance().getPlacedGameBlocks().add(e.getBlock().getLocation());
    }

}
