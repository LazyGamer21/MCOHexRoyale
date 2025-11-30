package net.mcoasis.mcohexroyale.managers;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.HashSet;
import java.util.Set;

public class WorldManager {

    private static WorldManager instance;

    public static WorldManager getInstance() {
        if (instance == null) {
            instance = new WorldManager();
        }
        return instance;
    }

    private WorldManager() {
        // Private constructor to prevent instantiation
        gameWorld = Bukkit.getWorld(MCOHexRoyale.getInstance().getConfig().getString("game-world", "world"));
        lobbyWorld = Bukkit.getWorld(MCOHexRoyale.getInstance().getConfig().getString("lobby-world", "world"));

        if (gameWorld == null) {
            Bukkit.getLogger().severe("[MCOHexRoyale] Game world not found! Please check the configuration.");
        }
        if (lobbyWorld == null) {
            Bukkit.getLogger().severe("[MCOHexRoyale] Lobby world not found! Please check the configuration.");
        }
    }

    private World gameWorld;
    private World lobbyWorld;

    private final Set<Location> placedGameBlocks = new HashSet<>();

    /**
     * Removes all entities and player-placed blocks from the game world.
     */
    public void resetGameWorld() {
        Bukkit.getLogger().info("RESETTING GAME WORLD");
        // Logic to reset the game world to its initial state
        for (Entity entity : WorldManager.getInstance().getGameWorld().getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                entity.remove();
            }
            if (entity instanceof Item || entity instanceof BlockDisplay) {
                entity.remove();
            }
        }
        for (Location loc : placedGameBlocks) {
            if (loc.getWorld() != null) {
                loc.getBlock().setType(Material.AIR);
            }
        }
        placedGameBlocks.clear();
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public Set<Location> getPlacedGameBlocks() {
        return placedGameBlocks;
    }

}