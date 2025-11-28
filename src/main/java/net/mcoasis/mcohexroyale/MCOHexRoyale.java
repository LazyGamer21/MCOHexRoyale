package net.mcoasis.mcohexroyale;

import me.ericdavis.lazyScoreboard.LazyScoreboard;
import me.ericdavis.lazySelection.LazySelection;
import me.ericdavis.lazygui.LazyGui;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.commands.HexRoyaleCommand;
import net.mcoasis.mcohexroyale.commands.SetMiddleSpawnCommand;
import net.mcoasis.mcohexroyale.commands.SetTeamCommand;
import net.mcoasis.mcohexroyale.commands.participant.ShopCommand;
import net.mcoasis.mcohexroyale.commands.participant.TeamChatCommand;
import net.mcoasis.mcohexroyale.commands.tabcompleters.SetTeamTabCompleter;
import net.mcoasis.mcohexroyale.events.listeners.*;
import net.mcoasis.mcohexroyale.events.listeners.custom.HexLossListener;
import net.mcoasis.mcohexroyale.events.listeners.custom.TeamLossListener;
import net.mcoasis.mcohexroyale.events.listeners.custom.TeamWonListener;
import net.mcoasis.mcohexroyale.events.listeners.custom.lazyselection.LazySelectionListener;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.gui.ShopPage;
import net.mcoasis.mcohexroyale.gui.main.ResetTilesPage;
import net.mcoasis.mcohexroyale.gui.main.TeamsPage;
import net.mcoasis.mcohexroyale.gui.main.teams.SingleTeamPage;
import net.mcoasis.mcohexroyale.gui.shop.BuyPage;
import net.mcoasis.mcohexroyale.gui.shop.SellPage;
import net.mcoasis.mcohexroyale.hexagonal.HexFlag;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.gui.main.TilesPage;
import net.mcoasis.mcohexroyale.gui.main.GameControlsPage;
import net.mcoasis.mcohexroyale.events.listeners.custom.HexCaptureListener;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import net.mcoasis.mcohexroyale.util.ConfigUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MCOHexRoyale extends JavaPlugin implements Listener {

    private static MCOHexRoyale instance;

    public static MCOHexRoyale getInstance() {
        return instance;
    }

    private BukkitTask gameLogicUpdater;
    private BukkitTask warningMessageTask;

    private BlockBreakListener blockBreakListener;

    private LazyScoreboard scoreboard;

    private FileConfiguration spawnsConfig;
    private File spawnsFile;

    private FileConfiguration flagsConfig;
    private File flagsFile;

    private ConfigUtil shopConfigUtil;

    /* region OnEnable/OnDisable */

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        createFlagsConfig();
        createSpawnsConfig();
        shopConfigUtil = new ConfigUtil(this, "shop");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TeamPlaceholder().register();
            getLogger().info("Registered PlaceholderAPI placeholders!");
        }

        registerLibraries();
        registerGuiPages();
        registerCommandsAndListeners();
    }

    @Override
    public void onDisable() {
        GameManager.getInstance().endGame(false, true);
    }

    public void createFlagsConfig() {
        flagsFile = new File(getDataFolder(), "flags.yml");

        // If the file doesn't exist, save the default one from resources
        if (!flagsFile.exists()) {
            saveResource("flags.yml", false);
        }

        flagsConfig = YamlConfiguration.loadConfiguration(flagsFile);
    }

    public void createSpawnsConfig() {
        spawnsFile = new File(getDataFolder(), "spawns.yml");

        // If the file doesn't exist, save the default one from resources
        if (!spawnsFile.exists()) {
            saveResource("spawns.yml", false);
        }

        spawnsConfig = YamlConfiguration.loadConfiguration(spawnsFile);
    }

    public void saveFlagsConfig() {
        try {
            flagsConfig.save(flagsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSpawnsConfig() {
        try {
            spawnsConfig.save(spawnsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* endregion */

    public void startGame() {
        stopGame();

        // populate the grid before other stuff so it can be used
        HexManager.getInstance().populateGrid();
        scoreboard = new LazyScoreboard(ChatColor.GOLD + "" + ChatColor.BOLD + "-+- Hex Royale -+-");

        loadHexFlags();
    }

    /***
     * remove all flag entities, remove scoreboards, stop runnables
     */
    public void stopGame() {
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            if (tile.getHexFlag() != null) tile.getHexFlag().removeFlag();
        }
        if (scoreboard == null) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            scoreboard.removeScoreboard(p);
        }
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void restartRunnables() {
        restartLogicRunnable();
        restartWarningRunnable();
    }

    public void restartLogicRunnable() {
        if (gameLogicUpdater != null && !gameLogicUpdater.isCancelled()) gameLogicUpdater.cancel();

        reloadConfig();

        gameLogicUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                updateTiles();
                updateScoreboard();
                GuiManager.getInstance().refreshPages();
            }
        }.runTaskTimer(this, 0, getConfig().getInt("capture-update-timer", 20));

        this.reloadConfig();
    }

    public void restartWarningRunnable() {
        if (warningMessageTask != null && !warningMessageTask.isCancelled()) warningMessageTask.cancel();

        reloadConfig();

        warningMessageTask = new BukkitRunnable() {
            @Override
            public void run() {
                sendWarningMessages();
            }
        }.runTaskTimer(this, 0, getConfig().getInt("warning-message-timer", 30));

        this.reloadConfig();
    }

    void sendWarningMessages() {
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            if (!team.getBaseTile().isBaseAndBeingCaptured()) continue;
            if (!team.isTeamAlive()) continue;
            for (Player member : team.getMembersAlive().keySet()) {
                // send action bar message and sound effect
                member.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.RED + "Your base is being captured!"));
                Location loc = member.getLocation();

                new BukkitRunnable() {
                    int tick = 0;
                    @Override
                    public void run() {
                        if (tick > 4) {
                            cancel();
                            return;
                        }
                        float pitch = 1.0f - ((tick%2) * 0.2f);
                        member.playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, pitch);
                        tick++;
                    }
                }.runTaskTimer(this, 0L, 2L);
            }
        }
    }

    void updateTiles() {
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            if (tile.getFlagLocation() == null) continue;
            tile.doCaptureCheck();
            String colorToSpawn = tile.isCurrentTeamOwns() ? tile.getCurrentTeam().getTeamColor().getName() : "";
            spawnParticles(tile.getHexFlag().getBase(), getConfig().getDouble("capture-distance"), colorToSpawn);
            tile.updateFlagPosition();
        }
    }

    private void registerCommandsAndListeners() {
        blockBreakListener = new BlockBreakListener();

        // register commands
        getCommand("mcohexroyale").setExecutor(new HexRoyaleCommand());
        getCommand("setteam").setExecutor(new SetTeamCommand());
        TeamChatCommand teamChatCommand = new TeamChatCommand();
        getCommand("teamchat").setExecutor(teamChatCommand);
        getCommand("shop").setExecutor(new ShopCommand());
        getCommand("setmiddlespawn").setExecutor(new SetMiddleSpawnCommand());

        // register tab completers
        getCommand("setteam").setTabCompleter(new SetTeamTabCompleter());

        // register listeners
        getServer().getPluginManager().registerEvents(new LazySelectionListener(), this);
        getServer().getPluginManager().registerEvents(new HexCaptureListener(), this);
        getServer().getPluginManager().registerEvents(new HexLossListener(), this);
        getServer().getPluginManager().registerEvents(new TeamLossListener(), this);
        getServer().getPluginManager().registerEvents(new TeamWonListener(), this);
        getServer().getPluginManager().registerEvents(blockBreakListener, this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageEntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(teamChatCommand), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new RespawnListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChangeWorldListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerCraftListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractWithMapListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);

        blockBreakListener.loadHarvestableBlocks(getConfig());
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


        new ShopPage(this);

        new BuyPage(this);
        new SellPage(this);
    }

    private void updateScoreboard() {
        if (scoreboard == null) return;

        int lineLength = 30;

        for (Player player : Bukkit.getOnlinePlayers()) {
            // only update scoreboard for players in the game world
            if (player.getWorld() != WorldManager.getInstance().getGameWorld()) {
                scoreboard.removeScoreboard(player);
                continue;
            }

            // sets up scoreboard if it doesn't exist for the player
            scoreboard.setupScoreboard(player);

            scoreboard.addBlankLine(player);

            if (!GameManager.getInstance().isSuddenDeathStarted()) {
                scoreboard.setStat(player, "timer", ChatColor.YELLOW + "Game End: "
                        + ChatColor.WHITE + GameManager.getInstance().getFormattedTime(GameManager.getInstance().getGameTimerSeconds()));

                if (GameManager.getInstance().getMiddleTileSeconds() <= 0) {
                    if (HexCaptureListener.middleTileTeam != null) {
                        int timeleft = HexCaptureListener.getWinTimeLeft();
                        scoreboard.setStat(player, "middle", ChatColor.YELLOW + "Middle Tile: "
                                + HexCaptureListener.middleTileTeam.getTeamColor().getColor() + HexCaptureListener.middleTileTeam.getTeamColor().getName()
                                + ChatColor.WHITE + " - " + ChatColor.WHITE + GameManager.getInstance().getFormattedTime(timeleft));
                    } else {
                        scoreboard.setStat(player, "middle", ChatColor.YELLOW + "Middle Tile: "
                                + ChatColor.WHITE + "Capturable");
                    }
                } else {
                    scoreboard.setStat(player, "middle", ChatColor.YELLOW + "Middle Tile: "
                            + ChatColor.WHITE + GameManager.getInstance().getFormattedTime(GameManager.getInstance().getMiddleTileSeconds()));
                }
            } else {
                scoreboard.setStat(player, "timer", ChatColor.RED + "Sudden Death!");
            }

            scoreboard.addBlankLine(player);

            for (HexTeam team : HexManager.getInstance().getTeams()) {

                String line;
                // if the team is alive, show alive count, else show eliminated
                if (team.isTeamAlive()) {
                    int alive = team.getMembersAlive().isEmpty() ? 0 : (int) team.getMembersAlive().entrySet().stream().filter(Map.Entry::getValue).count();
                    int total = team.getMembersAlive().isEmpty() ? 0 : team.getMembersAlive().size();

                    line = team.getTeamColor().getColor() + team.getTeamColor().getName() + " Alive: " + alive + "/" + total;
                } else {
                    line = team.getTeamColor().getColor() + team.getTeamColor().getName() + ": " + ChatColor.DARK_RED + "ELIMINATED";
                }

                String line2;
                // if the team has base ownership, show owned tiles, else show base lost
                if (team.hasBaseOwnership()) {
                    line2 = team.getTeamColor().getColor() + team.getTeamColor().getName() + " Tiles: " + HexManager.getInstance().getOwnedTiles(team);
                } else {
                    line2 = team.getTeamColor().getColor() + team.getTeamColor().getName() + ": " + ChatColor.DARK_RED + "BASE LOST";
                }

                scoreboard.setStat(player, team.getTeamColor().getName(), line, lineLength, true);
                //only show second line if team is alive and sudden death has not started
                if (!GameManager.getInstance().isSuddenDeathStarted()) {
                    if (team.isTeamAlive()) scoreboard.setStat(player, team.getTeamColor().getName() + "2", line2, lineLength, true);
                }

            }

            scoreboard.addBlankLine(player);

            int coins = SellPage.coinAmounts.getOrDefault(player.getUniqueId(), 0);
            if (coins == 0) {
                scoreboard.setStat(player, "wallet", ChatColor.GREEN + "Wallet: " + ChatColor.WHITE + "You're Broke");
            } else {
                scoreboard.setStat(player, "wallet", ChatColor.GREEN + "Wallet: "
                        + ChatColor.GOLD + SellPage.coinAmounts.get(player.getUniqueId()) + " Coins");
            }


            scoreboard.addBlankLine(player);

            scoreboard.setStat(player, "endLine", ChatColor.GOLD + "" + ChatColor.BOLD + "-+--------------+-");

            scoreboard.updateStats(player);
        }
    }

    public void saveHexFlag(HexTile tile) {
        HexFlag flag = tile.getHexFlag();
        if (flag == null || flag.getBase() == null || flag.getTop() == null) {
            Bukkit.getLogger().warning("[HexRoyale] Failed to save flag positions for flag (" + tile.getQ() + ", " + tile.getR() + ")");
            return; // nothing to save
        }

        String path = "flags." + tile.getQ() + "_" + tile.getR();

        saveFlagData(flagsConfig, path, flag.getTop(), flag.getBottom(), flag.getBase());

        saveFlagsConfig();
    }


    /***
     * load all hex flags from config and set their positions in the HexTiles
     */
    public void loadHexFlags() {
        if (!flagsConfig.isConfigurationSection("flags")) return;

        ConfigurationSection flagsSection = flagsConfig.getConfigurationSection("flags");
        if (flagsSection == null) return;

        for (String key : flagsSection.getKeys(false)) {
            try {
                String[] parts = key.split("_");
                int q = Integer.parseInt(parts[0]);
                int r = Integer.parseInt(parts[1]);

                Map<FlagLocPos, Location> locs = loadFlagData(flagsConfig, "flags." + key);

                if (locs == null) {
                    Bukkit.getLogger().warning("[HexRoyale] Failed to load flag positions from config!");
                    return;
                }

                Location top = locs.get(FlagLocPos.TOP);
                Location bottom = locs.get(FlagLocPos.BOTTOM);
                Location base = locs.get(FlagLocPos.BASE);

                if (base != null && top != null) {
                    HexTile tile = HexManager.getInstance().getHexTile(q, r);
                    if (tile != null) {
                        tile.setFlagPositions(top, bottom, base);
                        boolean spawnAtTop = tile.getCurrentTeam() != null;
                        if (tile.getHexFlag() != null)
                            tile.getHexFlag().spawnFlag(spawnAtTop);
                    }
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MCOHexRoyale] Failed to load HexFlag for key: " + key);
                e.printStackTrace();
            }
        }
    }

    public void clearSpawns(HexTeam.TeamColor teamColor) {
        String key = teamColor.getName().toLowerCase() + "-spawns";

        spawnsConfig.set(key, new ArrayList<>()); // set empty list
        saveSpawnsConfig();
    }

    public void saveSpawn(HexTeam.TeamColor teamColor, Location loc) {
        String key = teamColor.getName().toLowerCase() + "-spawns";

        List<String> list = spawnsConfig.getStringList(key);
        list.add(serializeLocation(loc));

        spawnsConfig.set(key, list);
        saveSpawnsConfig();
    }

    public List<Location> loadSpawns(HexTeam.TeamColor teamColor) {
        String key = teamColor.getName().toLowerCase() + "-spawns";

        List<String> raw = spawnsConfig.getStringList(key);
        List<Location> result = new ArrayList<>();

        for (String s : raw) {
            Location loc = deserializeLocation(s);
            if (loc != null) result.add(loc);
        }

        return result;
    }

    private String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," +
                loc.getX() + "," +
                loc.getY() + "," +
                loc.getZ() + "," +
                loc.getYaw() + "," +
                loc.getPitch();
    }

    private Location deserializeLocation(String s) {
        try {
            String[] split = s.split(",");
            World world = Bukkit.getWorld(split[0]);

            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            float yaw = Float.parseFloat(split[4]);
            float pitch = Float.parseFloat(split[5]);

            return new Location(world, x, y, z, yaw, pitch);

        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to deserialize location: " + s);
            return null;
        }
    }



    private void saveFlagData(FileConfiguration config, String path, Location top, Location bottom, Location base) {
        config.set(path + ".world", base.getWorld().getName());
        config.set(path + ".x", base.getX());
        config.set(path + ".z", base.getZ());
        config.set(path + ".topY", top.getY());
        config.set(path + ".bottomY", bottom.getY());
        config.set(path + ".baseY", base.getY());
    }



    private Map<FlagLocPos, Location> loadFlagData(FileConfiguration config, String path) {
        if (!config.isConfigurationSection(path)) return null;

        String worldName = config.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = config.getDouble(path + ".x");
        double z = config.getDouble(path + ".z");

        double baseY = config.getDouble(path + ".baseY");
        double topY = config.getDouble(path + ".topY");
        double bottomY = config.getDouble(path + ".bottomY");

        Location base = new Location(world, x, baseY, z);
        Location top = new Location(world, x, topY, z);
        Location bottom = new Location(world, x, bottomY, z);

        Map<FlagLocPos, Location> locs = new HashMap<>();
        locs.put(FlagLocPos.BASE, base);
        locs.put(FlagLocPos.TOP, top);
        locs.put(FlagLocPos.BOTTOM, bottom);
        return locs;
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

    public void resetPlayer(Player player, boolean pluginDisable) {
        // weird sequence to prevent flying, still don't really know if it works
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.ADVENTURE);
        if (!pluginDisable) Bukkit.getScheduler().runTaskLater(this, () -> player.setGameMode(GameMode.SURVIVAL), 1L);
        else player.setGameMode(GameMode.SURVIVAL);

        // reset all player things that may change like potions effects, survival mode, etc
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setExp(0f);
        player.setLevel(0);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.setVelocity(new Vector(0, 0, 0));
        player.closeInventory();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (scoreboard != null) scoreboard.removeScoreboard(player);
    }

    //* Config Getters

    /***
     *
     * @return The {@link FileConfiguration} that holds shop information
     */
    public FileConfiguration getShopConfig() {
        return this.shopConfigUtil.getConfig();
    }
}
