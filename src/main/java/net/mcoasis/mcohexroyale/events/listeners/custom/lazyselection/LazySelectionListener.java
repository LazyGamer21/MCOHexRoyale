package net.mcoasis.mcohexroyale.events.listeners.custom.lazyselection;

import me.ericdavis.lazySelection.LocationType;
import me.ericdavis.lazySelection.events.LocationSetEvent;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class LazySelectionListener implements Listener {

    @EventHandler
    public void onFlagSet(LocationSetEvent e) {
        if (e.getLocationType() == LocationType.AREA) return;
        if (e.getCollection() == null) return;

        Player p = e.getPlayer();
        UUID playerId = p.getUniqueId();
        HashMap<UUID, HexTile> playerSettingFlag = HexManager.getInstance().getPlayerSettingFlag();

        if (!playerSettingFlag.containsKey(playerId)) return;

        HexTile tile = playerSettingFlag.get(playerId);

        List<Location> flagPoints = e.getCollection();
        Location currentLocation = e.getLocation();

        if (flagPoints.size() > 1) {
            Location matchLocation = flagPoints.getFirst();
            e.setLocation(new Location(matchLocation.getWorld(), matchLocation.getBlockX(), currentLocation.getBlockY(), matchLocation.getBlockZ()));
        } else return;

        e.setFinishSelection(true);
        playerSettingFlag.remove(playerId);

        List<Location> sorted = new ArrayList<>(flagPoints);

        sorted.add(e.getLocation());

        sorted = sorted.stream()
                .sorted(Comparator.comparingDouble(Location::getY)
                        .reversed()).toList();

        tile.setFlagTop(sorted.getFirst());
        tile.setFlagBottom(sorted.get(1));
        tile.setFlagBase(sorted.get(2));

        // checking if the flag is a base tile, if not then spawn the flag at the bottom
        boolean spawnAtTop = tile.getCurrentTeam() != null;
        if (tile.getHexFlag() == null) return;
        tile.getHexFlag().spawnFlag(spawnAtTop);

        MCOHexRoyale.getInstance().saveHexFlag(tile);
    }

}

















