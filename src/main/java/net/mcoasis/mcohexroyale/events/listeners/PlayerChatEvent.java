package net.mcoasis.mcohexroyale.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerChatEvent implements Listener {

    public static final Map<UUID, String> settingPrice = new HashMap<>();

    @EventHandler
    public void onAdminChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("hexroyale.admin")) return;

        UUID id = p.getUniqueId();

        if (!settingPrice.containsKey(id)) return;

        String priceToChange = settingPrice.get((id));


    }

}
