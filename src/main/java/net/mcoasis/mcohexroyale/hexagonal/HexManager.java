package net.mcoasis.mcohexroyale.hexagonal;

import java.util.*;

import net.mcoasis.mcohexroyale.FlagLocPos;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.shop.BuyPage;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class HexManager {

    private static HexManager instance;

    private HexManager() {}

    public static HexManager getInstance() {
        if (instance == null) {
            instance = new HexManager();
        }
        return instance;
    }

    private final Set<HexTile> hexGrid = new HashSet<>();
    private final Set<HexTile> visited = new HashSet<>();
    private final Set<HexTeam> teams = new HashSet<>();
    private HashMap<UUID, HexTile> settingTileFlag = new HashMap<>();
    private HashMap<UUID, TeamColor> settingTeamSpawns = new HashMap<>();

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
    public @Nullable HexTeam getPlayerTeam(Player player) {
        for (HexTeam team : teams) {
            if (team.getMembersAlive().containsKey(player)) {
                return team;
            }
        }
        return null;
    }

    /**
     *
     * @param team The {@link HexTeam} to count the owned tiles for
     * @return Returns how many tiles are owned by the given {@link HexTeam}
     */
    public int getOwnedTiles(HexTeam team) {
        int count = 0;
        for (HexTile tile : hexGrid) {
            if (tile.getCurrentTeam() != null && tile.getCurrentTeam().equals(team) && tile.isCurrentTeamOwns()) {
                count++;
            }
        }
        return count;
    }

    /**
     * @implNote Clears the hex grid and populates it with new {@link HexTile}s while also
     * giving the 4 corners their respective {@link HexTeam}s
     */
    public void populateGrid() {
        clearGrid();

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

    /***
     * Clears the hexGrid while also deleting all now old flags
     */
    public void clearGrid() {
        for (HexTile tile : hexGrid) {
            tile.getHexFlag().removeFlag();
        }
        hexGrid.clear();
    }

    /***
     * load all hex flags from config and set their positions in the HexTiles
     */
    public void loadHexFlags() {
        MCOHexRoyale plugin = MCOHexRoyale.getInstance();
        FileConfiguration flagsConfig = plugin.getFlagsConfig();

        if (!flagsConfig.isConfigurationSection("flags")) {
            Bukkit.getLogger().warning("[MCOHexRoyale] Failed to loadHexFlags - config section 'flags' not found");
            return;
        }

        ConfigurationSection flagsSection = flagsConfig.getConfigurationSection("flags");
        if (flagsSection == null) {
            Bukkit.getLogger().warning("[MCOHexRoyale] Failed to loadHexFlags - config section 'flags' is null");
            return;
        }

        for (String key : flagsSection.getKeys(false)) {
            try {
                String[] parts = key.split("_");
                int q = Integer.parseInt(parts[0]);
                int r = Integer.parseInt(parts[1]);

                Map<FlagLocPos, Location> locs = loadFlagData(flagsConfig, "flags." + key);

                if (locs == null) {
                    Bukkit.getLogger().warning("[HexRoyale] Failed to load flag positions from config!");
                    return;
                }

                Location top = locs.get(FlagLocPos.TOP);
                Location bottom = locs.get(FlagLocPos.BOTTOM);
                Location base = locs.get(FlagLocPos.BASE);

                if (base != null && top != null) {
                    HexTile tile = HexManager.getInstance().getHexTile(q, r);
                    if (tile != null) {
                        tile.setFlagPositions(top, bottom, base);
                        boolean spawnAtTop = tile.getCurrentTeam() != null;
                        if (tile.getHexFlag() != null) tile.getHexFlag().spawnFlag(spawnAtTop);

                        Bukkit.getLogger().info("[HexRoyale] Loaded flag from config for tile: (" + q + ", " + r + ")");
                    } else {
                        Bukkit.getLogger().warning("[MCOHexRoyale] Failed to loadHexFlags - tile is null");
                    }
                } else {
                    Bukkit.getLogger().warning("[MCOHexRoyale] Failed to loadHexFlags - base or top is null");
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MCOHexRoyale] Failed to load HexFlag for key: " + key);
                e.printStackTrace();
            }
        }
    }

    private Map<FlagLocPos, Location> loadFlagData(FileConfiguration config, String path) {
        if (!config.isConfigurationSection(path)) return null;

        String worldName = config.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = config.getDouble(path + ".x");
        double z = config.getDouble(path + ".z");

        double baseY = config.getDouble(path + ".baseY");
        double topY = config.getDouble(path + ".topY");
        double bottomY = config.getDouble(path + ".bottomY");

        Location base = new Location(world, x, baseY, z);
        Location top = new Location(world, x, topY, z);
        Location bottom = new Location(world, x, bottomY, z);

        Map<FlagLocPos, Location> locs = new HashMap<>();
        locs.put(FlagLocPos.BASE, base);
        locs.put(FlagLocPos.TOP, top);
        locs.put(FlagLocPos.BOTTOM, bottom);
        return locs;
    }

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

    public boolean canCapture(HexTeam team, HexTile startTile) {

        HexTile base = team.getBaseTile();

        if (base == null) {
            Bukkit.getLogger().severe("HexTeam (" + team.getTeamColor().getName() + ") is missing a base tile!");
            return false;
        }

        TeamColor teamColor = team.getTeamColor();

        // Quick return: if the tile is already the team's color, then just use areConnected
        if (startTile.getCurrentTeam() != null && startTile.getCurrentTeam().getTeamColor().equals(teamColor)) {
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
                return true; // Path found!
            }

            for (HexTile neighbor : current.getNeighbors()) {
                if (visited.contains(neighbor)) continue;

                // If we're leaving the start tile, enforce teamColor
                if (neighbor.getCurrentTeam() == null || !neighbor.getCurrentTeam().getTeamColor().equals(teamColor)) continue;
                if (!neighbor.isCurrentTeamOwns()) continue;

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

    public HashMap<UUID, HexTile> getPlayerSettingFlag() {
        return settingTileFlag;
    }

    public HashMap<UUID, TeamColor> getSettingTeamSpawns() {
        return settingTeamSpawns;
    }
}