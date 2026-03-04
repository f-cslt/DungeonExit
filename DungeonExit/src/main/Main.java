package main;

import controller.GameController;

/**
 * The Main class serves as the entry point to launch the game.
 * It creates an instance of the GameController, which is responsible for initializing
 * the game model, game view, and handling the game loop.
 *
 * The main method is executed when the application is run, starting the game.
 *
 * @see GameController The controller that handles the overall game logic and state transitions.
 */
public class Main {

	/**
	 * The main method is the entry point of the program. It initializes the game by
	 * creating a new instance of the GameController.
	 *
	 * @param args Command-line arguments (not used in this case).
	 */
	public static void main(String[] args) {
		// Initialize the game by creating an instance of GameController
		new GameController();
	}
}
