package controller;

import java.awt.event.KeyEvent;
import listeners.MenuStateListener;
import model.GameModel;
import model.GameState;
import utils.KeyAdapter;
import utils.MouseAdapter;
import view.GameView;

/**
 * The MenuController class is responsible for handling user input and events when the player is in the menu state.
 * This controller processes key and mouse events, allowing the player to interact with the game menu.
 * It can switch to other game states, such as the level selection screen or the playing state, based on user actions.
 *
 * @see GameModel  The model that holds the game's state.
 * @see GameState  The current state of the game (e.g., menu, playing).
 * @see GameView   The view that displays the game's UI, including the menu.
 */
public class MenuController extends AbstractController implements MenuStateListener, MouseAdapter, KeyAdapter {

	private final GameStateController gameStateController;

	/**
	 * Constructs a MenuController with the provided GameModel and GameView.
	 *
	 * @param gameModel The model representing the game state.
	 * @param gameView  The view that displays the game's interface, including the menu.
	 */
	public MenuController(GameModel gameModel, GameView gameView, GameStateController gameStateController) {
		super(gameModel, gameView);
		this.gameModel = gameModel;
		this.gameView = gameView;

		this.gameStateController = gameStateController;
		gameView.getMenuView().setMenuStateListener(this);
	}

	/**
	 * Updates the menu state. Currently, no logic is implemented in this method,
	 * but it can be expanded to manage dynamic elements of the menu.
	 */
	@Override
	public void update() {
		// Currently no dynamic update logic for the menu.
	}

	/**
	 * Quits the game by exiting the application.
	 * This can be triggered when the player selects "Quit" in the menu.
	 */
	@Override
	public void quitGame() {
		System.exit(0);
	}

	/**
	 * Handles key press events. It listens for specific keys to transition between different game states:
	 * 'P' to start playing, and 'L' to go to the level selector.
	 *
	 * @param e The KeyEvent generated when a key is pressed.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_L -> switchToLevelSelector(); // Switch to level selector when 'L' is pressed
		}
	}

	@Override
	public void switchToGameRules() {
		gameView.getMenuView().displayRules();
	}



}
