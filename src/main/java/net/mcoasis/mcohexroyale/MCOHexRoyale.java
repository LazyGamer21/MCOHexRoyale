package net.mcoasis.mcohexroyale;

import net.mcoasis.mcohexroyale.commands.HexRoyaleCommand;
import net.mcoasis.mcohexroyale.commands.tabcompleters.HexRoyaleTabCompleter;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.listeners.HexCaptureListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCOHexRoyale extends JavaPlugin implements Listener {

    private static MCOHexRoyale instance;

    public static MCOHexRoyale getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        HexManager.getInstance().populateGrid();

        // register commands
        HexRoyaleCommand hexRoyaleCommand = new HexRoyaleCommand();
        getCommand("hexroyale").setExecutor(hexRoyaleCommand);
        getCommand("hexroyale").setTabCompleter(new HexRoyaleTabCompleter(hexRoyaleCommand));

        // register events
        getServer().getPluginManager().registerEvents(new HexCaptureListener(), this);
        getServer().getPluginManager().registerEvents(this, this);

        new HexTeam(HexTeam.TeamColor.RED);
    }

    @EventHandler
    public void onPlayerCrouch(PlayerToggleSneakEvent e) {
        if (!e.getPlayer().isSneaking()) return;
        getServer().getPluginManager().callEvent(new HexCaptureEvent(e.getPlayer()));
        HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).addMember(e.getPlayer());
    }
}
