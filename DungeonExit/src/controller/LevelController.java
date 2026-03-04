package controller;

import java.awt.event.KeyEvent;
import listeners.LevelListener;
import model.GameModel;
import model.GameState;
import model.LevelModel;
import utils.KeyAdapter;
import utils.MouseAdapter;
import utils.SoundManager;
import view.GameView;
import view.LevelView;

/**
 * The PlayingController class is responsible for handling events and actions that occur while the player is in the game playing state.
 * It listens to user input from the keyboard and mouse, allowing the player to interact with the game world.
 * The controller is also responsible for managing the state transitions, such as switching to the menu or level selector.
 *
 * @see GameModel  The model representing the game state and player actions.
 * @see GameState  The current state of the game (e.g., playing, menu).
 * @see GameView   The view that displays the game interface during the playing state.
 */
public class LevelController extends AbstractController implements LevelListener, MouseAdapter, KeyAdapter {

	private final GameModel gameModel;
	private final GameView gameView;
	private final GameStateController gameStateController;
	private BoardController boardController;
	private final LevelModel levelModel;
	private final LevelView levelView;

	private Thread winThread;

	/**
	 * Constructs a PlayingController with the provided GameModel and GameView.
	 *
	 * @param gameModel The model that represents the current game state and actions.
	 * @param gameView  The view that handles rendering the game's playing screen.
	 */
	public LevelController(GameModel gameModel, GameView gameView, GameStateController gameStateController) {
		super(gameModel, gameView);
		this.gameModel = gameModel;
		this.gameView = gameView;
		this.gameStateController = gameStateController;
		this.levelModel = gameModel.getLevelModel();
		this.levelView = gameView.getLevelView();

		gameView.getLevelView().setLevelListener(this);

		gameView.getLevelView().getCheckWinButton().addActionListener(e -> {
			// boardController.resetAnim();
			gameView.getLevelView().animPlay();
			winThread = new Thread(() -> {
                            boolean win = boardController.checkWinCondition(winThread, 0);
                            if (win) {
                                gameView.getVictoryMenuView().setCurrentVictory(0);
                                gameView.switchToVictoryMenu();
                                writeNewFile();
                                
                                SoundManager.getInstance().stopAll();
                                SoundManager.getInstance().playBackgroundMusic("res/sounds/victory.wav");
                            }
                            gameView.getLevelView().animFinish();
                        });
			winThread.start();
		});
		winThread = null;
	}

	private void writeNewFile() {
		levelModel.checkAndUpdateBestTimeAndHighscore();
	}

	public void initLevelControllers(){
		this.boardController = new BoardController(
				gameModel.getLevelModel().getBoard(),
				gameView.getLevelView().getBoardView(),
				gameModel.getLevelModel().getPieceBag(),
				gameView.getLevelView().getPieceBagView());
	}

	@Override
	public void solve(){
		levelModel.solve();
		// Need to update the piece bag view.
		levelView.getPieceBagView().draw();
	}

	@Override
	public void clean(){
		levelModel.cleanBeforeSolve();
		levelView.getPieceBagView().draw();
	}

	@Override
	public void changeLevel(int index){
		levelView.removeCurrentBoardView();
		levelModel.changeLevel(index);
		levelView.changeLevelView();
		boardController.setCurrentBoard(index, levelModel.getNbBoard());
	}

	@Override
	public void checkWin(){}

	@Override
	public String getCurrentTime() {
	    return levelModel.getFormattedTime();

	}

	/**
	 * Updates the game state during gameplay. This could include updates to player movement,
	 * game environment, or any other gameplay-related logic.
	 */
	@Override
	public void update() {
		levelModel.update();
	}

	/**
	 * Handles key press events. It listens for specific keys to allow the player to switch between different game states:
	 * 'M' to return to the menu, and 'L' to open the level selector.
	 *
	 * @param e The KeyEvent generated when a key is pressed.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_M -> {
				switchToMenu();
			}
			case KeyEvent.VK_L -> {
				switchToLevelSelector();
			}
			default -> invokeUserInputHandling(boardController, "keyPressed", e);
		}
	}
}
