package net.mcoasis.mcohexroyale;

import me.ericdavis.lazySelection.LazySelection;
import me.ericdavis.lazygui.LazyGui;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.events.listeners.RespawnListener;
import net.mcoasis.mcohexroyale.events.listeners.custom.lazyselection.AreaCompleteListener;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.gui.main.ResetTilesPage;
import net.mcoasis.mcohexroyale.gui.main.TeamsPage;
import net.mcoasis.mcohexroyale.gui.main.teams.SingleTeamPage;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.gui.main.TilesPage;
import net.mcoasis.mcohexroyale.gui.main.GameControlsPage;
import net.mcoasis.mcohexroyale.events.listeners.custom.HexCaptureListener;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

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

        registerLibraries();
        registerGuiPages();
        registerCommandsAndListeners();
        startRunnable();

        //! for testing the dfs implementation, it still doesn't work
        /*HexTile tile2 = HexManager.getInstance().getHexTile(0, 0);
        if (HexManager.getInstance().canCapture(HexManager.getInstance().getTeam(HexTeam.TeamColor.BLUE), tile2)) Bukkit.broadcastMessage("yessir");
        else Bukkit.broadcastMessage("nossir");*/
    }

    @Override
    public void onDisable() {

        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            if (tile.getHexFlag() == null) continue;
            tile.getHexFlag().restoreOriginalBlocks();
        }

    }

    private void startRunnable() {
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (HexTile tile : HexManager.getInstance().getHexGrid()) {
                    if (tile.getFlagLocation() == null) continue;
                    tile.doCaptureCheck();
                    String colorToSpawn = tile.isCurrentTeamOwns() ? tile.getCurrentTeam().getTeamColor().getName() : "";
                    spawnParticles(tile.getFlagLocation(), CAPTURE_DISTANCE, colorToSpawn);
                }
                GuiManager.getInstance().refreshPages();
            }
        }, 0, FLAG_CAPTURE_TIMER);
    }

    private void registerCommandsAndListeners() {
        // register commands
        HexRoyaleCommand hexRoyaleCommand = new HexRoyaleCommand();
        getCommand("hexroyale").setExecutor(hexRoyaleCommand);

        // register events
        getServer().getPluginManager().registerEvents(new HexCaptureListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AreaCompleteListener(), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(), this);
    }

    private void registerLibraries() {
        new LazyGui(this);
        new LazySelection(this);
    }

    private void registerGuiPages() {
        new MainPage();

        new GameControlsPage();
        new TilesPage();
        new TeamsPage();
        new ResetTilesPage();

        new SingleTeamPage();
    }

    //! next make a way to save flag pole points so they can easily just be loaded using config
    //!    - when doing this make sure the loaded points are valid (same x and z)
    //! make it so you save flags using WorldEdit schematics and set the name for the schematics in the config
    //!    - from this the schematics should be turned into falling blocks so the flags can be raised/lowered


    //! for testing, make an auto team assigner and a way to manually set teams in the gui
    @EventHandler
    public void onPlayerSetTeam(PlayerInteractEvent e) {
        Material material = e.getPlayer().getInventory().getItemInMainHand().getType();
        if (material.equals(Material.RED_DYE)) {
            HexManager.getInstance().getTeam(HexTeam.TeamColor.RED).addMember(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.GRAY + "You are now on Red Team");
        }
        if (material.equals(Material.BLUE_DYE)) {
            HexManager.getInstance().getTeam(HexTeam.TeamColor.BLUE).addMember(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.GRAY + "You are now on Blue Team");
        }
        if (material.equals(Material.LIME_DYE)) {
            HexManager.getInstance().getTeam(HexTeam.TeamColor.GREEN).addMember(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.GRAY + "You are now on Green Team");
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

    public void resetPlayer(Player player) {
        // weird sequence to prevent flying, still don't really know if it works
        player.setAllowFlight(false);
        player.setFlying(false);
        Bukkit.getScheduler().runTaskLater(this, () -> player.setGameMode(GameMode.SURVIVAL), 1L);

        // reset velocity
        player.setVelocity(new Vector(0, 0, 0));

        // Clear inventory
        player.getInventory().clear();

        // Remove potion effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // Reset health and food
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(5.0f);

        // Reset experience
        player.setExp(0);
        player.setLevel(0);

        // Reset fall distance to prevent fall damage
        player.setFallDistance(0);

        // Clear fire ticks
        player.setFireTicks(0);
    }
}
