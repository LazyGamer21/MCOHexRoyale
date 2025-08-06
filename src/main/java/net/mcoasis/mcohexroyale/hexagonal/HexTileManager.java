package net.mcoasis.mcohexroyale.hexagonal;

import java.util.*;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColors;

public class HexTileManager {

    private static HexTileManager instance;

    private HexTileManager() {}

    public static HexTileManager getInstance() {
        if (instance == null) {
            instance = new HexTileManager();
        }
        return instance;
    }

    public void populateGrid() {
        new HexTile(3, -2, TeamColors.YELLOW); // YELLOW Team

        new HexTile(2, 0, null);
        new HexTile(2, -1, null);
        new HexTile(2, -2, null);

        new HexTile(1, 0, TeamColors.RED); // RED Team
        new HexTile(1, -1, null);
        new HexTile(1, -2, null);
        new HexTile(1, 1, null);
        new HexTile(1, 2, null);

        new HexTile(0, 0, null);
        new HexTile(0, -1, null);
        new HexTile(0, -2, null);
        new HexTile(0, 1, null);
        new HexTile(0, 2, null);

        new HexTile(-1, 0, TeamColors.BLUE); // BLUE Team
        new HexTile(-1, -1, null);
        new HexTile(-1, -2, null);
        new HexTile(-1, 1, null);
        new HexTile(-1, 2, null);

        new HexTile(-2, 0, null);
        new HexTile(-2, 1, null);
        new HexTile(-2, 2, null);

        new HexTile(-3, 2, TeamColors.GREEN); // GREEN Team
    }

    private Map<HexCoordinate, TeamColors> hexGrid = new HashMap<>();
    private Set<HexCoordinate> visited = new HashSet<>();

    public void setHex(int q, int r, TeamColors color) {
        hexGrid.put(new HexCoordinate(q, r), color);
    }

    public HexTile findHexTile(Set<HexTile> tiles, int q, int r) {
        for (HexTile tile : tiles) {
            if (tile.getHexCoordinate().getQ() == q && tile.getHexCoordinate().getR() == r) {
                return tile;
            }
        }
        return null; // Not found
    }

    // Perform DFS to find if there's a path from start to end with the same color
    public boolean areConnected(HexCoordinate start, HexCoordinate end) {
        // Ensure the start and end are within the grid
        if (!hexGrid.containsKey(start) || !hexGrid.containsKey(end)) {
            return false;
        }

        // Get the color of the start tile
        TeamColors targetColor = hexGrid.get(start);
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
    private boolean dfs(HexCoordinate current, HexCoordinate end, TeamColors targetColor) {
        // If we reached the target, return true
        if (current.equals(end)) {
            return true;
        }

        // Mark the current tile as visited
        visited.add(current);

        // Explore neighbors
        for (int[] direction : DIRECTIONS) {
            int nq = current.q + direction[0];
            int nr = current.r + direction[1];
            HexCoordinate neighbor = new HexCoordinate(nq, nr);

            // Check if the neighbor is in the grid, has the same color, and hasn't been visited yet
            if (hexGrid.containsKey(neighbor) && !visited.contains(neighbor) && hexGrid.get(neighbor) == targetColor) {
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
}