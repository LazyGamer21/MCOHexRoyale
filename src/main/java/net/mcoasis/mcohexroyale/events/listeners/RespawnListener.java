package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        HexTeam team = HexManager.getInstance().getPlayerTeam(p);

        if (team == null) return;
        if (team.hasBaseCaptured()) {
            p.setGameMode(GameMode.SPECTATOR);

            int respawnTimer = 5;

            p.sendMessage(ChatColor.GRAY + "Respawning in " + respawnTimer + " seconds!");
            Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> {
                team.getBaseTile().teleportToBase(p);
                p.setGameMode(GameMode.SURVIVAL);
                setKit(p);
                team.getMembersAlive().put(p, true);
            }, 20L * respawnTimer);
            return;
        }

        p.setGameMode(GameMode.SPECTATOR);
        team.getMembersAlive().put(p, false);
        p.sendMessage(ChatColor.RED + "Your team does not have their flag! Wait to respawn until it is recaptured!");


    }

    private void setKit(Player p) {
        PlayerInventory inv = p.getInventory();

        inv.clear();

        // set armor
        inv.setHelmet(new ItemStack(Material.IRON_HELMET));
        inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        inv.setBoots(new ItemStack(Material.IRON_BOOTS));

        // shield
        inv.setItemInOffHand(new ItemStack(Material.SHIELD));

        // weapons
        inv.setItem(0, new ItemStack(Material.IRON_AXE));
        inv.setItem(1, new ItemStack(Material.IRON_SWORD));

        // food
        inv.setItem(8, new ItemStack(Material.BREAD, 8));
    }

}