package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.managers.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class PlayerCraftListener implements Listener {

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e) {
        // prevent crafting during STARTING and INGAME states
        if (GameManager.getInstance().getGameState().equals(GameManager.GameState.STARTING)
                || GameManager.getInstance().getGameState().equals(GameManager.GameState.INGAME)) e.setCancelled(true);

    }

}
