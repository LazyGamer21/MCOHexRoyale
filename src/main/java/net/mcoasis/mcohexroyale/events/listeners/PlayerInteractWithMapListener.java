package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractWithMapListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        // Ignore left-clicking blocks (which is used for breaking)
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        // Stop harvesting sweet berries, glow berries, cave vines, etc.
        Material blockType = e.getClickedBlock().getType();

        // Sweet berry bush
        if (blockType == Material.SWEET_BERRY_BUSH) {
            e.setCancelled(true);
            return;
        }

        // Glow berries & cave vines (hanging berries)
        if (blockType == Material.CAVE_VINES || blockType == Material.CAVE_VINES_PLANT) {
            e.setCancelled(true);
            return;
        }

        // Pitcher pods (1.20+), bamboo shoots, etc — fully unharvestable
        if (blockType == Material.PITCHER_CROP || blockType == Material.PITCHER_PLANT) {
            e.setCancelled(true);
            return;
        }


        if (e.getAction() == Action.PHYSICAL) {
            if (e.getClickedBlock().getType() == Material.FARMLAND) {
                e.setCancelled(true);
            }
        }

        // do not allow stripping logs
        if (isStrippable(e.getClickedBlock().getType())) {
            // check if it is a right click with an axe
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Material itemInHand = e.getPlayer().getInventory().getItemInMainHand().getType();
                if (itemInHand.toString().contains("AXE")) {
                    e.setCancelled(true);
                }
            }
        }

        // if the player is an admin, allow interaction
        boolean isAdmin = e.getPlayer().hasPermission("hexroyale.admin");

        boolean isButton = e.getClickedBlock().getType().toString().endsWith("BUTTON");
        boolean isLever = e.getClickedBlock().getType().toString().contains("LEVER");
        boolean isTrapdoor = e.getClickedBlock().getType().toString().endsWith("TRAPDOOR");
        boolean isDoor = e.getClickedBlock().getType().toString().endsWith("DOOR");

        if ((isButton || isLever || isTrapdoor || isDoor) && !isAdmin) {
            if (!WorldManager.getInstance().getPlacedGameBlocks().contains(e.getClickedBlock().getLocation())) {
                e.setCancelled(true);
            }
        }

        if (GameManager.getInstance().getGameState() == GameManager.GameState.INGAME) {
            // get the closest HexTile to block being interacted with

            Location location = e.getClickedBlock().getLocation();

            double closestDistance = Double.MAX_VALUE;
            HexTile closestTile = null;
            for (HexTile tile : HexManager.getInstance().getHexGrid()) {
                if (tile.getFlagLocation() == null) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.getName().contains("LazyGamer21")) continue;
                        p.sendMessage("flag location missing for: " + tile.getQ() + ", " + tile.getR());;
                    }
                    continue;
                }
                if (tile.getFlagLocation().getWorld() != location.getWorld()) continue;
                double distance = location.distance(tile.getFlagLocation());
                if (distance > closestDistance) continue;
                closestDistance = distance;
                closestTile = tile;
            }

            if (closestTile != null) {
                HexTeam team = HexManager.getInstance().getPlayerTeam(e.getPlayer());

                if (!closestTile.teamOwns(team)) {
                    // if the player is interacting with a furnace
                    if (e.getClickedBlock().getType().toString().contains("FURNACE")) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.RED + "You can't use that!");
                    }
                }
            }



        }

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Entity entity = event.getEntity();
        Player player = (Player) event.getDamager();

        // Stop breaking Armor Stands or Item Frames
        if (entity instanceof ArmorStand || entity instanceof ItemFrame) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't break that!");
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player p)) return;

        // Stop breaking paintings, item frames, etc.
        event.setCancelled(true);
        p.sendMessage(ChatColor.RED + "You can't break that!");
    }

    public static boolean isStrippable(Material block) {
        if (block == null) return false;

        String name = block.name();

        return name.endsWith("_LOG")
                || name.endsWith("_WOOD")
                || name.endsWith("_STEM")
                || name.endsWith("_HYPHAE");
    }

}
