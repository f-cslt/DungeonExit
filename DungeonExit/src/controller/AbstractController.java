package controller;

import java.awt.event.InputEvent;
import java.lang.reflect.Method;
import model.GameModel;
import model.GameState;
import utils.SoundManager;
import view.GameView;

public abstract class AbstractController{
    
    protected GameModel gameModel;
    protected GameView gameView;

	// Path to gameplay music
	protected static final String GAMEPLAY_MUSIC_PATH = "res/sounds/Piano_ 双極ノ悪夢.wav";
	private static final String BACKGROUND_MUSIC_PATH = "res/sounds/main-menu-music.wav";



    public AbstractController(GameModel gameModel, GameView gameView){
        this.gameModel = gameModel;
        this.gameView = gameView;
    }

    /**
     * Switches the game state to the "playing" state and switches to the level view.
     * This can be triggered when the player decides to start playing the level.
     */
    public void switchToLevel() {
        gameModel.getGameState().setState(GameState.States.LEVEL);
        gameView.switchToLevel();
		// Path to gameplay music
		SoundManager.getInstance().stopAll();
		SoundManager.getInstance().playBackgroundMusic(GAMEPLAY_MUSIC_PATH);

    }

	public void switchToMultiplayer() {
		gameModel.getGameState().setState(GameState.States.MULTIPLAYER);
		gameView.switchToMultiplayer();
	}

    /**
     * Switches the game state to the "menu" state and switches back to the menu view.
     * This can be triggered when the player decides to go back to the main menu.
     */
    public void switchToMenu() {
        gameModel.getGameState().setState(GameState.States.MENU);
        gameView.switchToMenu();
		SoundManager.getInstance().stopAll();
		SoundManager.getInstance().playBackgroundMusic(BACKGROUND_MUSIC_PATH);

		

    }

    /**
	 * Switches the game state to the "level selector" state and transitions to the level selector view.
	 * This can be triggered when the player selects "Select Level" in the menu.
	 */
	public void switchToLevelSelector() {
		gameModel.getGameState().setState(GameState.States.LEVEL_SELECTOR);
		gameView.switchToLevelSelector();
		// Stop gameplay music before switching to level selector
		SoundManager.getInstance().stopAll();
	}

	public void switchToLevelCreator() {
		gameModel.getGameState().setState(GameState.States.LEVEL_CREATOR);
		gameView.switchToLevelCreator();
		SoundManager.getInstance().stopAll();
		SoundManager.getInstance().playBackgroundMusic(GAMEPLAY_MUSIC_PATH);

	}

    /**
     * Each controller has an update method.
     */
    protected abstract void update();

    /**
	 * A reflective method that delegates user input events to the appropriate controller's method.
	 *
	 * @param controller The controller where the method is located.
	 * @param funcName The name of the method to invoke in the controller.
	 * @param evt The event to pass to the method.
	 */
	protected void invokeUserInputHandling(Object controller, String funcName, InputEvent evt) {
		try {
			Method method = controller.getClass().getMethod(funcName, new Class[] { evt.getClass() });
			method.invoke(controller, evt);
		} catch (Exception ex) {
			System.err.println("FAIL INVOKE: " + controller.getClass().getName() + funcName);
			ex.printStackTrace();
		}
	}
    
}
