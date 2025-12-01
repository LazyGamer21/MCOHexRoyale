package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexFlag;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    // ---------------------------------------------
    // GLOBAL HANDLERS
    // ---------------------------------------------

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Chunk chunk = e.getChunk();
        removeBlockDisplaysInChunk(chunk);

        // loading a chunk may complete the 3x3 area for a nearby flag
        checkNearbyFlags(chunk);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Chunk chunk = e.getChunk();
        removeBlockDisplaysInChunk(chunk);

        // If any chunk in a flag's 3x3 area unloads → flag should despawn
        unloadFlagsMissingChunks(chunk);
    }

    // ---------------------------------------------
    // REMOVE BLOCK DISPLAYS
    // ---------------------------------------------

    private void removeBlockDisplaysInChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof BlockDisplay display) {
                display.remove();
            }
        }
    }

    // ---------------------------------------------
    // FLAG SPAWNING / DESPAWNING LOGIC
    // ---------------------------------------------

    /**
     * Check all flags. If this loaded chunk is adjacent/diagonal to one,
     * re-evaluate if the full 3×3 is present.
     */
    private void checkNearbyFlags(Chunk loadedChunk) {
        int lx = loadedChunk.getX();
        int lz = loadedChunk.getZ();
        World world = loadedChunk.getWorld();

        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            HexFlag flag = tile.getHexFlag();

            int fx = flag.getBase().getBlockX() >> 4;
            int fz = flag.getBase().getBlockZ() >> 4;

            // Only check flags whose 3×3 area includes the loaded chunk
            if (Math.abs(lx - fx) <= 1 && Math.abs(lz - fz) <= 1) {
                if (areAll9ChunksLoaded(world, fx, fz)) {
                    // All chunks loaded → ensure flag is spawned
                    flag.spawnFlag(false);
                    flag.moveFlag(tile.getCapturePercentage());
                }
            }
        }
    }

    /**
     * When a chunk unloads, check if it's inside the 3×3 area of any flag.
     * If yes → despawn that flag.
     */
    private void unloadFlagsMissingChunks(Chunk unloadedChunk) {
        int ux = unloadedChunk.getX();
        int uz = unloadedChunk.getZ();
        World world = unloadedChunk.getWorld();

        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            HexFlag flag = tile.getHexFlag();

            int fx = flag.getBase().getBlockX() >> 4;
            int fz = flag.getBase().getBlockZ() >> 4;

            // Is this unloaded chunk part of the 3×3?
            if (Math.abs(ux - fx) <= 1 && Math.abs(uz - fz) <= 1) {
                // Despawn it immediately
                flag.removeFlag();
            }
        }
    }

    // ---------------------------------------------
    // UTILITY
    // ---------------------------------------------

    /**
     * Checks the full 3×3 area around a flag's center chunk coordinates.
     * Uses only coordinate math + isChunkLoaded() → completely safe inside unload events.
     */
    private boolean areAll9ChunksLoaded(World world, int centerX, int centerZ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (!world.isChunkLoaded(centerX + dx, centerZ + dz)) {
                    return false;
                }
            }
        }
        return true;
    }
}