package controller;

import listeners.LevelCreatorListener;
import model.GameModel;
import model.LevelCreator;
import model.Piece;
import model.Tile.TileType;
import utils.KeyAdapter;
import utils.MouseAdapter;
import utils.SoundManager;
import view.GameView;
import view.LevelCreatorView;

public class LevelCreatorController extends AbstractController implements KeyAdapter, MouseAdapter, LevelCreatorListener {

	private final GameStateController gameStateController;
    private final LevelCreator levelCreator;
    private final LevelCreatorView levelCreatorView;
    private final BoardController boardController;
    private boolean isShowingFullPieceBag = false;
    private Thread winThread;

    // Path to gameplay music
	private static final String GAMEPLAY_MUSIC_PATH = "res/sounds/Piano_ 双極ノ悪夢.wav";

    public LevelCreatorController(GameModel gameModel, GameView gameView, GameStateController gameStateController) {
        super(gameModel, gameView);
        this.gameStateController = gameStateController;
        this.levelCreator = gameModel.getLevelCreator();
        this.levelCreatorView = gameView.getLevelCreatorView();
        this.boardController = new BoardController(
            levelCreator.getBoard(),
            levelCreatorView.getBoardView(),
            levelCreator.getPieceBag(),
            levelCreatorView.getPieceBagView()
        );
        initializeListener();

       
		winThread = null;
    }

    private void initializeListener() {
        gameView.getLevelCreatorView().setLevelCreatorListener(this);
    }
    

    @Override
    protected void update() {}

    @Override
    public void addBoard() {
        if (levelCreator.getNbBoard() < 2) {
            levelCreator.addBoard();
            levelCreatorView.addBoardView();
            boardController.addListeners();
        }
    }

    @Override
    public void removeBoard() {
        if(levelCreator.getNbBoard() > 1) {
            if(levelCreator.getCurrentBoard() == levelCreator.getNbBoard() - 1) {
            boardController.removeListeners();
            levelCreator.removeBoard();
            boardController.setCurrentBoard(levelCreator.getCurrentBoard(), levelCreator.getNbBoard());
            levelCreatorView.removeBoardView();
            levelCreatorView.addCurrentBoardView();
            } else {
                boardController.removeListeners();
                levelCreator.removeBoard();
                levelCreatorView.removeBoardView();
            }
        } 
    }

	@Override
	public void changeLevel(int index) {
		levelCreatorView.removeCurrentBoardView();
		levelCreator.changeLevel(index);
		levelCreatorView.changeLevelView();
		boardController.setCurrentBoard(index, levelCreator.getNbBoard());
    }

    @Override
    public void handlePieceSelection(Piece piece) {
        levelCreator.handlePieceSelection(piece);
        levelCreatorView.getPieceBagView().draw();
        levelCreatorView.getFullPieceBagView().draw();
    }

    @Override
    public void openFullPieceBag() {
        isShowingFullPieceBag = !isShowingFullPieceBag;
        if (isShowingFullPieceBag) {
            // Show full piece bag
            levelCreatorView.displayFullPieceBag(levelCreator.getFullPieceBag());
        } else {
            // Show regular piece bag
            levelCreatorView.displayRegularPieceBag();
        }
    }

    @Override
    public void checkpointClicked(TileType checkpoint) {
        levelCreator.addCheckpointSelectionToPieceBag(checkpoint);
    }

    @Override
    public void save() {
        gameView.getLevelCreatorView().animPlay();
        winThread = new Thread(() -> {
            boolean win = boardController.checkWinCondition(winThread, 0);
            if (win) {
                SoundManager.getInstance().stopAll();
                SoundManager.getInstance().playBackgroundMusic("res/sounds/victory.wav");
                
                // Show name input dialog on EDT to avoid threading issues
                javax.swing.SwingUtilities.invokeLater(() -> {
                    levelCreatorView.showLevelNameDialog();
                });
            } else {
                // Inform user that the path is not valid
                javax.swing.SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(
                        gameView.getLevelCreatorView(),
                        "The current path is not valid. Make sure all checkpoints are connected.",
                        "Invalid Path",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                    );
                });
            }
            gameView.getLevelCreatorView().animFinish();
        });
        winThread.start();
    }
    
    /**
     * Save the level with a specified name
     * 
     * @param levelName The name to save the level as
     */
    @Override
    public void saveWithName(String levelName) {
        try {
            levelCreator.save(levelName);
            
            // Play success sound
            SoundManager.getInstance().playSoundEffect("button_click");
            
            // Show success message on EDT
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(
                    gameView.getLevelCreatorView(), 
                    "Level saved successfully as '" + levelName + "'!", 
                    "Success", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
                changeMusicVictoryToBasic();
                
            });
        } catch (Exception e) {
            // Show error message
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(
                    gameView.getLevelCreatorView(), 
                    "Error saving level: " + e.getMessage(), 
                    "Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
            });
        }
    }

    @Override
    public void changeMusicVictoryToBasic(){
        SoundManager.getInstance().stopAll();
        SoundManager.getInstance().playBackgroundMusic(GAMEPLAY_MUSIC_PATH);
    }

    @Override
    public void checkWin() {
        // boardController.resetAnim();
        gameView.getLevelCreatorView().animPlay();
        winThread = new Thread(() -> {  
                        boolean win = boardController.checkWinCondition(winThread, 0);
                        gameView.getLevelCreatorView().animFinish();
                    });
        winThread.start();
		
    }

    @Override
    public void solve() {
        levelCreator.solve();
        levelCreatorView.getPieceBagView().draw();
    }

    @Override
    public void clean() {
        levelCreator.cleanBeforeSolve();
        levelCreatorView.getPieceBagView().draw();
    }

    
    
}