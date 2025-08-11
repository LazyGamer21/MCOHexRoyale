package net.mcoasis.mcohexroyale;

import me.ericdavis.lazyGui.LazyGui;
import me.ericdavis.lazyGui.guiOther.GuiManager;
import net.mcoasis.mcohexroyale.commands.HexRoyaleCommand;
import net.mcoasis.mcohexroyale.commands.tabcompleters.HexRoyaleTabCompleter;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.events.HexCaptureEvent;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.lazygui.guipages.FlagsPage;
import net.mcoasis.mcohexroyale.listeners.HexCaptureListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

        // populate the grid before other stuff so it can be used
        HexManager.getInstance().populateGrid();

        new LazyGui(this);

        new FlagsPage();

        for (Player p : Bukkit.getOnlinePlayers()) {
            GuiManager.guiPages.get(FlagsPage.pageId).open(p);
        }

        // register commands
        HexRoyaleCommand hexRoyaleCommand = new HexRoyaleCommand();
        getCommand("hexroyale").setExecutor(hexRoyaleCommand);
        getCommand("hexroyale").setTabCompleter(new HexRoyaleTabCompleter(hexRoyaleCommand));

        // register events
        getServer().getPluginManager().registerEvents(new HexCaptureListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerCrouch(PlayerToggleSneakEvent e) {
        if (!e.getPlayer().isSneaking()) return;
        getServer().getPluginManager().callEvent(new HexCaptureEvent(e.getPlayer()));
        HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).addMember(e.getPlayer());
    }
}
