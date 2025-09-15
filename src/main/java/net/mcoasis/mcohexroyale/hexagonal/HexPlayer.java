package net.mcoasis.mcohexroyale.hexagonal;

import org.bukkit.entity.Player;

public class HexPlayer {

    private final Player player;
    private boolean alive = true;
    private HexTeam team;

    public HexPlayer(Player player, HexTeam team) {
        this.player = player;
        this.team = team;
    }

    public HexTeam getTeam() {
        return team;
    }

    public void setTeam(HexTeam team) {
        this.team = team;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Player getPlayer() {
        return player;
    }

}
