package net.mcoasis.mcohexroyale.hexagonal;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class HexTeam {

    private HashSet<Player> members = new HashSet<>();
    private final TeamColors teamColor;

    public HexTeam(TeamColors teamColor) {
        this.teamColor = teamColor;
        HexTeamManager.getInstance().registerTeam(this, teamColor);
    }

    public enum TeamColors {
        RED("Red", ChatColor.RED + ""),
        GREEN("Green", ChatColor.GREEN + ""),
        BLUE("Blue", ChatColor.BLUE + ""),
        YELLOW("Yellow", ChatColor.YELLOW + "");

        final String name;
        final String color;

        TeamColors(String name, String color) {
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

    public void addMember(Player p) {
        members.add(p);
    }

    //* getters and setters

    public HashSet<Player> getMembers() {
        return members;
    }

    public TeamColors getTeamColor() {
        return teamColor;
    }
}
