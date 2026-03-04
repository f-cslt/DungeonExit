package controller;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import model.GameModel;
import model.MultiplayerModel;
import utils.KeyAdapter;
import utils.MouseAdapter;
import utils.SoundManager;
import view.GameView;
import view.MultiplayerView;

public class MultiplayerController extends AbstractController implements MouseAdapter, KeyAdapter {

	private MultiplayerModel multiplayerModel;
	private MultiplayerView multiplayerView;

	private BoardController boardController;

	private boolean checkWin;
	private Thread winThread;

	private static final String GAMEPLAY_MUSIC_PATH = "res/sounds/multiplayer.wav";
	private static final String SWITCH_ROUND_SF = "res/sounds/switch.wav";
	
	public MultiplayerController(GameModel gameModel, GameView gameView) {
		super(gameModel, gameView);
		this.multiplayerModel = gameModel.getMultiplayerModel();
		this.multiplayerView = gameView.getMultiplayerView();
		this.checkWin = false;

		multiplayerView.getChangeRoundButton().addActionListener(e -> {
			switchRound();
		});

		multiplayerView.getChangeLevelButtonUp().addActionListener(e -> {
			changeLevel(multiplayerModel.getCurrentBoard() + 1);
		});

		multiplayerView.getChangeLevelButtonDown().addActionListener(e -> {
			changeLevel(multiplayerModel.getCurrentBoard() - 1);
		});

		multiplayerView.getBackButton().addActionListener(e -> {
			SoundManager.getInstance().stopAll();
			switchToLevelSelector();
		});
		
		multiplayerView.getDoneButton().addActionListener(e -> {
			checkWin = true;
			multiplayerView.animPlay();
			winThread = new Thread(new Runnable() {
				@Override
				public void run() {
					boolean win = boardController.checkWinCondition(winThread, multiplayerModel.getCurrentRound());
					if (win) {
						gameView.getVictoryMenuView().setCurrentVictory(multiplayerModel.getCurrentRound());
						gameView.switchToVictoryMenu();
						SoundManager.getInstance().stopAll();
						SoundManager.getInstance().playBackgroundMusic("res/sounds/victory.wav");
					} else {
						switchRound();
					}
					checkWin = false;
					multiplayerView.animFinish();
				}
			});
			winThread.start();
		});
		SoundManager.getInstance().loadSoundEffect("switch", SWITCH_ROUND_SF);
	}

	public void initMultiplayerController() {
		this.boardController = new BoardController(
				multiplayerModel.getBoard(),
		 		multiplayerView.getBoardView(),
				multiplayerModel.getPieceBagPlayer1(),
				multiplayerModel.getPieceBagPlayer2(),
				multiplayerView.getPieceBagViewPlayer1(),
				multiplayerView.getPieceBagViewPlayer2());
		winThread = null;
		SoundManager.getInstance().playBackgroundMusic(GAMEPLAY_MUSIC_PATH);
	}

	public void changeLevel(int index){
		multiplayerView.removeCurrentBoardView();
		multiplayerModel.changeLevel(index);
		multiplayerView.changeLevelView();
		boardController.setCurrentBoard(index, multiplayerModel.getNbBoard());
	}

	public void switchRound() {
		SoundManager.getInstance().playSoundEffect("switch");
		switch (multiplayerModel.getCurrentRound()) {
			case 1-> {
				multiplayerView.switchRound(2);
				multiplayerModel.setCurrentRound(2);
				boardController.setCurrentPlayer(1);
            }
			case 2 -> {
				multiplayerView.switchRound(1);
				multiplayerModel.setCurrentRound(1);
				boardController.setCurrentPlayer(0);
			}
		}
		multiplayerView.activeRoundPanel();
		multiplayerModel.resetTimer();
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
				// Stop gameplay music before switching to menu
				SoundManager.getInstance().stopAll();
				switchToMenu();
			}
			case KeyEvent.VK_L -> {
				// Stop gameplay music before switching to level selector
				SoundManager.getInstance().stopAll();
				switchToLevelSelector();
			}
			default -> invokeUserInputHandling(boardController, "keyPressed", e);
		}
	}

	@Override
	protected void update() {
		if (!checkWin) {
			gameModel.getMultiplayerModel().update();
		}
		if (multiplayerModel.getElapsedTimeInSeconds() > multiplayerModel.getRoundTime()) {
			switchRound();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		boardController.mouseWheelMoved(e);
	}
}
