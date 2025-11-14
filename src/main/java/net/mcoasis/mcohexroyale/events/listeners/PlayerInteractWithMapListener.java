package net.mcoasis.mcohexroyale.events.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractWithMapListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        boolean isButton = e.getClickedBlock().getType().toString().endsWith("BUTTON");
        boolean isLever = e.getClickedBlock().getType().toString().contains("LEVER");
        boolean isTrapdoor = e.getClickedBlock().getType().toString().endsWith("TRAPDOOR");
        boolean isDoor = e.getClickedBlock().getType().toString().endsWith("DOOR");

        // if player is an operator
        boolean isAdmin = e.getPlayer().hasPermission("hexroyale.admin");

        if (!isAdmin) {
            if (isButton || isLever || isTrapdoor || isDoor) {
                e.setCancelled(true);
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
        if (!(event.getRemover() instanceof Player)) return;

        Entity entity = event.getEntity();
        Player player = (Player) event.getRemover();

        // Stop breaking paintings, item frames, etc.
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You can't break that!");
    }

}
