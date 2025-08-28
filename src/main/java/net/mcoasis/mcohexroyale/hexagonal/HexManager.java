package net.mcoasis.mcohexroyale.hexagonal;

import java.util.*;

import net.mcoasis.mcohexroyale.datacontainers.PlayerFlagData;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColor;
import net.mcoasis.mcohexroyale.listeners.PlayerInteractListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HexManager {

    // -- == Singleton Stuff == --

    private static HexManager instance;

    private HexManager() {}

    public static HexManager getInstance() {
        if (instance == null) {
            instance = new HexManager();
        }
        return instance;
    }

    // -- == Singleton Stuff == --

    // -- == HexTeam Stuff == --

    private final Set<HexTeam> teams = new HashSet<>();

    /**
     *
     * @param color The {@link TeamColor} of the team to retrieve
     * @return The {@link HexTeam} of that {@link TeamColor} if it exists -- A new {@link HexTeam} of that {@link TeamColor} if it does not exist
     */
    public HexTeam getTeam(TeamColor color) {
        for (HexTeam team : teams) {
            if (team.getTeamColor().equals(color)) return team;
        }
        return new HexTeam(color);
    }

    /**
     *
     * @param player The player retrieving the team for
     * @return The first {@link HexTeam} found that has this player in its members
     */
    public HexTeam getPlayerTeam(Player player) {
        for (HexTeam team : teams) {
            if (team.getMembers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    // -- == HexTeam Stuff == --

    // -- == HexTile Stuff == --

    private HashMap<UUID, PlayerFlagData> settingTileFlag = new HashMap<>();

    /**
     *
     * @param player
     * @param tile
     * @param settingPole If the player is setting the pole -- if false it means they're setting the flag.
     *                    This will be taken out since in the future they will always be setting the pole.
     *                    Flags will eventually set their own non-poles (the flag part) from the WorldEdit schematics
     */
    public void setPlayerSettingFlag(Player player, HexTile tile, boolean settingPole) {
        settingTileFlag.put(player.getUniqueId(), new PlayerFlagData(tile, settingPole));
        player.sendMessage(ChatColor.GRAY + "Punch a Block to set Flag Corner 1 for Tile (" + ChatColor.YELLOW + tile.getQ() + ", " + tile.getR() + ChatColor.GRAY + ") -- Right-Click to Cancel");
    }

    /**
     * @implNote Populates the {@link HexManager}'s hexGrid Set with new {@link HexTile}s while also
     * giving the 4 corners their respective {@link HexTeam}s
     */
    public void populateGrid() {
        getTeam(TeamColor.YELLOW).setBaseLocation(new HexTile(3, -2, getTeam(TeamColor.YELLOW))); // YELLOW Team

        new HexTile(2, 0, null);
        new HexTile(2, -1, null);
        new HexTile(2, -2, null);

        new HexTile(1, 0, null);
        new HexTile(1, -1, null);
        new HexTile(1, -2, null);
        new HexTile(1, 1, null);
        getTeam(TeamColor.RED).setBaseLocation(new HexTile(1, 2, getTeam(TeamColor.RED))); // RED Team

        new HexTile(0, 0, null);
        new HexTile(0, -1, null);
        new HexTile(0, -2, null);
        new HexTile(0, 1, null);
        new HexTile(0, 2, null);

        new HexTile(-1, 0, null);
        new HexTile(-1, -1, null);
        getTeam(TeamColor.RED).setBaseLocation(new HexTile(-1, -2, getTeam(TeamColor.BLUE))); // BLUE Team
        new HexTile(-1, 1, null);
        new HexTile(-1, 2, null);

        new HexTile(-2, 0, null);
        new HexTile(-2, 1, null);
        new HexTile(-2, 2, null);

        getTeam(TeamColor.GREEN).setBaseLocation(new HexTile(-3, 2, getTeam(TeamColor.GREEN))); // GREEN Team
    }

    private final Set<HexTile> hexGrid = new HashSet<>();
    private final Set<HexTile> visited = new HashSet<>();

    /**
     *
     * @param q Q axial coordinate for the desired tile
     * @param r R axial coordinate for the desired tile
     * @return The {@link HexTile} assigned to the provided axial coordinates
     */
    public HexTile getHexTile(int q, int r) {
        for (HexTile tile : hexGrid) {
            if (tile.getQ() == q && tile.getR() == r) {
                return tile;
            }
        }
        return null; // Not found
    }

    private final int[][] DIRECTIONS = {
            {1, 0},   // East
            {1, -1},  // Southeast
            {0, -1},  // Southwest
            {-1, 0},  // West
            {-1, 1},  // Northwest
            {0, 1}    // Northeast
    };

    public boolean areConnected(HexTile start, HexTile end) {
        // Ensure the start and end are within the grid
        if (!hexGrid.contains(start) || !hexGrid.contains(end)) {
            return false;
        }
        if (start.getCurrentTeam() == null || end.getCurrentTeam() == null) {
            return false;
        }

        // Both must be the same color
        TeamColor targetColor = start.getCurrentTeam().getTeamColor();
        if (end.getCurrentTeam().getTeamColor() != targetColor) {
            return false;
        }

        visited.clear();
        return dfs(start, end, targetColor);
    }

    private boolean dfs(HexTile current, HexTile end, TeamColor targetColor) {
        if (current.equals(end)) {
            return true;
        }

        visited.add(current);

        for (int[] direction : DIRECTIONS) {
            int nq = current.getQ() + direction[0];
            int nr = current.getR() + direction[1];

            // Lookup neighbor instead of creating a new HexTile
            HexTile neighbor = getHexTile(nq, nr);

            if (neighbor != null
                    && !visited.contains(neighbor)
                    && neighbor.getCurrentTeam() != null
                    && neighbor.getCurrentTeam().getTeamColor() == targetColor) {

                if (dfs(neighbor, end, targetColor)) {
                    return true;
                }
            }
        }

        return false;
    }

    // -- == HexTile Stuff == --

    // -- == Getters + Setters == --

    public Set<HexTile> getHexGrid() {
        return hexGrid;
    }

    public Set<HexTeam> getTeams() {
        return teams;
    }

    public HashMap<UUID, PlayerFlagData> getSettingTileFlag() {
        return settingTileFlag;
    }
}