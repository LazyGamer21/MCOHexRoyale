package net.mcoasis.mcohexroyale.listeners;

import net.mcoasis.mcohexroyale.commands.subcommands.GuiCommand;
import net.mcoasis.mcohexroyale.datacontainers.PlayerFlagData;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private GuiCommand flagCommand;

    public PlayerInteractListener() {
        //this.flagCommand = flagCommand;
    }

    public enum CurrentBlock {
        FIRST,
        SECOND;
    }

    //? for setting the flag pole position
    //? -- used for moving the flag up and down and for setting capture position
    @EventHandler
    public void onFlagPoleSet(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        //* references
        HashMap<UUID, PlayerFlagData> settingTileFlag = HexManager.getInstance().getSettingTileFlag();
        PlayerFlagData flagData = settingTileFlag.get(p.getUniqueId());

        //* make sure player is setting the flag pole
        if (settingTileFlag.containsKey(p.getUniqueId())) {
            if (!flagData.isSettingPole()) return;
            //* cancel if they right-clicked
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                p.sendMessage(ChatColor.GRAY + "Setting Flag Pole Points -- " + ChatColor.RED + "Canceled");
                settingTileFlag.remove(p.getUniqueId());
                e.setCancelled(true);
                return;
            }
        } else return;

        //* we only care about left-clicking now
        if (!e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            return;
        }

        //* only if left-click is a block
        if (e.getClickedBlock() == null){
            return;
        }

        int x = e.getClickedBlock().getLocation().getBlockX();
        int y = e.getClickedBlock().getLocation().getBlockY();
        int z = e.getClickedBlock().getLocation().getBlockZ();

        e.setCancelled(true);

        if (flagData.getCurrentBlock().equals(CurrentBlock.FIRST)){
            //* set the first block
            flagData.setFlagTemp1(e.getClickedBlock().getLocation());

            p.sendMessage(ChatColor.GRAY + "Set Flag Pole Bottom to (" + ChatColor.YELLOW + x + ", " + y + ", " + z + ChatColor.GRAY + ")");
            p.sendMessage(ChatColor.GRAY + "Punch Another Block to set Flag Pole Top -- Right-Click to Cancel");

            //* let code know they're setting the second block now
            flagData.setCurrentBlock(CurrentBlock.SECOND);
            return;
        } else {
            //* set the second block
            flagData.setFlagTemp2(e.getClickedBlock().getLocation());

            //* ensure locations are in the same world
            if (flagData.getFlagTemp1().getWorld() == null ||
                    flagData.getFlagTemp2().getWorld() == null ||
                    !flagData.getFlagTemp1().getWorld()
                            .equals(flagData.getFlagTemp2().getWorld())){
                p.sendMessage(ChatColor.RED + "Both Points Must be in the Same World!");
                p.sendMessage(ChatColor.AQUA + "Choose a Different Block for Flag Pole Top");
                return;
            }

            //* ensure x and z coordinates are the same since it's a pole
            if (flagData.getFlagTemp1().getBlockX() != flagData.getFlagTemp2().getBlockX() ||
                    flagData.getFlagTemp1().getBlockZ() != flagData.getFlagTemp2().getBlockZ()) {
                p.sendMessage(ChatColor.RED + "Both Points must have Matching X and Z Coordinates!");
                p.sendMessage(ChatColor.AQUA + "Choose a Different Block for Flag Pole Top");
                return;
            }

            p.sendMessage(ChatColor.GRAY + "Set Flag Pole Top to (" + ChatColor.YELLOW + x + ", " + y + ", " + z + ChatColor.GRAY + ")");
            p.sendMessage(ChatColor.AQUA + "Flag Pole Setup Complete - Y levels: (" + ChatColor.YELLOW + flagData.getFlagTemp1().getBlockY() + " - " + flagData.getFlagTemp2().getBlockY() + ChatColor.AQUA + ")");
        }

        //* passed all checks, now finalize the corners
        if (flagData.getFlagTemp1().getY() < flagData.getFlagTemp2().getY()) {
            // ensure setFlagPole gets the bottom of the flag first
            flagData.getHexTile().setFlagPole(flagData.getFlagTemp1(), flagData.getFlagTemp2());
        } else flagData.getHexTile().setFlagPole(flagData.getFlagTemp2(), flagData.getFlagTemp1());

        //* make particles to visualize the flag area
        createParticleOutline(flagData.getFlagTemp1(), flagData.getFlagTemp2());

        settingTileFlag.remove(p.getUniqueId());
    }

    //? for when player is setting flag (not pole) area
    //? -- used for the part that will move
    @EventHandler
    public void onFlagAreaSet(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        //* references
        HashMap<UUID, PlayerFlagData> settingTileFlag = HexManager.getInstance().getSettingTileFlag();
        PlayerFlagData flagData = settingTileFlag.get(p.getUniqueId());
        int minAreaVolume = HexTile.MIN_FLAG_AREA_VOLUME;
        int maxAreaVolume = HexTile.MAX_FLAG_AREA_VOLUME;

        //* make sure player is setting the flag (not pole)
        if (settingTileFlag.containsKey(p.getUniqueId())) {
            if (flagData.isSettingPole()) return;
            //* cancel if they right-clicked
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                p.sendMessage(ChatColor.GRAY + "Setting Flag Corners -- " + ChatColor.RED + "Canceled");
                settingTileFlag.remove(p.getUniqueId());
                e.setCancelled(true);
                return;
            }
        } else return;

        //* we only care about left-clicking now
        if (!e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            return;
        }

        //* only if left-click is a block
        if (e.getClickedBlock() == null){
            return;
        }

        int x = e.getClickedBlock().getLocation().getBlockX();
        int y = e.getClickedBlock().getLocation().getBlockY();
        int z = e.getClickedBlock().getLocation().getBlockZ();

        e.setCancelled(true);

        if (flagData.getCurrentBlock().equals(CurrentBlock.FIRST)){
            //* set the first block
            flagData.setFlagTemp1(e.getClickedBlock().getLocation());

            p.sendMessage(ChatColor.GRAY + "Set Flag Corner 1 to (" + ChatColor.YELLOW + x + ", " + y + ", " + z + ChatColor.GRAY + ")");
            p.sendMessage(ChatColor.GRAY + "Punch Another Block to set Flag Corner 2 -- Right-Click to Cancel");

            //* let code know they're setting the second block now
            flagData.setCurrentBlock(CurrentBlock.SECOND);
            return;
        } else {
            //* set the second block
            flagData.setFlagTemp2(e.getClickedBlock().getLocation());

            //* ensure locations are in the same world
            if (flagData.getFlagTemp1().getWorld() == null ||
                    flagData.getFlagTemp2().getWorld() == null ||
                    !flagData.getFlagTemp1().getWorld()
                            .equals(flagData.getFlagTemp2().getWorld())){
                p.sendMessage(ChatColor.RED + "Both Corners Must be in the Same World!");
                p.sendMessage(ChatColor.AQUA + "Choose a Different Block for Flag Corner 2");
                return;
            }

            //* calculate volume to ensure it's within acceptable range
            int volume = calculateVolume(flagData.getFlagTemp1(), flagData.getFlagTemp2());
            if (volume > maxAreaVolume){
                p.sendMessage(ChatColor.RED + "Volume (" + ChatColor.YELLOW + volume + ChatColor.RED + ") Exceeds Max Value: " + ChatColor.YELLOW + maxAreaVolume);
                p.sendMessage(ChatColor.AQUA + "Choose a Different Block for Flag Corner 2");
                return;
            }
            if (volume < minAreaVolume){
                p.sendMessage(ChatColor.RED + "Volume (" + ChatColor.YELLOW + volume + ChatColor.RED + ") Exceeds Min Value: " + ChatColor.YELLOW + minAreaVolume);
                p.sendMessage(ChatColor.AQUA + "Choose a Different Block for Flag Corner 2");
                return;
            }

            p.sendMessage(ChatColor.GRAY + "Set Flag Corner 2 to (" + ChatColor.YELLOW + x + ", " + y + ", " + z + ChatColor.GRAY + ")");
            p.sendMessage(ChatColor.AQUA + "Flag Area Setup Complete - Area: " + ChatColor.YELLOW + volume);
        }

        //* passed all checks, now finalize the corners
        flagData.getHexTile().setFlagCorners(flagData.getFlagTemp1(), flagData.getFlagTemp2());

        //* make particles to visualize the flag area
        createParticleOutline(flagData.getFlagTemp1(), flagData.getFlagTemp2());

        settingTileFlag.remove(p.getUniqueId());
    }

    /**
     *
     * @param loc1 Corner 1
     * @param loc2 Corner 2
     * @return The volume of blocks created by the two locations
     */
    private int calculateVolume(Location loc1, Location loc2) {
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());

        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());

        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int xDiff = maxX - minX + 1;  // Add 1 to include both endpoints
        int yDiff = maxY - minY + 1;
        int zDiff = maxZ - minZ + 1;

        return xDiff * yDiff * zDiff;
    }

    /**
     *
     * @param loc1 Corner 1
     * @param loc2 Corner 2
     * @implNote Creates particles to visualize the volume created by the two locations
     */
    public void createParticleOutline(Location loc1, Location loc2) {
        World world = loc1.getWorld();
        if (world == null || !world.equals(loc2.getWorld())) {
            Bukkit.getLogger().warning("[MCOHexRoyale -- createParticleOutline] Locations must be in the same world.");
            return;
        }

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        // Increased particle count and persistence
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    boolean isEdge = (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ);
                    if (isEdge) {
                        Location blockLoc = new Location(world, x + 0.5, y + 0.5, z + 0.5);

                        // Increase the number of particles and density
                        world.spawnParticle(
                                Particle.HAPPY_VILLAGER, // Choose your preferred particle
                                blockLoc,
                                10,    // Particle count
                                0.1,   // X-offset for spread
                                0.1,   // Y-offset for spread
                                0.1,   // Z-offset for spread
                                0.05   // Speed/extra parameter for density
                        );
                    }
                }
            }
        }
    }

}
