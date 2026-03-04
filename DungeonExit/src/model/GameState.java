package model;

/**
 * The GameState class represents the current state of the game.
 * The game can be in various states, which are represented by an enum called `States`.
 * These states can be expanded to include more states, such as 'FIGHTING' or 'FLIPPING'
 * for puzzle-related actions, depending on the game requirements.
 */
public class GameState {

	private States state; // The current state of the game

	/**
	 * Constructs a new GameState instance, initializing the state to MENU.
	 * The default state when the game starts is set to MENU.
	 */
	public GameState() {
		this.state = States.MENU; // Initial game state is set to MENU
	}

	/**
	 * Retrieves the current game state.
	 *
	 * @return the current game state
	 */
	public States getState() {
		return this.state;
	}

	/**
	 * Sets the current game state to the specified state.
	 *
	 * @param state the new game state to set
	 */
	public void setState(States state) {
		this.state = state;
	}

	/**
	 * Enum representing the various states in the game.
	 * The game can exist in one of these states:
	 * <ul>
	 *     <li>MENU - The game is in the main menu.</li>
	 *     <li>PLAYING - The game is currently being played.</li>
	 *     <li>LEVEL_SELECTOR - The player is selecting a level to play.</li>
	 * </ul>
	 * Future states such as 'FIGHTING' or 'FLIPPING' can be added to this enum as the game evolves.
	 */
	public enum States {
		MENU,             // Main menu state
		LEVEL,          // The game is in play
		LEVEL_SELECTOR,   // The player is choosing a level to play
		VICTORY,
		MULTIPLAYER,
		LEVEL_CREATOR
	}
}
