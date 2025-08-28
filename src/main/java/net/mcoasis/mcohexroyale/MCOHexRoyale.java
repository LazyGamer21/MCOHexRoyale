package net.mcoasis.mcohexroyale;

import me.ericdavis.lazygui.LazyGui;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.commands.HexRoyaleCommand;
import net.mcoasis.mcohexroyale.commands.HexRoyaleTabCompleter;
import net.mcoasis.mcohexroyale.hexagonal.HexFlag;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.lazygui.guipages.TilesPage;
import net.mcoasis.mcohexroyale.lazygui.guipages.GameControlsPage;
import net.mcoasis.mcohexroyale.listeners.HexCaptureListener;
import net.mcoasis.mcohexroyale.listeners.PlayerInteractListener;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCOHexRoyale extends JavaPlugin implements Listener {

    private static MCOHexRoyale instance;

    public static MCOHexRoyale getInstance() {
        return instance;
    }

    // 20 is a good value for this, flag capture percentage updates every 20 ticks (1 second)
    public static int FLAG_CAPTURE_TIMER = 20;
    public static double CAPTURE_DISTANCE = 10.0;

    @Override
    public void onEnable() {
        instance = this;

        // populate the grid before other stuff so it can be used
        HexManager.getInstance().populateGrid();

        new LazyGui(this);

        new GameControlsPage();
        new TilesPage();

        new HexTeam(HexTeam.TeamColor.RED);
        new HexTeam(HexTeam.TeamColor.BLUE);
        new HexTeam(HexTeam.TeamColor.GREEN);
        new HexTeam(HexTeam.TeamColor.YELLOW);

        // register commands
        HexRoyaleCommand hexRoyaleCommand = new HexRoyaleCommand();
        getCommand("hexroyale").setExecutor(hexRoyaleCommand);
        getCommand("hexroyale").setTabCompleter(new HexRoyaleTabCompleter(hexRoyaleCommand));

        // register events
        getServer().getPluginManager().registerEvents(new HexCaptureListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (HexTile tile : HexManager.getInstance().getHexGrid()) {
                    if (tile.getFlagLocation() == null) continue;
                    tile.doCaptureCheck();
                    String colorToSpawn = tile.isCurrentTeamOwns() ? tile.getCurrentTeam().getTeamColor().getName() : "";
                    spawnParticles(tile.getFlagLocation(), CAPTURE_DISTANCE, colorToSpawn);
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    GuiManager.getInstance().refreshPage(TilesPage.pageId, p);
                }
            }
        }, 0, FLAG_CAPTURE_TIMER);
    }

    //! next make a way to save flags so they can easily just be loaded, either through the config, SQLite, or worldedit schematics

    //! the gui only updates for the most recent person to open it

    @Override
    public void onDisable() {
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            if (tile.getHexFlag() == null) continue;
            tile.getHexFlag().restoreOriginalBlocks();
        }
    }

    @EventHandler
    public void onPlayerBlueTeam(PlayerInteractEvent e) {
        Material material = e.getPlayer().getInventory().getItemInMainHand().getType();
        if (material.equals(Material.BLUE_DYE)) {
            HexManager.getInstance().getTeam(HexTeam.TeamColor.BLUE).addMember(e.getPlayer());
            e.getPlayer().sendMessage("You are now on Blue Team");
        }
    }

    @EventHandler
    public void onPlayerRedTeam(PlayerInteractEvent e) {
        Material material = e.getPlayer().getInventory().getItemInMainHand().getType();
        if (material.equals(Material.RED_DYE)) {
            HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).addMember(e.getPlayer());
            e.getPlayer().sendMessage("You are now on Red Team");
        }
    }

    public void spawnParticles(Location loc, double distance, String color) {
        World world = loc.getWorld();
        if (world == null) return;

        int points = 36; // number of particles around the circle (higher = smoother)
        double angleStep = (2 * Math.PI) / points;

        for (int i = 0; i < points; i++) {
            double angle = i * angleStep;
            double x = loc.getX() + distance * Math.cos(angle);
            double z = loc.getZ() + distance * Math.sin(angle);

            Particle.DustOptions dustColor = new Particle.DustOptions(Color.BLACK, 1.0f);
            switch (color) {
                case "Red":
                    dustColor = new Particle.DustOptions(Color.RED, 1.0f);
                    break;
                case "Blue":
                    dustColor = new Particle.DustOptions(Color.BLUE, 1.0f);
                    break;
                case "Green":
                    dustColor = new Particle.DustOptions(Color.LIME, 1.0f);
                    break;
                case "Yellow":
                    dustColor = new Particle.DustOptions(Color.YELLOW, 1.0f);
                    break;
                default:
                    break;
            }

            Location particleLoc = new Location(world, x, loc.getY(), z);
            world.spawnParticle(Particle.DUST, particleLoc, 3, 0.5, 0.5, 0.5, 0, dustColor);
        }
    }
}
