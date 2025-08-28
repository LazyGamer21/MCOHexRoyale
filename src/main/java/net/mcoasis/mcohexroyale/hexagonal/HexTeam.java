package net.mcoasis.mcohexroyale.hexagonal;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class HexTeam {

    private final HashSet<Player> members = new HashSet<>();
    private final TeamColor teamColor;
    private HexTile baseLocation;

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

    /**
     *
     * @param player The player to add to this {@link HexTeam}'s set of members
     */
    public void addMember(Player player) {
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            team.getMembers().remove(player);
        }
        members.add(player);
    }

    // -- == Getters + Setters == --

    public HashSet<Player> getMembers() {
        return members;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public HexTile getBaseLocation() {
        return baseLocation;
    }

    public void setBaseLocation(HexTile baseLocation) {
        this.baseLocation = baseLocation;
    }
}
