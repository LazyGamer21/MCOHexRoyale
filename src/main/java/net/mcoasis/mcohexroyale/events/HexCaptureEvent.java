package net.mcoasis.mcohexroyale.events;

import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTeamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class HexCaptureEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HexCaptureEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Nullable public HexTeam getTeam() {
        return HexTeamManager.getInstance().getPlayerTeam(player);
    }

}
