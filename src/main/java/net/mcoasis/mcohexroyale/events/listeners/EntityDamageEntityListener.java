package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityDamageEntityListener implements Listener {

    @EventHandler
    public void onEntityDamageEntity(org.bukkit.event.entity.EntityDamageByEntityEvent e) {
        // if it is a player damaging another player on the same HexTeam then cancel the event
        if (e.getDamager() instanceof Player attacker && e.getEntity() instanceof Player damaged) {
            HexTeam attackerTeam = HexManager.getInstance().getPlayerTeam(attacker);
            HexTeam damagedTeam = HexManager.getInstance().getPlayerTeam(damaged);
            if (attackerTeam != null && attackerTeam.equals(damagedTeam)) {
                e.setCancelled(true);
            }
        }
    }

}