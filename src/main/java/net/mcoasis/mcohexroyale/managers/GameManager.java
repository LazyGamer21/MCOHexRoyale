package net.mcoasis.mcohexroyale.managers;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.listeners.RespawnListener;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {

    private static GameManager instace;

    public static GameManager getInstance() {
        if (instace == null) {
            instace = new GameManager();
        }
        return instace;
    }

    /** how long the game will last */
    private double gameTimerSeconds = 0;

    /** how long until the middle tile is capturable */
    private double middleTileSeconds = 0;

    private GameState gameState = GameState.LOBBY;
    private BukkitTask gameTimerUpdater;

    public void startGame() {
        // while players are being teleported into the game and the timer is counting down
        setGameState(GameState.STARTING);

        // set the game time and middle tile time
        gameTimerSeconds = 60 * MCOHexRoyale.getInstance().getConfig().getDouble("game-timer", 30); // default is 45 minutes
        middleTileSeconds = 60 * MCOHexRoyale.getInstance().getConfig().getDouble("middle-tile-timer", 20); // default is 30 minutes

        MCOHexRoyale.getInstance().startGame();

        MCOHexRoyale.getInstance().restartRunnables();

        restartTimerRunnable();

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
        teleportPlayers(WorldManager.getInstance().getGameWorld(), playersToTeleport);

        //! announce game start in 15 seconds
        //! action bar status [teleporting players...] and then [Game Starting in (seconds) second/seconds]

        setGameState(GameState.INGAME);

        // check every team to see if it lost (has no players)
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            team.checkTeamLoss(true);
        }
    }

    /***
     *
     * @param teleportPlayers Whether to teleport players back to the lobby world or not
     */
    public void endGame(boolean teleportPlayers) {
        setGameState(GameState.ENDING);
        // Additional logic to end the game
        WorldManager.getInstance().resetGameWorld();
        MCOHexRoyale.getInstance().stopGame();
        List<Player> playersToTeleport = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (teleportPlayers) teleportPlayers(WorldManager.getInstance().getLobbyWorld(), playersToTeleport);
        setGameState(GameState.LOBBY);
        // kill all leftover horses and floating items
    }

    public void teleportAndResetPlayer(HexTeam team, Player p) {
        team.getBaseTile().teleportToBase(p);
        MCOHexRoyale.getInstance().resetPlayer(p);
        Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> RespawnListener.setKit(p), 1L);
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
                    gameEndedByTime();
                    this.cancel();
                }
            }
        }.runTaskTimer(MCOHexRoyale.getInstance(), 0, 20L);

        MCOHexRoyale.getInstance().reloadConfig();
    }

    private void gameEndedByTime() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.GOLD + "Time's Up!", ChatColor.YELLOW + "The game has ended in a draw!", 10, 70, 20);
            p.playSound(p.getLocation(), "minecraft:entity.ender_dragon.growl", 1.0f, 1.0f);
        }
        endGame(false);
    }

    public void teleportPlayers(World world, List<Player> players) {
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
                    count++;
                }

                // Stop the task once all players are teleported
                if (!iterator.hasNext()) {
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
    void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public double getMiddleTileSeconds() {
        return middleTileSeconds;
    }

    public double getGameTimerSeconds() {
        return gameTimerSeconds;
    }

}
