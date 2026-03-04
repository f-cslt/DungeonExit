package view;

import java.awt.*;
import java.io.FileNotFoundException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import model.GameModel;
import static utils.Constants.*;

/**
 * The GameView class serves as the main view of the game.
 * It manages and switches between different subviews, such as the menu, level selection, and gameplay views.
 * This class extends JPanel and is responsible for rendering the correct view based on the game state.
 */
public class GameView extends JPanel {

	private final GameModel gameModel; // Reference to the game model
	private final MenuView menuView; // The main menu view
	private final LevelSelectorView levelSelectorView; // The level selector view
	private final LevelView levelView; // The level gameplay view
	private final MultiplayerView multiplayerView;
	private final VictoryMenuView victoryMenuView;
	private final LevelCreatorView levelCreatorView;
	private GameWindow window; // Reference to the game window

	/**
	 * Constructs the GameView with the specified game model.
	 * It initializes and manages different subviews of the game.
	 *
	 * @param gameModel the game model that stores game data and state.
	 */
	public GameView(GameModel gameModel) {
		this.gameModel = gameModel;

		setLayout(new BorderLayout()); // Allows child JPanels to fill the window

		/*
		 * Technically, a view should only render data from its model.
		 * However, passing the full GameModel instead of individual models makes the view more modular.
		 */
		this.menuView = new MenuView(gameModel);
		this.levelSelectorView = new LevelSelectorView(gameModel);
		this.levelView = new LevelView(gameModel.getLevelModel());
		this.multiplayerView = new MultiplayerView(gameModel.getMultiplayerModel());
		this.victoryMenuView = new VictoryMenuView();
		this.levelCreatorView = new LevelCreatorView(gameModel);

		// Set panel properties
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)); // TODO: Replace with constants
		this.setDoubleBuffered(true);
		this.setFocusable(true);

		// Display the menu view by default
		add(menuView);
	}

	/**
	 * Switches the currently displayed view to the specified JPanel.
	 *
	 * @param panel the JPanel to be displayed.
	 */
	public void switchToView(JComponent panel) {
		removeAll();
		add(panel);
		revalidate();
		repaint();
		setFocusable(true);
		requestFocusInWindow();
	}

	/**
	 * Switches the view to the main menu.
	 */
	public void switchToMenu() {
		switchToView(menuView);
	}

	/**
	 * Switches the view to the level gameplay screen.
	 */
	public void switchToLevel() {
		switchToView(levelView);
	}

	public void switchToMultiplayer() {
		switchToView(multiplayerView);
	}

	/**
	 * Switches the view to the level selection screen.
	 */
	public void switchToLevelSelector() {
		try {
			levelSelectorView.updatePane();
		} catch (FileNotFoundException e) {
		}
		levelSelectorView.switchToModeSelection();
		switchToView(levelSelectorView);
	}


	public void switchToVictoryMenu() {
		switchToView(victoryMenuView);
	}

	public void switchToLevelCreator() {
		switchToView(levelCreatorView);
	}


	public void showVictoryMenu() {
		removeAll(); // Remove all components from this panel
		add(victoryMenuView); // Add the victory menu
		revalidate();
		repaint();
	}

	/**
	 * Sets the game window reference.
	 * 
	 * @param window The GameWindow instance
	 */
	public void setWindow(GameWindow window) {
		this.window = window;
	}

	/**
	 * Gets the game window reference.
	 * 
	 * @return The GameWindow instance
	 */
	public GameWindow getWindow() {
		return window;
	}

	/**
	 * Renders the appropriate game view based on the current game state.
	 *
	 * @param g the Graphics2D object used for drawing.
	 */
	private void draw(Graphics2D g) {
		switch (gameModel.getGameState().getState()) {
			case LEVEL -> levelView.repaint();
			case MENU -> menuView.repaint();
			case LEVEL_SELECTOR -> levelSelectorView.repaint();
			case VICTORY -> victoryMenuView.repaint();
			case MULTIPLAYER -> multiplayerView.repaint();
			case LEVEL_CREATOR -> levelCreatorView.repaint();
		}
		revalidate();
		requestFocusInWindow();
	}

	/**
	 * Overrides the paintComponent method to handle rendering.
	 *
	 * @param g the Graphics object used for rendering.
	 */
	@Override
	public void paintComponent(Graphics g) {
		Toolkit.getDefaultToolkit().sync(); // Helps with rendering lag
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		draw(g2);
	}

	/**
	 * Gets the MenuView instance.
	 *
	 * @return the MenuView instance.
	 */
	public MenuView getMenuView() {
		return menuView;
	}

	/**
	 * Gets the LevelSelectorView instance.
	 *
	 * @return the LevelSelectorView instance.
	 */
	public LevelSelectorView getLevelSelectorView() {
		return levelSelectorView;
	}

	/**
	 * Gets the LevelView instance.
	 *
	 * @return the LevelView instance.
	 */
	public LevelView getLevelView() {
		return levelView;
	}

	public MultiplayerView getMultiplayerView() {
		return multiplayerView;
	}

	public VictoryMenuView getVictoryMenuView() {
		return victoryMenuView;
	}

	public LevelCreatorView getLevelCreatorView() {
		return levelCreatorView;
	}
}
