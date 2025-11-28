package net.mcoasis.mcohexroyale.events.listeners.custom.lazyselection;

import me.ericdavis.lazySelection.LocationType;
import me.ericdavis.lazySelection.events.LazyPointsCompleteEvent;
import me.ericdavis.lazySelection.events.LocationSetEvent;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
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
    public void onLocationSet(LocationSetEvent e) {
        if (e.getLocationType() == LocationType.AREA) return;
        if (e.getCollection() == null) return;

        UUID playerId = e.getPlayer().getUniqueId();

        // check if the player is setting a flag
        HashMap<UUID, HexTile> playerSettingFlag = HexManager.getInstance().getPlayerSettingFlag();
        if (playerSettingFlag.containsKey(playerId)){
            handleSettingFlag(e, playerSettingFlag, playerId);
            return;
        }
    }

    @EventHandler
    public void onPointsCompleted(LazyPointsCompleteEvent e) {
        UUID playerId = e.getPlayer().getUniqueId();

        // check if the player is setting a team spawn
        HashMap<UUID, HexTeam.TeamColor> settingTeamSpawns = HexManager.getInstance().getSettingTeamSpawns();
        if (settingTeamSpawns.containsKey(playerId)){
            handleSettingTeamSpawns(e, settingTeamSpawns, playerId);
            return;
        }
    }

    private void handleSettingTeamSpawns(LazyPointsCompleteEvent e, HashMap<UUID, HexTeam.TeamColor> settingTeamSpawns, UUID playerId) {
        List<Location> spawnPoints = e.getPoints();

        HexTeam.TeamColor teamColor = settingTeamSpawns.get(playerId);
        Set<Location> spawnLocations = HexManager.getInstance().getTeam(teamColor).getSpawnLocations();

        spawnLocations.clear();
        spawnLocations.addAll(spawnPoints);

        MCOHexRoyale.getInstance().clearSpawns(teamColor);

        for (Location loc : spawnPoints) {
            MCOHexRoyale.getInstance().saveSpawn(teamColor, loc);
        }

        settingTeamSpawns.remove(playerId);

        e.getPlayer().sendMessage(ChatColor.AQUA + "Successfully set " + ChatColor.LIGHT_PURPLE + spawnPoints.size() + ChatColor.AQUA + " spawn point(s) for "
                + teamColor.getColor() + teamColor.getName() + " Team");
    }

    private void handleSettingFlag(LocationSetEvent e, HashMap<UUID, HexTile> playerSettingFlag, UUID playerId) {
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

















