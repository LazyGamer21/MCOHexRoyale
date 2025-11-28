package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BlockBreakListener implements Listener {

    // used for regenerating blocks after they are harvested
    private final Map<Material, Integer> harvestableRegenTimes = new HashMap<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        Location brokenBlockLocation = e.getBlock().getLocation();

        // allow breaking blocks that were placed by players
        if (WorldManager.getInstance().getPlacedGameBlocks().contains(brokenBlockLocation)) {
            WorldManager.getInstance().getPlacedGameBlocks().remove(brokenBlockLocation);
            return;
        }

        // don't allow breaking blocks that players did not place
        boolean playerIsAdmin = e.getPlayer().hasPermission("hexroyale.admin");
        if (!playerIsAdmin){
            e.setCancelled(true);
        } else {
            if (GameManager.getInstance().getGameState() == GameManager.GameState.INGAME) {
                e.setCancelled(true);
            }
        }

        // ignore blocks broken by players not in teams (e.g., spectators, admins)
        Player p = e.getPlayer();
        HexTeam team = HexManager.getInstance().getPlayerTeam(p);
        if (team == null) return;

        // get the closest HexTile to the broken block
        double closestDistance = Double.MAX_VALUE;
        HexTile closestTile = null;
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            double distance = brokenBlockLocation.distance(tile.getFlagLocation());
            if (distance > closestDistance) continue;
            closestDistance = distance;
            closestTile = tile;
        }

        // something went wrong, there should always be the closest tile
        if (closestTile == null) {
            Bukkit.getLogger().warning("[HexRoyale] Closest tile to a broken game block not found!");
            return;
        }

        // make sure the player's team owns this tile
        if (!closestTile.teamOwns(team)) return;

        FileConfiguration config = MCOHexRoyale.getInstance().getConfig();
        boolean blocksHarvestable = config.getBoolean("blocks-harvestable");
        boolean woodHarvestable = config.getBoolean("wood-harvestable");
        Map<String, Integer> harvestableBlocks = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("harvestable-blocks");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                harvestableBlocks.put(key.toUpperCase(), section.getInt(key));
            }
        }

        // check if blocks are set to be harvestable
        if (!blocksHarvestable) return;

        // check if the current block is harvestable
        if (!isHarvestable(e.getBlock(), woodHarvestable, harvestableBlocks)) return;

        // successful harvest
        Material blockType = e.getBlock().getType();
        Collection<ItemStack> drops = e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), p);
        if (!drops.isEmpty()) {
            for (ItemStack drop : drops) {
                HashMap<Integer, ItemStack> remaining = p.getInventory().addItem(drop);
                // drop remaining items on ground if inventory full
                for (ItemStack leftover : remaining.values()) {
                    p.getWorld().dropItemNaturally(brokenBlockLocation, leftover);
                }
            }
            int xp = getXpForBlock(blockType);
            if (xp > 0) {
                p.giveExp(xp);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
            brokenBlockLocation.getBlock().setType(Material.AIR);
        }

        // regenerate block after time specified in config
        Integer regenSeconds = harvestableRegenTimes.get(blockType);
        if (regenSeconds == null) regenSeconds = 0;
        Location blockLoc = brokenBlockLocation.clone();

        Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> {
            blockLoc.getBlock().setType(blockType);
        }, Math.max(regenSeconds * 20L, 1L)); // convert seconds to ticks
    }

    private boolean isHarvestable(Block block, boolean woodHarvestable, Map<String, Integer> harvestableBlocks) {
        if (block.getY() <= MCOHexRoyale.getInstance().getConfig().getInt("map-floor-y-level")) return false;

        Material type = block.getType();

        // Direct match from config
        if (harvestableBlocks.containsKey(type.name())) return true;

        // Handle wood if enabled
        if (woodHarvestable) {
            String name = type.name();
            if (name.contains("WOOD") || name.contains("LOG") || name.contains("PLANKS") || name.contains("STRIPPED_")) {
                return true;
            }
        }

        return false;
    }

    private boolean hasCorrectTool(Block block, ItemStack tool) {
        Material blockType = block.getType();
        Material toolType = tool.getType();

        // basic pickaxe checks
        if (blockType.name().contains("ORE") || blockType == Material.STONE) {
            return toolType.name().endsWith("_PICKAXE");
        }

        // wood can be mined with axes
        if (blockType.name().contains("WOOD") || blockType.name().contains("LOG") || blockType.name().contains("PLANKS") || blockType.name().contains("STRIPPED_")) {
            return toolType.name().endsWith("_AXE");
        }

        // other blocks can be broken by hand (optional)
        return true;
    }

    private int getXpForBlock(Material type) {
        switch (type) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                return ThreadLocalRandom.current().nextInt(0, 3);
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case NETHER_QUARTZ_ORE:
            case NETHER_GOLD_ORE:
                return ThreadLocalRandom.current().nextInt(3, 8);
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                return ThreadLocalRandom.current().nextInt(1, 5);
            default:
                return 0;
        }
    }

    public void loadHarvestableBlocks(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("harvestable-blocks");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                Material mat = Material.valueOf(key.toUpperCase());
                int regenTime = section.getInt(key);
                harvestableRegenTimes.put(mat, regenTime);
            } catch (Exception ex) {
                Bukkit.getLogger().warning("Invalid material in harvestable-blocks: " + key);
            }
        }
    }


}
