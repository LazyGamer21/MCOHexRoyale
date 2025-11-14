package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.GameManager.GameState;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

        boolean inGame = GameManager.getInstance().getGameState().equals(GameManager.GameState.INGAME);
        boolean playerIsAdmin = e.getPlayer().hasPermission("hexroyale.admin");
        if (!inGame) {
            // only let admins break/place blocks in the game world
            if (!playerIsAdmin){
                e.setCancelled(true);
            }
            return;
        }

        GameState gameState = GameManager.getInstance().getGameState();
        // only track blocks placed during the game
        if (!gameState.equals(GameState.INGAME) && !gameState.equals(GameState.STARTING)) return;

        // only track blocks placed by players in teams
        Player p = e.getPlayer();
        if (HexManager.getInstance().getPlayerTeam(p) == null) return;

        if (gameState.equals(GameState.STARTING)) {
            e.setCancelled(true);
            return;
        }

        int maxY = MCOHexRoyale.getInstance().getConfig().getInt("max-build-height", 100);
        if (e.getBlock().getLocation().getBlockY() > maxY) {
            p.sendMessage(ChatColor.RED + "Max build height reached!");
            e.setCancelled(true);
            return;
        }

        WorldManager.getInstance().getPlacedGameBlocks().add(e.getBlock().getLocation());
    }

}
