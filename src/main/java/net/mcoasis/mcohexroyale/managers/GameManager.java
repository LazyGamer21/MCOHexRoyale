package net.mcoasis.mcohexroyale.managers;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.HexLossEvent;
import net.mcoasis.mcohexroyale.events.listeners.EntityDamageEntityListener;
import net.mcoasis.mcohexroyale.events.listeners.RespawnListener;
import net.mcoasis.mcohexroyale.gui.shop.SellPage;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static net.mcoasis.mcohexroyale.events.listeners.RespawnListener.setKit;

public class GameManager {

    private static GameManager instance;

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /** how long the game will last */
    private double gameTimerSeconds = 0;

    /** how long until the middle tile is capturable */
    private double middleTileSeconds = 0;

    private GameState gameState = GameState.LOBBY;
    private BukkitTask gameTimerUpdater;

    public boolean isSuddenDeathStarted() {
        return suddenDeathStarted;
    }

    public void setSuddenDeathStarted(boolean suddenDeathStarted) {
        this.suddenDeathStarted = suddenDeathStarted;
    }

    /***
     * used for scoreboard
     */
    private boolean suddenDeathStarted = false;

    public void startGame() {
        WorldManager.getInstance().resetGameWorld();

        HexManager.getInstance().populateGrid();

        // load all team spawns
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            team.loadTeamSpawns();
        }

        // while players are being teleported into the game and the timer is counting down
        setGameState(GameState.STARTING);

        SellPage.coinAmounts.clear();

        // set the game time and middle tile time
        gameTimerSeconds = 60 * MCOHexRoyale.getInstance().getConfig().getDouble("game-timer", 30); // default is 45 minutes
        middleTileSeconds = 60 * MCOHexRoyale.getInstance().getConfig().getDouble("middle-tile-timer", 20); // default is 30 minutes

