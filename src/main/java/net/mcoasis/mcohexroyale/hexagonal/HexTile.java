package net.mcoasis.mcohexroyale.hexagonal;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import net.mcoasis.mcohexroyale.events.HexLossEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColor;
import net.mcoasis.mcohexroyale.managers.GameManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class HexTile {

    private final HexManager hexManager;

    /**
     * The axial coordinates for this {@link HexTile}
     */
    private final int q, r;

    private HexTeam currentTeam;
    private boolean currentTeamOwns = false;

    private double capturePercentage = 0.0;
    private int capturingPlayersAmount = 0;

    private HexFlag hexFlag;

    private HexTeam baseTeam;
    private HexTeam capturingTeam = null;

    public HexTile(int q, int r, HexTeam team) {
        this.hexManager = HexManager.getInstance();
        this.q = q;
        this.r = r;
        this.currentTeam = team;
        this.baseTeam = team;
        if (team != null) {
            this.capturePercentage = 100.0;
            this.currentTeamOwns = true;
        }
        setHex(this);
    }

    private void setHex(HexTile tile) {
        HexTile oldTile = hexManager.getHexTile(tile.getQ(), tile.getR());
        if (oldTile != null) hexManager.getHexGrid().remove(oldTile);
        hexManager.getHexGrid().add(tile);
    }

    public void setFlagBase(Location loc) {
        if (hexFlag == null) hexFlag = new HexFlag(this);
        hexFlag.setBase(loc);
    }

    public void setFlagBottom(Location loc) {
        if (hexFlag == null) hexFlag = new HexFlag(this);
        hexFlag.setBottom(loc);
    }

    public void setFlagTop(Location loc) {
        if (hexFlag == null) hexFlag = new HexFlag(this);
        hexFlag.setTop(loc);
    }

    public void setFlagPositions(Location top, Location bottom, Location base) {
        if (hexFlag == null) hexFlag = new HexFlag(this);
        hexFlag.setTop(top);
        hexFlag.setBottom(bottom);
        hexFlag.setBase(base);
    }

    private final HashMap<Player, HexTeam> capturingPlayers = new HashMap<>();

    public void doCaptureCheck() {
        // return if this tile does not have a flag
        if (hexFlag.getBase() == null) return;

        // only allow capturing of the middle tile after the middle tile time is up
        if (q == 0 && r == 0 && GameManager.getInstance().getMiddleTileSeconds() > 0) return;

        capturingPlayersAmount = 0;

        // get all the people in the flag area that can capture it
        setCapturers();

        if (!capturingPlayers.isEmpty()) {

            // Count players per team
            Map<TeamColor, Integer> teamCounts = new EnumMap<>(TeamColor.class);

            for (Player capturer : capturingPlayers.keySet()) {
                HexTeam team = HexManager.getInstance().getPlayerTeam(capturer);
                if (team == null) continue;

                TeamColor color = team.getTeamColor();
                teamCounts.put(color, teamCounts.getOrDefault(color, 0) + 1);
            }

            // Track top two counts
            TeamColor topTeam = null;
            int max = 0;
            int secondMax = 0; // 👈 will hold the "second team" player count

            // get the top 2 teams and subtract second top from top
            for (Map.Entry<TeamColor, Integer> entry : teamCounts.entrySet()) {
                int count = entry.getValue();
                TeamColor teamColor = entry.getKey();
                if (count > max) {
                    secondMax = max;
                    max = count;
                    topTeam = teamColor;
                } else if (count > secondMax) {
                    secondMax = count;
                }
            }

            capturingTeam = null;

            if (topTeam != null) {
                // Check if multiple teams are tied with max
                int tiedTeams = 0;
                for (int count : teamCounts.values()) {
                    if (count == max) tiedTeams++;
                }

                if (!(tiedTeams > 1)) {
                    capturingTeam = HexManager.getInstance().getTeam(topTeam);
                }
            }

            // "max" is the number of players in the capturingTeam
            capturingPlayersAmount = max - secondMax;

            // multiplier option
            double captureMultiplier = MCOHexRoyale.getInstance().getConfig().getDouble("capture-multiplier", 1.0);
            double captureUpdateTimer = MCOHexRoyale.getInstance().getConfig().getInt("capture-update-timer");
            double percentageChange = calculateChange(capturingPlayersAmount) * captureMultiplier;
            percentageChange *= (captureUpdateTimer/20); // normalize capture change to account for different timer (20 is the default timer)

            // Handle percentage updates
            if (capturingTeam == null) return;

            // if no current team or the current team is the capturing team
            if (currentTeam == null || currentTeam.equals(capturingTeam)) {
                boolean currentTeamWasNull = currentTeam == null;

                // return if it's already maxed
                if (capturePercentage >= 100.0) return;

                // increase percentage
                capturePercentage += percentageChange;

                currentTeam = capturingTeam;

                if (currentTeamWasNull) hexFlag.spawnFlag(false);

                if (capturePercentage >= 100) {
                    if (!currentTeamOwns) flagOwnershipGained();
                }

                // if the current team is not the capturing team
            } else {

                capturePercentage -= percentageChange;

                if (capturePercentage <= 0) {
                    flagOwnershipGone();
                    capturePercentage = 0;
                    currentTeam = null;
                }

            }
        }
    }

    public boolean isBaseAndBeingCaptured() {
        if (baseTeam == null) return false;
        if (capturingPlayers.isEmpty()) return false;

        // check if the capturing team is not the base team
        return !capturingTeam.equals(baseTeam);
    }

    public void updateFlagPosition() {
        if (hexFlag == null) return;

        hexFlag.moveFlag(capturePercentage);
    }

    private void flagOwnershipGone() {
        currentTeamOwns = false;
        HexTeam team = currentTeam;
        currentTeam = null;
        capturePercentage = 0;
        Bukkit.getPluginManager().callEvent(new HexLossEvent(team, this));
    }

    private void flagOwnershipGained() {
        currentTeamOwns = true;
        capturePercentage = 100;
        Bukkit.getPluginManager().callEvent(new HexCaptureEvent(currentTeam,this));
    }

    private void setCapturers() {
        capturingPlayers.clear();
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            for (Player member : team.getMembersAlive().keySet()) {
                if (team.getMembersAlive().get(member) == false) continue;
                if (member.isDead()) continue;
                if (member.getLocation().getWorld() == null || !member.getLocation().getWorld().equals(hexFlag.getBase().getWorld())) continue;
                if (member.getLocation().distance(hexFlag.getBase()) > MCOHexRoyale.getInstance().getConfig().getDouble("capture-distance")) {
                    continue;
                }

                if (!HexManager.getInstance().canCapture(team, this)) continue;
                capturingPlayers.put(member, team);
            }
        }
    }

    public List<HexTile> getNeighbors() {
        int[][] directions = {
                { 1, 0 }, { 1, -1 }, { 0, -1 },
                { -1, 0 }, { -1, 1 }, { 0, 1 }
        };

        List<HexTile> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int neighborQ = q + dir[0];
            int neighborR = r + dir[1];
            HexTile neighbor = hexManager.getHexTile(neighborQ, neighborR);
            if (neighbor != null && !neighbor.equals(this)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    private double calculateChange(int numberOfPlayers) {
        if (numberOfPlayers <= 0) return 1.0; // base case

        // Exponential scaling
        double value = Math.pow(100.0, (double) numberOfPlayers / 15.0);

        // Clamp between 1 and 100
        return Math.min(100.0, Math.max(1.0, value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexTile that = (HexTile) o;
        return q == that.q && r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    private final Random random = new Random();

    /**
     * Teleports a player to a random spot around their flag
     *
     * @param player   the player to teleport
     */
    public void teleportToBase(Player player) {
        // Random angle in radians (0 to 2π)
        double angle = random.nextDouble() * 2 * Math.PI;

        // Calculate X/Z offset
        double TEAM_TELEPORT_DISTANCE = 5.0;
        double offsetX = TEAM_TELEPORT_DISTANCE * Math.cos(angle);
        double offsetZ = TEAM_TELEPORT_DISTANCE * Math.sin(angle);

        // Create new location at same Y level
        Location target = hexFlag.getBase().clone().add(offsetX, 0, offsetZ);

        // Keep player facing the same direction
        target.setYaw(player.getLocation().getYaw());
        target.setPitch(player.getLocation().getPitch());

        // Teleport
        if (target.getWorld() != null) player.teleport(getHighestSpawnLocation(target.getWorld(), target.getBlockX(), target.getBlockZ()));
        else Bukkit.getLogger().severe("[MCOHexRoyale] Failed to respawn player (" + player.getName() + ") -- World not found in teleport location");
    }

    private Location getHighestSpawnLocation(World world, int x, int z) {
        // Start from world max height downwards
        int maxY = world.getMaxHeight();

        for (int y = maxY; y > world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);

            if (block.getType().isAir()) {
                Block below = world.getBlockAt(x, y - 1, z);

                if (isSpawnable(below.getType())) {
                    // Center player in block
                    return new Location(world, x + 0.5, y, z + 0.5);
                }
            }
        }
        return null; // No valid spawn found
    }

    private boolean isSpawnable(Material mat) {
        // Must be solid and not liquid/fire
        if (!mat.isSolid()) return false;

        return switch (mat) {
            case LAVA, WATER, MAGMA_BLOCK, FIRE, SOUL_FIRE, POWDER_SNOW -> false;
            default -> true;
        };
    }

    // -- == Getters + Setters == --

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public Location getFlagLocation() {
        if (hexFlag == null) return null;
        return hexFlag.getBase();
    }

    public HexTeam getCurrentTeam() {
        return currentTeam;
    }

    public double getCapturePercentage() {
        return capturePercentage;
    }

    public @Nullable HexFlag getHexFlag() {
        return hexFlag;
    }

    public HexFlag getOrCreateHexFlag() {
        if (hexFlag == null) hexFlag = new HexFlag(this);
        return hexFlag;
    }

    public boolean isCurrentTeamOwns() {
        return currentTeamOwns;
    }

    public int getCapturingPlayersAmount() {
        return capturingPlayersAmount;
    }

    public void setCapturePercentage(double capturePercentage) {
        this.capturePercentage = capturePercentage;
    }

    public void setCurrentTeamOwns(boolean currentTeamOwns) {
        this.currentTeamOwns = currentTeamOwns;
    }

    public void setCurrentTeam(HexTeam currentTeam) {
        this.currentTeam = currentTeam;
    }
}