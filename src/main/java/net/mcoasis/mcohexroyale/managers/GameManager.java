package net.mcoasis.mcohexroyale.managers;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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
        setGameState(GameState.STARTING);

        gameTimerSeconds = 60 * MCOHexRoyale.getInstance().getConfig().getDouble("game-timer", 30); // default is 45 minutes
        middleTileSeconds = 60 * MCOHexRoyale.getInstance().getConfig().getDouble("middle-tile-timer", 20); // default is 30 minutes

        MCOHexRoyale.getInstance().restartGame();

        MCOHexRoyale.getInstance().restartRunnables();

        restartTimerRunnable();

        //! assign teams
        //! teleport players to their team spawns
        //! give starting items
        //! announce game start

        Bukkit.broadcastMessage("starting game...");

        setGameState(GameState.INGAME);
    }

    public void endGame() {
        Bukkit.broadcastMessage("ending game...");
        setGameState(GameState.ENDING);
        // Additional logic to end the game
        WorldManager.getInstance().resetGameWorld();
        MCOHexRoyale.getInstance().stopRunnables();
        MCOHexRoyale.getInstance().stopGame();
        setGameState(GameState.LOBBY);
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
                    endGame();
                    this.cancel();
                }
            }
        }.runTaskTimer(MCOHexRoyale.getInstance(), 0, 20L);

        MCOHexRoyale.getInstance().reloadConfig();
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