        // players will be teleported to their base in PlayerChangeWorldListener
        List<Player> playersToTeleport = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            HexTeam team = HexManager.getInstance().getPlayerTeam(p);
            if (team != null && p.getWorld().equals(WorldManager.getInstance().getGameWorld())) {
                teleportAndResetPlayer(team, p);
                continue;
            }
            playersToTeleport.add(p);
        }
        teleportPlayers(WorldManager.getInstance().getGameWorld(), playersToTeleport, true, false);
    }

    public void playersDoneTeleportingAtStart() {

        final int[] time = {10}; // countdown time in seconds

        Bukkit.getScheduler().runTaskTimer(MCOHexRoyale.getInstance(), task -> {

            if (time[0] <= 0) {
                // Countdown finished

                MCOHexRoyale.getInstance().startGame();

                MCOHexRoyale.getInstance().restartRunnables();

                restartTimerRunnable();

                // check every team to see if it lost (has no players)
                for (HexTeam team : HexManager.getInstance().getTeams()) {
                    team.checkTeamLoss(true);
                }

                setGameState(GameState.INGAME);

                task.cancel();
                return;
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (time[0] <= 5 && time[0] > 1) {
                    // Last 5,4,3,2 seconds → ping sound
                    p.playSound(
                            p.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING,
                            1f,
                            1f + ((5 - 10) * 0.2f)
                    );
                    p.sendMessage(ChatColor.YELLOW + "Game starting in " + (time[0]-1) + "...");
                }

                if (time[0] == 1) {
                    // Final second → lightning impact
                    p.playSound(
                            p.getLocation(),
                            Sound.ENTITY_LIGHTNING_BOLT_IMPACT,
                            1f,
                            1f
                    );
                    p.sendTitle(ChatColor.GOLD + "ZOO WE MAMA!", ChatColor.GRAY + "Good Luck!", 10, 70, 20);
                }
            }

            time[0]--;

        }, 0L, 20L);
    }

    /***
     *
     * @param teleportPlayers Whether to teleport players back to the lobby world or not
     */
    public void endGame(boolean teleportPlayers, boolean pluginDisable) {
        setGameState(GameState.ENDING);
        // Additional logic to end the game
        WorldManager.getInstance().resetGameWorld();
        MCOHexRoyale.getInstance().stopGame();
        HexManager.getInstance().getHexGrid().clear();
        HexManager.getInstance().getTeams().clear();
        SellPage.coinAmounts.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            MCOHexRoyale.getInstance().resetPlayer(p, pluginDisable);
        }
        List<Player> playersToTeleport = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (teleportPlayers && !pluginDisable) teleportPlayers(WorldManager.getInstance().getLobbyWorld(), playersToTeleport, false, false);
        setGameState(GameState.LOBBY);
    }

    public void teleportAndResetPlayer(HexTeam team, Player p) {
        team.getBaseTile().teleportToBase(p, true);
        MCOHexRoyale.getInstance().resetPlayer(p, false);
        Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> setKit(p), 1L);
    }

    public void restartTimerRunnable() {
        if (gameTimerUpdater != null && !gameTimerUpdater.isCancelled()) gameTimerUpdater.cancel();

        MCOHexRoyale.getInstance().reloadConfig();

        gameTimerUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                gameTimerSeconds -= 1;
                if (middleTileSeconds > 0) middleTileSeconds -= 1;
                if (gameTimerSeconds <= 0) {
                    startSuddenDeath();
                    this.cancel();
                }
            }
        }.runTaskTimer(MCOHexRoyale.getInstance(), 0, 20L);

        MCOHexRoyale.getInstance().reloadConfig();
    }

    private void startSuddenDeath() {

        for (UUID id : RespawnListener.playerRespawning) {
            Player p = Bukkit.getPlayer(id);
            HexTeam team = HexManager.getInstance().getPlayerTeam(p);
            if (!team.isTeamAlive()) return;
            p.setGameMode(GameMode.SURVIVAL);
            team.getBaseTile().teleportToBase(p, true);
            setKit(p);
            team.getMembersAlive().put(p, true);
            RespawnListener.playerRespawning.remove(p.getUniqueId());
        }

        Location middleSpawn = loadLocation(MCOHexRoyale.getInstance().getConfig(), "middle-tile-spawn");

        // if the middleSpawn is not set then end the game as a draw
        if (middleSpawn == null) {
            Bukkit.getLogger().warning("[HexRoyale] Middle tile spawn location is not set! Cannot start sudden death.");
            gameEndedByTime();
            return;
        }

        EntityDamageEntityListener.pvpEnabled = false;

        // take away spawns
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            HexTile baseTile = team.getBaseTile();
            if (baseTile == null) continue;
            team.getBaseTile().setCurrentTeam(null);
            baseTile.setCurrentTeamOwns(false);
            Bukkit.getPluginManager().callEvent(new HexLossEvent(team, team.getBaseTile()));
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.RED + "Sudden Death!", ChatColor.GRAY + "Respawns Disabled!", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
            suddenDeathStarted = true;
        }

        // idk if we wanna do this
        /*List<Player> playersToTeleport = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            playersToTeleport.add(p);
            p.setHealth(20.0);
            p.setSaturation(20.0f);
            p.setFoodLevel(20);
        }
        teleportPlayers(WorldManager.getInstance().getGameWorld(), playersToTeleport, false, true);*/
    }

    public void startSuddenDeathTimer() {
        final int[] time = {10}; // countdown time in seconds

        Bukkit.getScheduler().runTaskTimer(MCOHexRoyale.getInstance(), task -> {

            if (time[0] <= 0) {
                // Countdown finished
                suddenDeathTimerEnded();
                task.cancel();
                return;
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (time[0] <= 5 && time[0] > 1) {
                    // Last 5,4,3,2 seconds → ping sound
                    p.playSound(
                            p.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING,
                            1f,
                            1f + ((5 - 10) * 0.2f)
                    );
                    p.sendMessage(ChatColor.RED + "Sudden Death starting in " + (time[0]-1) + "...");
                    p.sendTitle(ChatColor.RED + "" + (time[0]-1), "", 0, 20, 0);
                }

                if (time[0] == 1) {
                    // Final second → lightning impact
                    p.playSound(
                            p.getLocation(),
                            Sound.ENTITY_LIGHTNING_BOLT_IMPACT,
                            1f,
                            1f
                    );
                }
            }

            time[0]--;

        }, 0L, 20L);
    }

    private void suddenDeathTimerEnded() {
        EntityDamageEntityListener.pvpEnabled = true;
    }

    private void gameEndedByTime() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.GOLD + "Time's Up!", ChatColor.YELLOW + "The game has ended in a draw!", 10, 70, 20);
            p.playSound(p.getLocation(), "minecraft:entity.ender_dragon.growl", 1.0f, 1.0f);
        }
        Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> {
            GameManager.getInstance().endGame(true, false);
        }, 20L * 10); // teleport after 10 seconds
    }

    public void teleportPlayers(World world, List<Player> players, boolean startOfGame, boolean suddenDeath) {
        MCOHexRoyale plugin = MCOHexRoyale.getInstance();
        FileConfiguration config = plugin.getConfig();
        int maxTeleportCount = config.getInt("max-teleport-count", 20);
        int teleportInterval = config.getInt("teleport-interval", 5);

        // Defensive check
        if (world == null || players == null || players.isEmpty()) return;

        // Copy list so we can modify it safely
        List<Player> remaining = new ArrayList<>(players);
        Iterator<Player> iterator = remaining.iterator();

        new BukkitRunnable() {
            @Override
            public void run() {
                int count = 0;

                while (iterator.hasNext() && count < maxTeleportCount) {
                    Player player = iterator.next();
                    iterator.remove();

                    // Teleport the player (you can customize the location)
                    player.teleport(world.getSpawnLocation());
                    // send all players a action bar message saying "starting soon"
                    if (startOfGame) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Teleported to game world! Game starting shortly..."));
                    } else if (suddenDeath) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Teleported to sudden death! Respawns are disabled."));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Teleported to lobby!"));
                    }
                    count++;
                }

                // Stop the task once all players are teleported
                if (!iterator.hasNext()) {
                    if (startOfGame) {
                        GameManager.getInstance().playersDoneTeleportingAtStart();
                    } else if (suddenDeath) {
                        GameManager.getInstance().startSuddenDeathTimer();
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, teleportInterval * 20L); // convert seconds to ticks
    }


    public String getFormattedTime(double time) {
        int totalSeconds = (int) time;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public enum GameState {
        LOBBY,
        STARTING,
        INGAME,
        ENDING;
    }

    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public double getMiddleTileSeconds() {
        return middleTileSeconds;
    }

    public double getGameTimerSeconds() {
        return gameTimerSeconds;
    }

    public static Location loadLocation(FileConfiguration config, String path) {
        String worldName = config.getString(path + ".world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");

        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

}
