package net.mcoasis.mcohexroyale.hexagonal;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.TeamLossEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class HexTeam {

    /**
     * the boolean is if the {@link Player} is alive or not (if they died while their flag was down they are not alive)
     */
    private final HashMap<Player, Boolean> membersAlive = new HashMap<>();
    private final TeamColor teamColor;
    private HexTile baseLocation;

    private final Set<Location> spawnLocations = new HashSet<>();

    private boolean teamAlive = true;

    public HexTeam(TeamColor teamColor) {
        this.teamColor = teamColor;
        registerTeam(this);
    }

    /**
     *
     * @param team The {@link HexTeam} to register
     * @implNote When a new {@link HexTeam} is created, it runs this method on its own
     */
    private void registerTeam(HexTeam team) {
        for (HexTeam existingTeam : HexManager.getInstance().getTeams()) {
            if (existingTeam.getTeamColor().equals(team.getTeamColor())) {
                MCOHexRoyale.getInstance().getLogger().warning("[MCOHexRoyale] Attempted to register a team that already exists!");
                return;
            }
        }
        HexManager.getInstance().getTeams().add(team);
    }

    public enum TeamColor {
        RED("Red", ChatColor.RED + ""),
        GREEN("Green", ChatColor.GREEN + ""),
        BLUE("Blue", ChatColor.BLUE + ""),
        YELLOW("Yellow", ChatColor.YELLOW + "");

        final String name;
        final String color;

        TeamColor(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public String getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

    }

    public void loadTeamSpawns() {
        this.spawnLocations.clear();
        this.spawnLocations.addAll(MCOHexRoyale.getInstance().loadSpawns(teamColor));
    }

    /**
     *
     * @param player The player to add to this {@link HexTeam}'s set of members
     */
    public void addMember(Player player) {
        if (getMembersAlive().containsKey(player)) return;
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            team.getMembersAlive().remove(player);
        }
        membersAlive.put(player, true);
    }

    public void checkTeamLoss(boolean bypassBaseOwnership) {
        if (hasBaseOwnership() && !bypassBaseOwnership) return;

        boolean teamAlive = false;

        for (boolean alive : getMembersAlive().values()) {
            teamAlive = alive;
            if (teamAlive) break;
        }

        if (teamAlive) return;

        Bukkit.getPluginManager().callEvent(new TeamLossEvent(this, getBaseTile()));
    }

    // -- == Getters + Setters == --

    public HashMap<Player, Boolean> getMembersAlive() {
        return membersAlive;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public HexTile getBaseTile() {
        return baseLocation;
    }

    public void setBaseLocation(HexTile baseLocation) {
        this.baseLocation = baseLocation;
    }

    public boolean hasBaseOwnership() {
        if (getBaseTile() == null) return false;
        return getBaseTile().isCurrentTeamOwns() && getBaseTile().getCurrentTeam().equals(this);
    }

    public boolean isTeamAlive() { return this.teamAlive; }

    public void setTeamAlive(boolean teamAlive) { this.teamAlive = teamAlive; }

    public Set<Location> getSpawnLocations() {
        return spawnLocations;
    }
}
