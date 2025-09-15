package net.mcoasis.mcohexroyale.hexagonal;

import java.util.*;

import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColor;
import org.bukkit.Bukkit;
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
        Bukkit.broadcastMessage(color.getColor() + "created a new team because couldn't find bruh");
        return new HexTeam(color);
    }

    /**
     *
     * @param player The player retrieving the team for
     * @return The first {@link HexTeam} found that has this player in its members
     */
    public HexTeam getPlayerTeam(Player player) {
        for (HexTeam team : teams) {
            if (team.getMembersAlive().containsKey(player)) {
                return team;
            }
        }
        return null;
    }

    // -- == HexTeam Stuff == --

    // -- == HexTile Stuff == --

    private HashMap<UUID, HexTile> settingTileFlag = new HashMap<>();

    public HashMap<UUID, HexTile> getPlayerSettingFlag() {
        return settingTileFlag;
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
        getTeam(TeamColor.BLUE).setBaseLocation(new HexTile(-1, -2, getTeam(TeamColor.BLUE))); // BLUE Team
        new HexTile(-1, 1, null);
        new HexTile(-1, 2, null);

        new HexTile(-2, 0, null);
        new HexTile(-2, 1, null);
        new HexTile(-2, 2, null);

        getTeam(TeamColor.GREEN).setBaseLocation(new HexTile(-3, 2, getTeam(TeamColor.GREEN))); // GREEN Team
    }

    public void resetAllTiles() {
        Bukkit.broadcastMessage("reset all tiles -- not implemented yet");
        Bukkit.getLogger().info("reset all tiles -- not implemented yet");
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

    //! not working right now -- false positive -- might be getNeighbors or just the function, both were made with AI -- try using dfs function in this function
    public boolean canCapture(HexTeam team, HexTile startTile) {
        HexTile base = team.getBaseTile();

        if (base == null) {
            Bukkit.getLogger().severe("HexTeam (" + team.getTeamColor().getName() + ") is missing a base tile!");
            return false;
        }

        //! debugging
        //Bukkit.broadcastMessage("testing base tile: " + base.getQ() + ", " + base.getR() + " with tile: " + startTile.getQ() + ", " + startTile.getR());

        TeamColor teamColor = team.getTeamColor();

        // Quick return: if the tile is already the team's color, then just use areConnected
        if (startTile.getCurrentTeam() != null && startTile.getCurrentTeam().getTeamColor().equals(teamColor)) {
            //! debugging
            //Bukkit.broadcastMessage("quick return");
            return areConnected(startTile, base);
        }

        // Otherwise, run a search where:
        // - The first tile (startTile) is allowed to be any color
        // - All other tiles in the path must match the team color
        Set<HexTile> visited = new HashSet<>();
        Deque<HexTile> stack = new ArrayDeque<>();
        stack.push(startTile);
        visited.add(startTile);

        while (!stack.isEmpty()) {
            HexTile current = stack.pop();

            if (current.equals(base)) {
                //! debugging
                //Bukkit.broadcastMessage("path found");
                return true; // Path found!
            }

            for (HexTile neighbor : current.getNeighbors()) {
                if (visited.contains(neighbor)) continue;

                // If we're leaving the start tile, enforce teamColor
                if (!neighbor.equals(startTile) && neighbor.getCurrentTeam() != null && !neighbor.getCurrentTeam().getTeamColor().equals(teamColor)) {
                    continue;
                }

                visited.add(neighbor);
                stack.push(neighbor);
            }
        }

        return false; // No valid path to base
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
}