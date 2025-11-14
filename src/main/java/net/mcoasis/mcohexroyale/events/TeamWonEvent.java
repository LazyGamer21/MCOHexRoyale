package net.mcoasis.mcohexroyale.events;

import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamWonEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final HexTeam team;
    private final boolean middleTile;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public TeamWonEvent(HexTeam team, boolean middleTile) {
        this.team = team;
        this.middleTile = middleTile;
    }

    public HexTeam getTeam() {
        return this.team;
    }

    public boolean isMiddleTile() {
        return this.middleTile;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
