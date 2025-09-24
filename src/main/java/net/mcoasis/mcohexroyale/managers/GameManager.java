package net.mcoasis.mcohexroyale.managers;

public class GameManager {

    private static GameManager instace;

    public static GameManager getInstance() {
        if (instace == null) {
            instace = new GameManager();
        }
        return instace;
    }

    private GameState gameState = GameState.LOBBY;

    public GameState getGameState() {
        return gameState;
    }

    void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void startGame() {
        setGameState(GameState.STARTING);
        // Additional logic to start the game
    }

    public void endGame() {
        setGameState(GameState.ENDING);
        // Additional logic to end the game
        WorldManager.getInstance().resetGameWorld();
        setGameState(GameState.LOBBY);
    }

    public enum GameState {
        LOBBY,
        STARTING,
        INGAME,
        ENDING;
    }

}
