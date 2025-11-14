package net.mcoasis.mcohexroyale.hexagonal;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HexFlag {

    HexTile tile;

    private List<FlagBlock> currentFlag = new ArrayList<>();
    private List<BlockDisplay> activeFlagBlocks = new ArrayList<>();

    private double flagHeight = Double.MIN_VALUE;

    public HexFlag(HexTile tile) {
        this.tile = tile;
    }

    public record FlagBlock(Vector offset, BlockData data) {}

    private Location base = new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);
    private Location bottom = new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);
    private Location top = new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);

    private void loadFlagSchematic() {
        String flagColorString;

        if (tile.getCurrentTeam() == null) {
            flagColorString = "white-flag-schematic";
        } else {
            flagColorString = switch (tile.getCurrentTeam().getTeamColor()) {
                case RED -> "red-flag-schematic";
                case GREEN -> "green-flag-schematic";
                case BLUE -> "blue-flag-schematic";
                case YELLOW -> "yellow-flag-schematic";
            };
        }

        String filePath = MCOHexRoyale.getInstance().getConfig().getString(flagColorString, "whiteflag.schem");

        // FAWE schematic folder
        File faweSchemFolder = new File(MCOHexRoyale.getInstance().getDataFolder().getParent(), "FastAsyncWorldEdit/schematics");
        File file = new File(faweSchemFolder, filePath);

        if (!file.exists()) {
            Bukkit.getLogger().warning("[MCOHexRoyale] Could not find file: " + file.getAbsolutePath());
            return;
        }

        List<FlagBlock> blocks = new ArrayList<>();

        Clipboard clipboard;
        try (FileInputStream fis = new FileInputStream(file)) {
            clipboard = Objects.requireNonNull(ClipboardFormats.findByFile(file)).getReader(fis).read();
        } catch (Exception e) {
            Bukkit.getLogger().warning("[MCOHexRoyale] Error loading schematic: " + file.getAbsolutePath());
            return;
        }

        BlockVector3 min = clipboard.getRegion().getMinimumPoint();
        BlockVector3 max = clipboard.getRegion().getMaximumPoint();
        flagHeight = clipboard.getDimensions().getBlockY();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    BlockVector3 vec = BlockVector3.at(x, y, z);
                    BlockState weBlock = clipboard.getBlock(vec);

                    if (!weBlock.getBlockType().getMaterial().isAir()) {
                        // Convert to Bukkit BlockData
                        String id = weBlock.getBlockType().getId().replace("minecraft:", "");
                        Material mat = Material.matchMaterial(id);
                        if (mat == null || mat == Material.BEDROCK) continue;

                        BlockData data = Bukkit.createBlockData(mat);

                        // Store relative position
                        Vector offset = new Vector(
                                x - min.getBlockX(),
                                y - min.getBlockY(),
                                z - min.getBlockZ()
                        );

                        blocks.add(new FlagBlock(offset, data));
                    }
                }
            }
        }

        currentFlag = blocks;
    }

    public void spawnFlag(boolean flagAtTop) {
        loadFlagSchematic();

        World flagWorld = bottom.getWorld();
        if (flagWorld == null) {
            Bukkit.getLogger().warning("[MCOHexRoyale] Could not respawn flag for tile: " + tile.getQ() + ", " + tile.getR());
            return;
        }

        List<BlockDisplay> displays = new ArrayList<>();
        removeFlag(); // remove old flag blocks

        if (currentFlag == null) {
            Bukkit.getLogger().warning("Could not spawn flag for tile: " + tile.getQ() + ", " + tile.getR());
            return;
        }

        Location spawnToClone = flagAtTop ? top.clone() : bottom.clone();
        if (flagAtTop) spawnToClone.add(0, -flagHeight + 1, 0);

        for (FlagBlock fb : currentFlag) {
            Location loc = spawnToClone.clone().add(fb.offset());

            BlockDisplay display = (BlockDisplay) flagWorld.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
            // Make it visible from far away (default is about 48 blocks)
            display.setViewRange(512f); // max is 512f (512 blocks)
            display.setBlock(fb.data());

            displays.add(display);
        }

        activeFlagBlocks = displays;
    }

    public void moveFlag(double capturePercentage) {
        // flag has not been set if the height is MIN_VALUE
        if (flagHeight == Double.MIN_VALUE) return;

        double flagPoleHeight = top.getY() - bottom.getY() + 1.0;

        double newY = bottom.getY() + ((flagPoleHeight- flagHeight) * (capturePercentage * 0.01));

        Location flagTeleportPoint = bottom.clone();
        flagTeleportPoint.setY(newY);

        for (int i = 0; i < activeFlagBlocks.size(); i++) {
            BlockDisplay display = activeFlagBlocks.get(i);
            FlagBlock fb = currentFlag.get(i);

            Location loc = flagTeleportPoint.clone().add(fb.offset());
            display.teleport(loc);
        }
    }

    public void removeFlag() {
        for (BlockDisplay display : activeFlagBlocks) {
            display.remove();
        }
        activeFlagBlocks.clear();
    }

    // -- == Getters + Setters == --

    public void setBase(Location base) {
        this.base = base;
    }

    public void setBottom(Location bottom) { this.bottom = bottom; }

    public void setTop(Location top) {
        this.top = top;
    }

    public Location getBase() {
        return base;
    }

    public Location getTop() {
        return top;
    }

    public Location getBottom() { return bottom; }

    public double getFlagHeight() {
        return flagHeight;
    }

}
