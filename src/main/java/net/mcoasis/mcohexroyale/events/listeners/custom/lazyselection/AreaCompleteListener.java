package net.mcoasis.mcohexroyale.events.listeners.custom.lazyselection;

import me.ericdavis.lazySelection.events.LazyAreaCompleteEvent;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class AreaCompleteListener implements Listener {

    @EventHandler
    public void onAreaSet(LazyAreaCompleteEvent e) {
        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();
        HashMap<UUID, HexTile> playerSettingFlag = HexManager.getInstance().getPlayerSettingFlag();

        if (!playerSettingFlag.containsKey(playerId)) return;

        HexTile tile = playerSettingFlag.get(playerId);
        playerSettingFlag.remove(playerId);

        boolean sameX = e.getPoint1().getBlockX() == e.getPoint2().getBlockX();
        boolean sameZ = e.getPoint1().getBlockZ() == e.getPoint2().getBlockZ();

        if (sameX && sameZ) {
            int blockY1 = e.getPoint1().getBlockY();
            int blockY2 = e.getPoint2().getBlockY();

            // ensure block1 is the lower point
            if (blockY1 < blockY2) tile.setFlagPole(e.getPoint1(), e.getPoint2());
            else tile.setFlagPole(e.getPoint2(), e.getPoint1());

            // checking if the flag is a base tile, if not then spawn the flag at the bottom
            boolean spawnAtTop = tile.getCurrentTeam() != null;
            if (tile.getHexFlag() == null) return;
            tile.getHexFlag().spawnFlag(spawnAtTop);

            MCOHexRoyale.getInstance().saveHexFlag(tile);
            return;
        }

        e.setCancelled(true);
        e.getPlayer().sendMessage(ChatColor.RED + "Flag not Set -- Points must have the same X and Z coordinates");
    }

}

















