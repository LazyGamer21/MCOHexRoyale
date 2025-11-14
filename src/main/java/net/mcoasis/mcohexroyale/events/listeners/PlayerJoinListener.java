package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Location spawnLoc = WorldManager.getInstance().getLobbyWorld().getSpawnLocation();
        e.getPlayer().teleport(spawnLoc);
    }

}
