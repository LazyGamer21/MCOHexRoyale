package net.mcoasis.mcohexroyale.events;

import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamWonEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final HexTeam team;
    private final HexTile tile;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public TeamWonEvent(HexTeam team, HexTile tile) {
        this.team = team;
        this.tile = tile;
    }

    public HexTeam getTeam() {
        return this.team;
    }

    public HexTile getTile() {
        return this.tile;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
