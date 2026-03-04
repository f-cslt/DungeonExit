package controller;

import java.awt.event.*;
import model.GameModel;
import utils.KeyAdapter;
import utils.MouseAdapter;
import view.GameView;

/**
 * This class is used to create and instantiate new controllers for different game states.
 * Ideally, we will have a controller for each game state (e.g., Level, Menu, Level Selector).
 *
 * This class delegates user actions to the appropriate controller based on the current game state.
 * It also provides methods to handle user input, such as keyboard and mouse events.
 *
 * @see GameModel  The model of the game that holds the game state.
 * @see GameView   The view responsible for rendering the game.
 * @see MenuController The controller responsible for handling the menu state.
 * @see LevelController The controller responsible for handling the Level state.
 * @see LevelController The controller responsible for handling the level selection state.
 */
public class GameStateController extends AbstractController implements MouseAdapter, KeyAdapter {

	// Controllers for different game states
	private MenuController menuController;
	private LevelController levelController;
	private LevelSelectorController levelSelectorController;
	private MultiplayerController multiplayerController;
	private VictoryMenuController victoryMenuController;
	private LevelCreatorController levelCreatorController;


	/**
	 * Constructor that initializes the GameStateController with the game model and view.
	 * It also instantiates the controllers for the different game states.
	 *
	 * @param gameModel The model of the game.
	 * @param gameView The view of the game.
	 */
	public GameStateController(GameModel gameModel, GameView gameView) {
		super(gameModel, gameView);
		this.gameModel = gameModel;
		this.gameView = gameView;

		initGamestates();
	}

	private void initGamestates(){
		this.levelController = new LevelController(gameModel, gameView, this);
		this.menuController = new MenuController(gameModel, gameView, this);
		this.levelSelectorController = new LevelSelectorController(gameModel, gameView, this);
		this.victoryMenuController = new VictoryMenuController(gameModel, gameView);
		this.multiplayerController = new MultiplayerController(gameModel, gameView);
		this.levelCreatorController = new LevelCreatorController(gameModel, gameView, this);
	}

	public LevelCreatorController getLevelCreatorController(){
		return levelCreatorController;
	}

	public MenuController getMenuController() {
		return menuController;
	}

	public LevelController getLevelController() {
		return levelController;
	}

	public LevelSelectorController getLevelSelectorController() {
		return levelSelectorController;
	}

	public MultiplayerController getMultiplayerController() {
		return multiplayerController;
	}

	/**
	 * Update the game depending on the current game state.
	 * This method delegates the update task to the appropriate controller based on the game state.
	 */
	@Override
	public void update() {
		// Check the current game state and delegate update to the appropriate controller
		switch (gameModel.getGameState().getState()) {
			case LEVEL -> levelController.update();  // Delegate to LevelController
			case MENU -> menuController.update();  // Delegate to MenuController
			case LEVEL_SELECTOR -> levelSelectorController.update();  // Delegate to LevelController
			case VICTORY -> gameView.switchToVictoryMenu();
			case MULTIPLAYER -> multiplayerController.update();
			case LEVEL_CREATOR -> levelCreatorController.update();
		}
	}

	/**
	 * Delegates the user input event to the appropriate controller based on the current game state.
	 *
	 * @param funcName The name of the function (e.g., 'keyPressed' or 'mouseClicked').
	 * @param evt The event that occurred.
	 */
	void delegateUserInputHandling(String funcName, InputEvent evt) {
		Object controller;
		switch (gameModel.getGameState().getState()) {
			case LEVEL -> controller = levelController;  // Assign LevelController
			case MENU -> controller = menuController;  // Assign MenuController
			case LEVEL_SELECTOR -> controller = levelSelectorController;  // Assign LevelController
			case MULTIPLAYER -> controller = multiplayerController;
			case LEVEL_CREATOR -> controller = levelCreatorController;
			default -> {
                        return;  // No action if the game state is not recognized
            }
		}
		// Invoke the method on the appropriate controller
		invokeUserInputHandling(controller, funcName, evt);
	}

	// Methods for handling key events
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_F11 -> {
				gameView.getWindow().toggleFullscreen();
			}
		}
		delegateUserInputHandling("keyPressed", e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		delegateUserInputHandling("keyReleased", e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		delegateUserInputHandling("keyTyped", e);
	}

	// Methods for handling mouse events
	@Override
	public void mouseDragged(MouseEvent e) {
		delegateUserInputHandling("mouseDragged", e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		delegateUserInputHandling("mouseMoved", e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		delegateUserInputHandling("mouseClicked", e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		delegateUserInputHandling("mouseReleased", e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		delegateUserInputHandling("mousePressed", e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		delegateUserInputHandling("mouseEntered", e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		delegateUserInputHandling("mouseExited", e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		delegateUserInputHandling("mouseWheelMoved", e);
	}
}
