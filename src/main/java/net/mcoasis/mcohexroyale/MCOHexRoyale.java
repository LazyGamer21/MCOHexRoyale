package net.mcoasis.mcohexroyale;

import me.ericdavis.lazyScoreboard.LazyScoreboard;
import me.ericdavis.lazySelection.LazySelection;
import me.ericdavis.lazygui.LazyGui;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.events.listeners.RespawnListener;
import net.mcoasis.mcohexroyale.events.listeners.custom.HexLossListener;
import net.mcoasis.mcohexroyale.events.listeners.custom.lazyselection.AreaCompleteListener;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.gui.main.ResetTilesPage;
import net.mcoasis.mcohexroyale.gui.main.TeamsPage;
import net.mcoasis.mcohexroyale.gui.main.teams.SingleTeamPage;
import net.mcoasis.mcohexroyale.hexagonal.HexFlag;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.gui.main.TilesPage;
import net.mcoasis.mcohexroyale.gui.main.GameControlsPage;
import net.mcoasis.mcohexroyale.events.listeners.custom.HexCaptureListener;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Map;

public final class MCOHexRoyale extends JavaPlugin implements Listener {

    private static MCOHexRoyale instance;

    public static MCOHexRoyale getInstance() {
        return instance;
    }

    private BukkitTask gameUpdater;
    private LazyScoreboard scoreboard;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        // populate the grid before other stuff so it can be used
        HexManager.getInstance().populateGrid();
        scoreboard = new LazyScoreboard(ChatColor.GOLD + "" + ChatColor.BOLD + "Hex Royale");

        loadHexFlags();

        registerLibraries();
        registerGuiPages();
        registerCommandsAndListeners();
        restartRunnable();
        updateScoreboard();
    }

    @Override
    public void onDisable() {
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            if (tile.getHexFlag() != null) tile.getHexFlag().removeFlag();
        }
    }

    public void restartRunnable() {
        if (gameUpdater != null && !gameUpdater.isCancelled()) gameUpdater.cancel();

        reloadConfig();

        gameUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                updateTiles();
                updateScoreboard();
                GuiManager.getInstance().refreshPages();
            }
        }.runTaskTimer(this, 0, getConfig().getInt("capture-update-timer", 20));

        this.reloadConfig();
    }

    void updateTiles() {
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            if (tile.getFlagLocation() == null) continue;
            tile.doCaptureCheck();
            String colorToSpawn = tile.isCurrentTeamOwns() ? tile.getCurrentTeam().getTeamColor().getName() : "";
            spawnParticles(tile.getFlagLocation(), getConfig().getDouble("capture-distance"), colorToSpawn);
            tile.updateFlagPosition();
        }
    }

    private void registerCommandsAndListeners() {
        // register commands
        HexRoyaleCommand hexRoyaleCommand = new HexRoyaleCommand();
        getCommand("mcohexroyale").setExecutor(hexRoyaleCommand);

        // register events
        getServer().getPluginManager().registerEvents(new HexCaptureListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AreaCompleteListener(), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(), this);
        getServer().getPluginManager().registerEvents(new HexLossListener(), this);
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

    private void updateScoreboard() {
        if (scoreboard == null) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboard.setupScoreboard(player);
            for (HexTeam team : HexManager.getInstance().getTeams()) {
                int alive = (int) team.getMembersAlive().entrySet().stream().filter(Map.Entry::getValue).count();
                int total = team.getMembersAlive().size();
                String line = team.getTeamColor().getColor() + team.getTeamColor().getName() + ": " + alive + "/" + total;
                scoreboard.setStat(player, team.getTeamColor().getName(), line);
            }
            scoreboard.updateStats(player);
        }
    }

    //! make game states
    //! stop players from breaking blocks in game world
    //!    -  if the block was in a resource tile area then give them the resource they mined
    //! only allow placing blocks on base tiles (any team can place on any base tile)
    //!    -  make a build height so players cannot go above the walls
    //!    -  only allow breaking player-placed blocks
    //! send a title message to a team when their base gets captured
    //! send a message to all players when any team's base tile is captured like the bed destruction message in bedwars

    public void saveHexFlag(HexTile tile) {
        HexFlag flag = tile.getHexFlag();
        if (flag == null || flag.getBase() == null || flag.getTop() == null) {
            return; // nothing to save
        }

        FileConfiguration config = MCOHexRoyale.getInstance().getConfig();
        String path = "flags." + tile.getQ() + "_" + tile.getR();

        saveLocation(config, path + ".base", flag.getBase());
        saveLocation(config, path + ".top", flag.getTop());

        saveConfig();
    }

    private void saveLocation(FileConfiguration config, String path, Location loc) {
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
    }

    public void loadHexFlags() {
        FileConfiguration config = MCOHexRoyale.getInstance().getConfig();
        if (!config.isConfigurationSection("flags")) return;

        for (String key : config.getConfigurationSection("flags").getKeys(false)) {
            try {
                // key format: q_r
                String[] parts = key.split("_");
                int q = Integer.parseInt(parts[0]);
                int r = Integer.parseInt(parts[1]);

                Location base = loadLocation(config, "flags." + key + ".base");
                Location top = loadLocation(config, "flags." + key + ".top");

                if (base != null && top != null) {
                    HexTile tile = HexManager.getInstance().getHexTile(q, r);
                    if (tile != null) {
                        tile.setFlagPole(base, top);
                        boolean spawnAtTop = tile.getCurrentTeam() != null;
                        if (tile.getHexFlag() != null) tile.getHexFlag().spawnFlag(spawnAtTop);
                    }
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MCOHexRoyale] Failed to load HexFlag for key: " + key);
                e.printStackTrace();
            }
        }
    }

    private Location loadLocation(FileConfiguration config, String path) {
        if (!config.isConfigurationSection(path)) return null;

        String worldName = config.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

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
