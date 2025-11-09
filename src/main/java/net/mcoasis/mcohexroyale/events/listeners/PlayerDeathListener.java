package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.events.TeamLossEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        HexTeam team = HexManager.getInstance().getPlayerTeam(p);

        if (team == null) return;

        e.setKeepInventory(true);

        team.getMembersAlive().put(p, false);

        team.checkTeamLoss();

    }

}
