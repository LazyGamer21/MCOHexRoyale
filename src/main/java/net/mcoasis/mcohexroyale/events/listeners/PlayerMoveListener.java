package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent e) {
        if (e.getPlayer().hasPermission("hexroyale.admin")) return;

        boolean starting = GameManager.getInstance().getGameState() == GameManager.GameState.STARTING;
        boolean inGameWorld = e.getPlayer().getWorld().equals(WorldManager.getInstance().getGameWorld());

        if (starting && inGameWorld) {
            // allow turning head but not moving
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                e.setCancelled(true);
            }
        }
    }

}
