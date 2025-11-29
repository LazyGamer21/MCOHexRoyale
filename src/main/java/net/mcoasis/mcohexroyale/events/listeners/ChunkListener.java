package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexFlag;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Chunk;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        removeBlockDisplaysInChunk(e.getChunk());
        spawnFlags(e.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        removeBlockDisplaysInChunk(e.getChunk());
    }

    private void removeBlockDisplaysInChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof BlockDisplay blockDisplay) {
                blockDisplay.remove();
            }
        }
    }

    private void spawnFlags(Chunk chunk) {
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            HexFlag flag = tile.getHexFlag();

            // if the chunk coordinates do not match
            if (flag.getBase().getChunk().getX() != chunk.getX() || flag.getBase().getChunk().getZ() != chunk.getZ()) {
                // then skip
                continue;
            }

            flag.spawnFlag(false);
            flag.moveFlag(tile.getCapturePercentage());
        }
    }

}
