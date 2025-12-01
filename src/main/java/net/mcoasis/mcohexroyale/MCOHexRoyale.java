package net.mcoasis.mcohexroyale;

import me.ericdavis.lazyItems.LazyItems;
import me.ericdavis.lazyItems.example.ExampleHammer;
import me.ericdavis.lazySelection.LazySelection;
import me.ericdavis.lazygui.LazyGui;
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
import net.mcoasis.mcohexroyale.gui.main.TeamsPage;
import net.mcoasis.mcohexroyale.gui.main.teams.SingleTeamPage;
import net.mcoasis.mcohexroyale.gui.shop.BuyPage;
import net.mcoasis.mcohexroyale.gui.shop.SellPage;
import net.mcoasis.mcohexroyale.gui.shop.buy.*;
import net.mcoasis.mcohexroyale.hexagonal.HexFlag;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.gui.main.TilesPage;
import net.mcoasis.mcohexroyale.gui.main.GameControlsPage;
import net.mcoasis.mcohexroyale.events.listeners.custom.HexCaptureListener;
import net.mcoasis.mcohexroyale.items.TrackingCompass;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.RunnablesManager;
import net.mcoasis.mcohexroyale.util.ConfigUtil;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class MCOHexRoyale extends JavaPlugin implements Listener {

    private static MCOHexRoyale instance;

    public static MCOHexRoyale getInstance() {
        return instance;
    }

    private BlockBreakListener blockBreakListener;

    private FileConfiguration spawnsConfig;
    private File spawnsFile;

    private FileConfiguration flagsConfig;
    private File flagsFile;

    private ConfigUtil shopConfigUtil;
    private ConfigUtil flagsConfigUtil;

    // used for map
    private BufferedImage mapImage;

    @Override
    public void onEnable() {
        instance = this;

        try {
            InputStream is = getResource("map.png");
            mapImage = ImageIO.read(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveDefaultConfig();
        createFlagsConfig();
        createSpawnsConfig();
        shopConfigUtil = new ConfigUtil(this, "shop");
        flagsConfigUtil = new ConfigUtil(this, "flags");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TeamPlaceholder().register();
            getLogger().info("Registered PlaceholderAPI placeholders!");
        }

        registerLibraries();
        registerGuiPages();
        registerCustomItems();
        registerCommandsAndListeners();

        HexManager.getInstance().populateGrid();
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
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new MiscellaneousListeners(), this);

        blockBreakListener.loadHarvestableBlocks(getConfig());
    }

    private void registerLibraries() {
        new LazyGui(this);
        new LazySelection(this);
        new LazyItems(this);
    }

    private void registerGuiPages() {
        new MainPage();

        new GameControlsPage();
        new TilesPage();
        new TeamsPage();

        new SingleTeamPage();


        new ShopPage(this);

        new BuyPage(this);
        new SellPage(this);

        new FoodPage(this);
        new PerksPage(this);
        new PotionsPage(this);
        new OtherPage(this);
        new WeaponsPage(this);
    }

    private void registerCustomItems() {

        new TrackingCompass();

        // new ExampleHammer(this);
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
        config.set(path + ".x", top.getX());
        config.set(path + ".z", top.getZ());
        config.set(path + ".topY", top.getY());
        config.set(path + ".bottomY", bottom.getY());
        config.set(path + ".baseY", base.getY());
    }





    public void resetPlayer(Player player, boolean pluginDisable) {
        // weird sequence to prevent flying, still don't really know if it works
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.ADVENTURE);
        if (!pluginDisable) Bukkit.getScheduler().runTaskLater(this, () -> player.setGameMode(GameMode.SURVIVAL), 1L);
        else player.setGameMode(GameMode.SURVIVAL);

        // reset all player things that may change like potions effects, survival mode, etc
        player.setGlowing(false);
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

        if (RunnablesManager.getInstance().getScoreboard() != null) RunnablesManager.getInstance().getScoreboard().removeScoreboard(player);
    }

    //* Config Getters

    /***
     *
     * @return The {@link FileConfiguration} that holds shop information
     */
    public ConfigUtil getShopConfigUtil() {
        return this.shopConfigUtil;
    }

    /***
     *
     * @return The {@link FileConfiguration} that holds shop information
     */
    public ConfigUtil getFlagsConfigUtil() {
        return this.flagsConfigUtil;
    }

    public FileConfiguration getFlagsConfig() {
        return flagsConfig;
    }

    public FileConfiguration getSpawnsConfig() {
        return spawnsConfig;
    }

    public BufferedImage getMapImage() {
        return mapImage;
    }
}
