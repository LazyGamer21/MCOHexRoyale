package net.mcoasis.mcohexroyale.hexagonal;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class HexTile {

    private final HexManager hexManager;

    /**
     * The axial coordinates for this {@link HexTile}
     */
    private final int q, r;

    private HexTeam currentTeam = null;
    private boolean currentTeamOwns = false;

    private double capturePercentage = 0.0;
    private int capturingPlayersAmount = 0;

    private Location flagLocation = null;
    public static final int MIN_FLAG_AREA_VOLUME = 4;
    public static int MAX_FLAG_AREA_VOLUME = 1000;

    private HexFlag hexFlag;

    private final Double TEAM_TELEPORT_DISTANCE = 5.0;

    public HexTile(int q, int r, HexTeam team) {
        this.hexManager = HexManager.getInstance();
        this.q = q;
        this.r = r;
        this.currentTeam = team;
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

    public void setFlagCorners(Location corner1, Location corner2) {
        if (hexFlag == null) hexFlag = new HexFlag(this);
        hexFlag.setCorner1(corner1);
        hexFlag.setCorner2(corner2);
        hexFlag.captureOriginalBlocks();
        hexFlag.spawnDisplays();
    }

    public void setFlagPole(Location corner1, Location corner2) {
        if (hexFlag == null) hexFlag = new HexFlag(this);
        hexFlag.setBase(corner1);
        hexFlag.setTop(corner2);
        flagLocation = corner1;
    }

    private final HashMap<Player, HexTeam> capturingPlayers = new HashMap<>();

    public void doCaptureCheck() {
        // return if this tile does not have a flag
        if (flagLocation == null) return;

        capturingPlayersAmount = 0;

        // get all the people in the flag area that can capture it
        setCapturers();

        HexTeam capturingTeam = null;

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
            double percentageChange = calculateChange(capturingPlayersAmount) * 20;

            // Handle percentage updates
            if (capturingTeam != null) {
                if (currentTeam == null || currentTeam.equals(capturingTeam)) {
                    // return if it's already maxed
                    if (capturePercentage >= 100.0) return;

                    // increase percentage
                    capturePercentage += percentageChange;

                    currentTeam = capturingTeam;
                    if (capturePercentage >= 100) {
                        capturePercentage = 100;
                        flagOwnershipGained();
                    }

                    //! debug
                    String color = "" + (getCurrentTeam() == null ? ChatColor.GRAY : getCurrentTeam().getTeamColor().getColor());
                    Bukkit.broadcastMessage(
                            color + ChatColor.BOLD + "(" + r + ", " + q + ") " + ChatColor.RESET + color + "Capture Percentage: " +
                                    ChatColor.YELLOW + ChatColor.BOLD + String.format("%.1f", getCapturePercentage())
                    );

                } else {
                    capturePercentage -= percentageChange;

                    if (capturePercentage <= 0) {
                        capturePercentage = 0;
                        flagOwnershipGone();
                    }

                    //! debug
                    String color = "" + (getCurrentTeam() == null ? ChatColor.GRAY : getCurrentTeam().getTeamColor().getColor());
                    Bukkit.broadcastMessage(
                            color + ChatColor.BOLD + "(" + r + ", " + q + ") " + ChatColor.RESET + color + "Capture Percentage: " +
                                    ChatColor.YELLOW + ChatColor.BOLD + String.format("%.1f", getCapturePercentage())
                    );
                }
            }
        }
    }

    private void flagOwnershipGone() {
        currentTeamOwns = false;
        currentTeam = null;
        capturePercentage = 0;
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
                if (member.getLocation().getWorld() == null || !member.getLocation().getWorld().equals(flagLocation.getWorld())) continue;
                if (member.getLocation().distance(flagLocation) > MCOHexRoyale.CAPTURE_DISTANCE) {
                    continue;
                }

                //! if (!HexManager.getInstance().canCapture(team, this)) continue;
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
            if (neighbor != null) {
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
        double offsetX = TEAM_TELEPORT_DISTANCE * Math.cos(angle);
        double offsetZ = TEAM_TELEPORT_DISTANCE * Math.sin(angle);

        // Create new location at same Y level
        Location target = getFlagLocation().clone().add(offsetX, 0, offsetZ);

        // Keep player facing the same direction
        target.setYaw(player.getLocation().getYaw());
        target.setPitch(player.getLocation().getPitch());

        // Teleport
        player.teleport(target);
    }

    // -- == Getters + Setters == --

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public Location getFlagLocation() {
        return flagLocation;
    }

    public void setFlagLocation(Location flagLocation) {
        this.flagLocation = flagLocation;
    }

    public HexTeam getCurrentTeam() {
        return currentTeam;
    }

    public double getCapturePercentage() {
        return capturePercentage;
    }

    public void setCapturePercentage(double capturePercentage) {
        this.capturePercentage = capturePercentage;
    }

    public Location getFlagCorner1() {
        return getHexFlag().getCorner1();
    }

    public Location getFlagCorner2() {
        return getHexFlag().getCorner2();
    }

    public HexFlag getHexFlag() {
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
}