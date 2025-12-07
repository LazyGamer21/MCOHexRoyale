package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.managers.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class PlayerCraftListener implements Listener {

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e) {
        // only during STARTING and INGAME states
        if (!GameManager.getInstance().getGameState().equals(GameManager.GameState.STARTING)
                && !GameManager.getInstance().getGameState().equals(GameManager.GameState.INGAME)) return;


        // HOT FIX 1 - disallow crafting boats
        if (e.getRecipe().getResult().getType().name().endsWith("_BOAT")) e.setCancelled(true);

        // allow the craft if the items used to craft it are only planks or logs
        boolean onlyPlanksOrLogs = true;
        for (var item : e.getInventory().getMatrix()) {
            if (item == null) continue;
            String itemType = item.getType().toString();
            if (!itemType.endsWith("PLANKS") && !itemType.endsWith("LOG") && !itemType.endsWith("WOOD") && !itemType.contains("STICK")) {
                onlyPlanksOrLogs = false;
                break;
            }
        }
        if (onlyPlanksOrLogs) return;

        // allow the craft if it is in a stonecutter
        if (e.getInventory().getHolder() != null) {
            if (e.getInventory().getHolder().toString().equals("STONECUTTER")) {
                return;
            }
        }

        e.setCancelled(true);

    }

}
