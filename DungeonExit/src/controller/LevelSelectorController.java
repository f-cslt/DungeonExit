package controller;

import java.awt.event.KeyEvent;
import java.io.File;
import listeners.LevelSelectorListener;
import model.GameModel;
import utils.KeyAdapter;
import utils.MouseAdapter;
import view.GameView;
import view.LevelSelectorView;

public class LevelSelectorController extends AbstractController implements LevelSelectorListener, MouseAdapter, KeyAdapter {

	private final GameModel gameModel;
    private final GameView gameView;
    private final LevelSelectorView levelSelectorView;

    private final GameStateController gameStateController;

    /**
     * Constructs a LevelController to manage the level model, level view,
     * and user interactions with the level. It also initializes the BoardController
     * to handle interactions with the game board.
     *
     * @param gameModel The model representing the game.
     * @param gameView The view for the game.
     */

    public LevelSelectorController(GameModel gameModel, GameView gameView, GameStateController gameStateController) {
        super(gameModel, gameView);
        this.gameModel = gameModel;
        this.gameView = gameView;

        this.gameStateController = gameStateController;
        this.levelSelectorView = gameView.getLevelSelectorView();
        this.levelSelectorView.setMode("map");
        gameView.getLevelSelectorView().setLevelSelectionListener(this);
    }


    @Override
    public void removeSurePane(){
        levelSelectorView.remove(levelSelectorView.getSurePane());
        try {
            levelSelectorView.updatePane();
        } catch (Exception e) {}
    }

    @Override
    public void putSurePane(){
        levelSelectorView.add(levelSelectorView.getSurePane());
        levelSelectorView.setLayer(levelSelectorView.getSurePane(), 2);
    }

    @Override
    public void launchLevel(File file) {
        try {
            if (gameView.getLevelSelectorView().getMode().equals("map")) {
                gameView.getLevelView().resetLevel();
                gameModel.getLevelModel().initLevelModels(file);
                gameModel.getLevelModel().resetTimer(); 
                gameView.getLevelView().initLevelViews();
                gameStateController.getLevelController().initLevelControllers();
                switchToLevel();
            }
            else if (gameView.getLevelSelectorView().getMode().equals("mult")) {
                gameView.getMultiplayerView().resetLevel();
                gameModel.getMultiplayerModel().initMultModels(file);
                gameModel.getMultiplayerModel().resetTimer(); 
                gameView.getMultiplayerView().initMultiplayerViews();
                gameStateController.getMultiplayerController().initMultiplayerController();
                switchToMultiplayer();
            }
        } catch (Exception e) {}
    }

    /**
     * Updates the level model. This method is called each game loop cycle to
     * keep the level in sync with the game state.
     */
    @Override
    public void update() {
        
    }

    /**
     * Handles the key press event. It listens for specific keys to switch between
     * game states, such as pressing 'P' to start Level the level and 'M' to go back to the menu.
     *
     * @param e The KeyEvent generated when a key is pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()){
            case KeyEvent.VK_M -> switchToMenu();
        }
    }

}
