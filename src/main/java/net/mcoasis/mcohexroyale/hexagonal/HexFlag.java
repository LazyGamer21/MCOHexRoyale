package net.mcoasis.mcohexroyale.hexagonal;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class HexFlag {

    private final HexTile parentTile;

    private final World world;
    private Location corner1;
    private Location corner2;

    private final List<BlockData> blockDataList = new ArrayList<>();
    private final List<Vector> relativeOffsets = new ArrayList<>();
    private final List<BlockDisplay> flagDisplays = new ArrayList<>();

    private double baseY = Double.NEGATIVE_INFINITY;
    private double topY = Double.NEGATIVE_INFINITY;

    private BukkitRunnable updateFlagRunnable;

    public HexFlag(HexTile parentTile) {
        //! set world to the game world
        this.world = Bukkit.getWorld("world");

        this.parentTile = parentTile;

        updateFlagRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                updateFlag();
            }
        };

        updateFlagRunnable.runTaskTimer(MCOHexRoyale.getInstance(), 0L, MCOHexRoyale.FLAG_CAPTURE_TIMER);
    }

    private Location origin = null;

    // Grab all the blocks from the area and store their data
    public void captureOriginalBlocks() {
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());

        origin = new Location(world, minX, minY, minZ); // store the origin

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.AIR) continue;
                    //? use command blocks for temp blocks for setting corners
                    if (block.getType() == Material.COMMAND_BLOCK) continue;

                    // Store original block data
                    blockDataList.add(block.getBlockData());
                    relativeOffsets.add(new Vector(x - minX, y - minY, z - minZ));
                }
            }
        }
    }

    // Spawn block displays and remove original blocks when animation starts
    public void spawnDisplays() {
        if (!flagDisplays.isEmpty()) return;

        Location base = origin.clone();

        for (int i = 0; i < blockDataList.size(); i++) {
            final int index = i; // final copy for lambda
            Vector offset = relativeOffsets.get(index);
            Block block = world.getBlockAt(
                    origin.getBlockX() + offset.getBlockX(),
                    origin.getBlockY() + offset.getBlockY(),
                    origin.getBlockZ() + offset.getBlockZ()
            );

            // Only set block to air when we start moving
            block.setType(Material.AIR);

            // Spawn block display
            BlockDisplay display = world.spawn(base.clone().add(offset), BlockDisplay.class, bd -> {
                bd.setBlock(blockDataList.get(index));
                bd.setGravity(false);
            });

            flagDisplays.add(display);
        }
    }

    // Put all original blocks back where they were
    public void restoreOriginalBlocks() {
        // Remove any active displays
        for (Entity e : flagDisplays) {
            e.remove();
        }
        flagDisplays.clear();

        // Place the blocks back in the world
        for (int i = 0; i < blockDataList.size(); i++) {
            Vector offset = relativeOffsets.get(i);

            Block block = world.getBlockAt(
                    corner1.getBlockX() + offset.getBlockX(),
                    corner1.getBlockY() + offset.getBlockY(),
                    corner1.getBlockZ() + offset.getBlockZ()
            );

            block.setBlockData(blockDataList.get(i), false);
        }
    }

    // Update flag position based on capturePercentage
    public void updateFlag() {
        if (flagDisplays.isEmpty())  return;
        if (baseY == Double.NEGATIVE_INFINITY || topY == Double.NEGATIVE_INFINITY) return;
        if (corner1 == null || corner2 == null) return;

        double capturePercentage = parentTile.getCapturePercentage();
        double y = getY(capturePercentage);

        if (y == Double.NEGATIVE_INFINITY) return; // something went wrong in getY()

        // If at the bottom or top, convert to real blocks
        if (capturePercentage == 0.0 || capturePercentage == 100.0) {
            // solidify when fully captured
            //! for some reason blocks don't move when this is on
            //solidifyAt(y);
        } else {
            // make sure displays are spawned if mid-capture
            spawnDisplays();
        }
    }

    private double getY(double capturePercentage) {
        double progress = capturePercentage / 100.0;
        double heightOfFlag = abs(corner1.getY() - corner2.getY());
        double heightOfPole = baseY + (topY - baseY);
        if (heightOfFlag > heightOfPole) {
            Bukkit.getLogger().warning("[MCOHexRoyale] (Tile " + this.parentTile.getQ() + ", " + this.parentTile.getR() + ") Flag is taller than Pole!");
            return Double.NEGATIVE_INFINITY;
        }
        double y = baseY + (topY - baseY - heightOfFlag) * progress;

        for (int i = 0; i < flagDisplays.size(); i++) {
            BlockDisplay display = flagDisplays.get(i);
            Vector offset = relativeOffsets.get(i);

            Location newLoc = new Location(world,
                    corner1.getBlockX() + offset.getX(),
                    y + offset.getY(),
                    corner1.getBlockZ() + offset.getZ());

            display.teleport(newLoc);
        }

        return y;
    }

    // Turn displays into actual blocks
    private void solidifyAt(double y) {
        // Remove displays
        for (Entity e : flagDisplays) {
            e.remove();
        }
        flagDisplays.clear();

        // Place blocks
        for (int i = 0; i < blockDataList.size(); i++) {
            BlockData data = blockDataList.get(i);
            Vector offset = relativeOffsets.get(i);

            Block block = world.getBlockAt(
                    corner1.getBlockX() + offset.getBlockX(),
                    (int) (y + offset.getY()),
                    corner1.getBlockZ() + offset.getBlockZ()
            );

            block.setBlockData(data, false);
        }
    }

    // -- == Getters + Setters == --

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public void setBaseY(double baseY) {
        this.baseY = baseY;
    }

    public void setTopY(double topY) {
        this.topY = topY;
    }

}
