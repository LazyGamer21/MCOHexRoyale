package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        // only track blocks broken during the game
        if (!GameManager.getInstance().getGameState().equals(GameManager.GameState.INGAME)) return;

        // allow breaking blocks that were placed by players
        if (WorldManager.getInstance().getPlacedGameBlocks().contains(e.getBlock().getLocation())) {
            WorldManager.getInstance().getPlacedGameBlocks().remove(e.getBlock().getLocation());
            return;
        }

        // ignore blocks broken by players not in teams (e.g. spectators, admins)
        Player p = e.getPlayer();
        if (HexManager.getInstance().getPlayerTeam(p) == null) return;

        // ignore blocks that were placed by players
        if (WorldManager.getInstance().getPlacedGameBlocks().contains(e.getBlock().getLocation())) return;

        // don't allow breaking blocks that are not placed by players
        e.setCancelled(true);

        //! if the block was a resource on a resource tile and their team owns it, give them the block
    }

}
