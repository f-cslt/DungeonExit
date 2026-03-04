package model;

/**
 * The GameModel class acts as the central model in the application, adhering to the MVC (Model-View-Controller) design pattern.
 * It manages the state of the game and instantiates other models that represent different aspects of the game,
 * such as the level and player. This class does not interact directly with views or controllers, which is consistent with
 * the separation of concerns in the MVC pattern.
 */
public class GameModel {

	private final GameState gameState; // The current state of the game
	private final LevelModel levelModel; // Model representing the game's level
	private final PlayerModel playerModel; // Model representing the player
	private final MultiplayerModel multiplayerModel;
	private final LevelCreator levelCreator;

	// Additional models (e.g., mapModel, scoreModel) can be added here in the future.

	/**
	 * Constructs a new GameModel instance, initializing the game state, level model, and player model.
	 * This constructor ensures that all necessary models are instantiated and ready for the game.
	 */
	public GameModel() {
		this.gameState = new GameState(); // Initialize game state
		this.levelModel = new LevelModel(); // Initialize level model
		this.playerModel = new PlayerModel(); // Initialize player model
		this.multiplayerModel = new MultiplayerModel();
		this.levelCreator = new LevelCreator();
		// Additional models can be instantiated here as needed
	}

	/**
	 * Retrieves the current game state.
	 *
	 * @return the current game state
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * Retrieves the level model.
	 *
	 * @return the level model
	 */
	public LevelModel getLevelModel() {
		return levelModel;
	}

	/**
	 * Retrieves the player model.
	 *
	 * @return the player model
	 */
	public PlayerModel getPlayerModel(){
		return playerModel;
	}


	public MultiplayerModel getMultiplayerModel(){
		return multiplayerModel;
	}

	public LevelCreator getLevelCreator() {
		return levelCreator;
	}

}
