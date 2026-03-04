package controller;


import java.awt.event.MouseWheelEvent;

import model.GameModel;
import model.GameState;
import view.GameView;

public class VictoryMenuController extends AbstractController{
 

    public VictoryMenuController(GameModel gameModel, GameView gameView) {
        super(gameModel, gameView);
        this.gameModel = gameModel;
        this.gameView = gameView;

        // Add action listener to main menu button
        gameView.getVictoryMenuView().getMainMenuButton().addActionListener(e -> {
            // Set state to MENU and update view
            gameModel.getGameState().setState(GameState.States.MENU);
            gameView.switchToMenu();
        });
    }
    @Override
    protected void update() {}
}
