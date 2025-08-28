package net.mcoasis.mcohexroyale.hexagonal;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        if (this.hexFlag == null) this.hexFlag = new HexFlag(this);
        this.hexFlag.setCorner1(corner1);
        this.hexFlag.setCorner2(corner2);
        this.hexFlag.captureOriginalBlocks();
        this.hexFlag.spawnDisplays();
    }

    public void setFlagPole(Location corner1, Location corner2) {
        if (this.hexFlag == null) this.hexFlag = new HexFlag(this);
        this.hexFlag.setBaseY(corner1.getBlockY());
        this.hexFlag.setTopY(corner2.getBlockY());
        this.flagLocation = corner1;
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
            Map<HexTeam.TeamColor, Integer> teamCounts = new EnumMap<>(HexTeam.TeamColor.class);

            for (Player capturer : capturingPlayers.keySet()) {
                HexTeam team = HexManager.getInstance().getPlayerTeam(capturer);
                if (team == null) continue;

                HexTeam.TeamColor color = team.getTeamColor();
                teamCounts.put(color, teamCounts.getOrDefault(color, 0) + 1);
            }

            // Track top two counts
            HexTeam.TeamColor topTeam = null;
            int max = 0;
            int secondMax = 0; // 👈 will hold the "second team" player count

            for (Map.Entry<HexTeam.TeamColor, Integer> entry : teamCounts.entrySet()) {
                int count = entry.getValue();
                if (count > max) {
                    secondMax = max;
                    max = count;
                    topTeam = entry.getKey();
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

                if (tiedTeams > 1) {
                    capturingTeam = null;
                } else {
                    capturingTeam = HexManager.getInstance().getTeam(topTeam);
                }
            }

            // "max" is the number of players in the capturingTeam
            capturingPlayersAmount = max - secondMax;
            double percentageChange = calculateChange(capturingPlayersAmount);

            // Handle percentage updates
            if (capturingTeam != null) {
                if (currentTeam == null || currentTeam.equals(capturingTeam)) {
                    // return if it's already maxed
                    if (capturePercentage >= 100.0) return;

                    // increase percentage
                    capturePercentage += percentageChange;


                    currentTeam = capturingTeam;
                    if (capturePercentage >= 100) {
                        flagOwnershipGained();
                    }

                } else {
                    capturePercentage -= percentageChange;

                    if (capturePercentage <= 0) {
                        flagOwnershipGone();
                    }
                }
            }

            // Broadcast main percentage
            Bukkit.broadcastMessage(ChatColor.GRAY + "Current Percentage (" + ChatColor.AQUA + q + ", " + r + ChatColor.GRAY + "): "
                    + (currentTeam == null ? ChatColor.GRAY : currentTeam.getTeamColor().getColor())
                    + String.format("%.2f", capturePercentage));
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
            for (Player member : team.getMembers()) {
                if (member.getLocation().distance(flagLocation) > MCOHexRoyale.CAPTURE_DISTANCE) {
                    continue;
                }
                //!if (!canCapture(team, tile)) continue;
                capturingPlayers.put(member, team);
            }
        }
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
        return hexFlag.getCorner1();
    }

    public Location getFlagCorner2() {
        return hexFlag.getCorner2();
    }

    public HexFlag getHexFlag() {
        return hexFlag;
    }

    public boolean isCurrentTeamOwns() {
        return currentTeamOwns;
    }

    public int getCapturingPlayersAmount() {
        return capturingPlayersAmount;
    }
}