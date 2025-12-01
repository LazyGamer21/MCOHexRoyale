package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EntityDamageEntityListener implements Listener {

    /***
     * this is only used for sudden death, pvp is handled elsewhere if needed
     */
    public static boolean pvpEnabled = true;

    @EventHandler
    public void onEntityDamageEntity(org.bukkit.event.entity.EntityDamageByEntityEvent e) {

        if (!(e.getDamager() instanceof Player attacker)) return;

        if (GameManager.getInstance().getGameState() != GameManager.GameState.INGAME) {
            if (!attacker.hasPermission("hexroyale.admin")) {
                e.setCancelled(true);
                return;
            }
        }

        if (!(e.getEntity() instanceof Player damaged)) return;

        // if it is a player damaging another player on the same HexTeam then cancel the event
        if (!pvpEnabled) {
            e.setCancelled(true);
            return;
        }
        HexTeam attackerTeam = HexManager.getInstance().getPlayerTeam(attacker);
        HexTeam damagedTeam = HexManager.getInstance().getPlayerTeam(damaged);
        if (attackerTeam != null && attackerTeam.equals(damagedTeam)) {
            e.setCancelled(true);
        }

        // if the damaged player dies to the attacking player, give the attacker the loot
        if (e.getFinalDamage() >= damaged.getHealth()) {
            giveLoot(attacker, damaged);
        }
    }

    private void giveLoot(Player attacker, Player victim) {
        PlayerInventory attackerInv = attacker.getInventory();
        PlayerInventory victimInv = victim.getInventory();

        Material[] lootMaterials = {
                Material.COBBLESTONE,
                Material.COAL,
                Material.RAW_IRON,
                Material.IRON_INGOT,
                Material.RAW_GOLD,
                Material.GOLD_INGOT,
                Material.DIAMOND,
                Material.SUNFLOWER
        };

        for (ItemStack item : victimInv.getContents()) {
            if (item == null) continue;
            boolean isLoot = false;
            for (Material lootMaterial : lootMaterials) {
                if (item.getType() == lootMaterial) {
                    isLoot = true;
                    break;
                }
            }
            if (!isLoot) continue;
            attackerInv.addItem(item);
            victimInv.remove(item);
        }
    }

}