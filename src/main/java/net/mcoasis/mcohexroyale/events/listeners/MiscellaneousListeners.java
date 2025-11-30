package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class MiscellaneousListeners {

    @EventHandler
    public void onDimensionChange(PlayerPortalEvent e) {
        Player p = e.getPlayer();

        // Only block if player is in your game world
        boolean inGame = GameManager.getInstance().getGameState() == GameManager.GameState.STARTING || GameManager.getInstance().getGameState() == GameManager.GameState.INGAME;
        if (p.getWorld() == WorldManager.getInstance().getGameWorld() && inGame) {
            e.setCancelled(true);
            p.sendMessage("§cYou cannot switch dimensions during a game!");
        }
    }

    @EventHandler
    public void onHorseSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;

        if (e.getEntity() instanceof Horse horse) {
            horse.setTamed(true);
            horse.setOwner(null);
        }
    }

}
