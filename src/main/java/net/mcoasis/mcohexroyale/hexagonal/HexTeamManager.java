package net.mcoasis.mcohexroyale.hexagonal;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class HexTeamManager {

    private static HexTeamManager instance;

    private HexTeamManager() {}

    public static HexTeamManager getInstance() {
        if (instance == null) {
            instance = new HexTeamManager();
        }
        return instance;
    }

    private HashMap<HexTeam.TeamColors, HexTeam> teams = new HashMap<>();

    public HashMap<HexTeam.TeamColors, HexTeam> getTeams() {
        return teams;
    }

    public void registerTeam(HexTeam team, HexTeam.TeamColors color) {
        teams.put(color, team);
    }

    public HexTeam getPlayerTeam(Player p) {
        for (HexTeam team : teams.values()) {
            if (team.getMembers().contains(p)) {
                return team;
            }
        }
        return null;
    }

}
