package net.mcoasis.mcohexroyale.events;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class HexCaptureEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final HexTeam team;
    private final HexTile tile;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HexCaptureEvent(HexTeam team, HexTile tile) {
        this.team = team;
        this.tile = tile;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public HexTeam getTeam() {
        return this.team;
    }

    public HexTile getTile() {
        return this.tile;
    }

}
