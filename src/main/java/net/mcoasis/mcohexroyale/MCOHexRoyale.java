package net.mcoasis.mcohexroyale;

import net.mcoasis.mcohexroyale.commands.HexRoyaleCommand;
import net.mcoasis.mcohexroyale.commands.tabcompleters.HexRoyaleTabCompleter;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTeamManager;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexTileManager;
import net.mcoasis.mcohexroyale.listeners.HexCaptureListener;
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

        HexTileManager.getInstance().populateGrid();

        // register commands
        HexRoyaleCommand commandExecutor = new HexRoyaleCommand();
        getCommand("hexroyale").setExecutor(commandExecutor);
        getCommand("hexroyale").setTabCompleter(new HexRoyaleTabCompleter(commandExecutor));

        // register events
        getServer().getPluginManager().registerEvents(new HexCaptureListener(), this);
        getServer().getPluginManager().registerEvents(this, this);

        new HexTeam(HexTeam.TeamColors.RED);
    }

    @EventHandler
    public void onPlayerCrouch(PlayerToggleSneakEvent e) {
        if (!e.getPlayer().isSneaking()) return;
        getServer().getPluginManager().callEvent(new HexCaptureEvent(e.getPlayer()));
        HexTeamManager.getInstance().getTeams().get(HexTeam.TeamColors.RED).addMember(e.getPlayer());
    }
}
