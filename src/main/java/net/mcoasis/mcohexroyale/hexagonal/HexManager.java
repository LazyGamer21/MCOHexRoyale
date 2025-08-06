package net.mcoasis.mcohexroyale.hexagonal;

import java.util.*;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColor;
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

    /**
     * @implNote Populates the {@link HexManager}'s hexGrid Set with new {@link HexTile}s while also
     * giving the 4 corners their respective {@link HexTeam}s
     */
    public void populateGrid() {
        new HexTile(3, -2, getTeam(TeamColor.YELLOW)); // YELLOW Team

        new HexTile(2, 0, null);
        new HexTile(2, -1, null);
        new HexTile(2, -2, null);

        new HexTile(1, 0, getTeam(TeamColor.RED)); // RED Team
        new HexTile(1, -1, null);
        new HexTile(1, -2, null);
        new HexTile(1, 1, null);
        new HexTile(1, 2, null);

        new HexTile(0, 0, null);
        new HexTile(0, -1, null);
        new HexTile(0, -2, null);
        new HexTile(0, 1, null);
        new HexTile(0, 2, null);

        new HexTile(-1, 0, getTeam(TeamColor.BLUE)); // BLUE Team
        new HexTile(-1, -1, null);
        new HexTile(-1, -2, null);
        new HexTile(-1, 1, null);
        new HexTile(-1, 2, null);

        new HexTile(-2, 0, null);
        new HexTile(-2, 1, null);
        new HexTile(-2, 2, null);

        new HexTile(-3, 2, getTeam(TeamColor.GREEN)); // GREEN Team
    }

    private final Set<HexTile> hexGrid = new HashSet<>();
    private final Set<HexTile> visited = new HashSet<>();

    public HexTile getHexTile(int q, int r) {
        for (HexTile tile : hexGrid) {
            if (tile.getQ() == q && tile.getR() == r) {
                return tile;
            }
        }
        return null; // Not found
    }

    // Perform DFS to find if there's a path from start to end with the same color
    public boolean areConnected(HexTile start, HexTile end) {
        // Ensure the start and end are within the grid
        if (!hexGrid.contains(start) || !hexGrid.contains(end)) {
            return false;
        }

        // Get the color of the start tile
        TeamColor targetColor = start.getCurrentTeam().getTeamColor();
        visited.clear(); // Clear visited set to ensure fresh DFS
        return dfs(start, end, targetColor);
    }


    private final int[][] DIRECTIONS = {
            {1, 0},   // East
            {1, -1},  // Southeast
            {0, -1},  // Southwest
            {-1, 0},  // West
            {-1, 1},  // Northwest
            {0, 1}    // Northeast
    };

    // Depth-First Search to find a connected path
    private boolean dfs(HexTile current, HexTile end, TeamColor targetColor) {
        // If we reached the target, return true
        if (current.equals(end)) {
            return true;
        }

        // Mark the current tile as visited
        visited.add(current);

        // Explore neighbors
        for (int[] direction : DIRECTIONS) {
            int nq = current.getQ() + direction[0];
            int nr = current.getR() + direction[1];
            HexTile neighbor = new HexTile(nq, nr);

            // Check if the neighbor is in the grid, has the same color, and hasn't been visited yet
            if (hexGrid.contains(neighbor) && !visited.contains(neighbor) && neighbor.getCurrentTeam().getTeamColor() == targetColor) {
                // Recursively check if the path exists through this neighbor
                if (dfs(neighbor, end, targetColor)) {
                    return true;
                }
            }
        }

        // If no path is found, return false
        return false;
    }

    // Example usage
    /*public static void main(String[] args) {
        HexTileManager grid = HexTileManager.getInstance();
        grid.populateGrid();

        // Example of checking if two hexagons are connected by the same color
        HexCoordinate start = new HexCoordinate(1, 0);
        HexCoordinate end = new HexCoordinate(1, -2);
        System.out.println("Hex (1,0) and Hex (1,-2) connected: " + grid.areConnected(start, end)); // true (RED path)

        start = new HexCoordinate(1, 0);
        end = new HexCoordinate(-1, -2);
        System.out.println("Hex (1,0) and Hex (-1,-2) connected: " + grid.areConnected(start, end)); // false (RED vs BLUE)
    }*/

    // -- == HexTile Stuff == --

    // -- == Getters + Setters == --

    public Set<HexTile> getHexGrid() {
        return hexGrid;
    }

    public Set<HexTeam> getTeams() {
        return teams;
    }
}