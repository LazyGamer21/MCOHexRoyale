package net.mcoasis.mcohexroyale.items;

import me.ericdavis.lazyItems.AbstractCustomItem;
import me.ericdavis.lazyItems.LazyItems;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TrackingCompass extends AbstractCustomItem {

    BukkitRunnable compassUpdater;

    // Currently tracked target per player using this compass
    private final Map<UUID, UUID> tracking = new HashMap<>();

    public TrackingCompass() {
        super(
                new NamespacedKey(LazyItems.getInstance().getPlugin(), "tracking_compass"),
                Material.COMPASS,
                420,
                ChatColor.LIGHT_PURPLE + "Player Tracker",
                List.of(
                        ChatColor.GRAY + "Right-click to track the nearest enemy",
                        ChatColor.GRAY + "Sneak + right-click to stop tracking",
                        "",
                        ChatColor.DARK_PURPLE + "Only tracks enemies without their base!"
                )
        );

        setCooldown(500);
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();

        if (item.getType() == Material.COMPASS) {
            CompassMeta meta = (CompassMeta) item.getItemMeta();
            if (meta != null) {
                meta.setLodestoneTracked(false);
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    @Override
    public void onRightClick(Player player) {

        if (player.isSneaking()) {
            stopTracking(player);
            player.sendMessage(ChatColor.RED + "Stopped tracking.");
            return;
        }

        if (hasCooldown(player)) {
            player.sendMessage(ChatColor.RED + "Tracker is on cooldown!");
            return;
        }

        Player target = findNearestPlayer(player);

        if (target == null || target.equals(player)) {
            player.sendMessage(ChatColor.RED + "No players found to track!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
            return;
        }

        startTracking(player, target);
        applyCooldown(player);

        player.sendMessage(ChatColor.GREEN + "Now tracking: " + ChatColor.AQUA + target.getName());
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.5f);
    }

    private Player findNearestPlayer(Player source) {
        Player nearest = null;
        double nearestDist = Double.MAX_VALUE;

        Location sourceLoc = source.getLocation();

        for (Player p : Bukkit.getOnlinePlayers()) {
            HexTeam playerTeam = HexManager.getInstance().getPlayerTeam(source);
            HexTeam targetTeam = HexManager.getInstance().getPlayerTeam(p);
            if (targetTeam == null) continue;
            if (playerTeam != null && playerTeam.getTeamColor() == targetTeam.getTeamColor()) continue;
            if (targetTeam.hasBaseOwnership()) continue;
            if (p.equals(source)) continue;
            if (p.getWorld() != source.getWorld()) continue; // Same world only (optional)
            if (p.getGameMode() == GameMode.SPECTATOR) continue;

            double dist = p.getLocation().distanceSquared(sourceLoc);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = p;
            }
        }
        return nearest;
    }

    private void startTracking(Player tracker, Player target) {
        stopTracking(tracker);
        if (compassUpdater != null && !compassUpdater.isCancelled()) compassUpdater.cancel();

        tracking.put(tracker.getUniqueId(), target.getUniqueId());

        // Start updating compass + action bar
        compassUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                HexTeam targetTeam = HexManager.getInstance().getPlayerTeam(target);

                if (!tracker.isOnline() || !target.isOnline()
                        || !tracking.containsKey(tracker.getUniqueId())
                        || targetTeam == null || targetTeam.hasBaseOwnership()) {
                    cancel();
                    return;
                }

                Location targetLoc = target.getLocation();
                tracker.setCompassTarget(targetLoc);

                // Action bar message
                double distance = tracker.getLocation().distance(targetLoc);
                String msg = ChatColor.LIGHT_PURPLE + "Tracking: " +
                        ChatColor.AQUA + target.getName() +
                        ChatColor.GRAY + " §l| §7" +
                        String.format("%.1f blocks", distance);

                // if the compass is in the main hand, set it to the target location
                ItemStack compassMainHand = tracker.getInventory().getItemInMainHand();
                if (AbstractCustomItem.isCustomItem(compassMainHand, TrackingCompass.class)) {
                    CompassMeta meta = (CompassMeta) compassMainHand.getItemMeta();
                    if (meta != null) {
                        meta.setLodestone(targetLoc);
                        compassMainHand.setItemMeta(meta);
                    }
                    // only send the message if they are holding tracker
                    tracker.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                }

                // if the compass is in the offhand, set it to the target location
                ItemStack compassOffHand = tracker.getInventory().getItemInOffHand();
                if (AbstractCustomItem.isCustomItem(compassOffHand, TrackingCompass.class)) {
                    CompassMeta meta = (CompassMeta) compassOffHand.getItemMeta();
                    if (meta != null) {
                        meta.setLodestone(targetLoc);
                        compassOffHand.setItemMeta(meta);
                    }
                    // only send the message if they are holding tracker
                    tracker.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                }
            }
        };

        compassUpdater.runTaskTimer(LazyItems.getInstance().getPlugin(), 0L,
                MCOHexRoyale.getInstance().getConfig().getInt("tracking-compass-update-time", 20)); // Update every 0.5 seconds
    }

    private void stopTracking(Player player) {
        UUID targetId = tracking.remove(player.getUniqueId());
        if (targetId != null) {
            Player target = Bukkit.getPlayer(targetId);
            if (target != null && target.isOnline()) {
                player.setCompassTarget(player.getWorld().getSpawnLocation());
            }
        }
    }

    // Optional: Clean up on player quit / death
    public void cleanup(Player player) {
        tracking.remove(player.getUniqueId());
    }
}
