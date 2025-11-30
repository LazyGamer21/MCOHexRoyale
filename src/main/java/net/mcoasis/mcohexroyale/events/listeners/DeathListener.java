package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();

        stuff(p);

        HexManager.getInstance().getSettingTeamSpawns().remove(playerId);
        HexManager.getInstance().getPlayerSettingFlag().remove(playerId);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        stuff(e.getEntity());
        e.getDrops().clear();
    }

    private void stuff(Player p) {
        HexTeam team = HexManager.getInstance().getPlayerTeam(p);

        if (team == null) return;

        p.getInventory().clear();

        team.getMembersAlive().put(p.getUniqueId(), false);

        team.checkTeamLoss(false);
    }

}
