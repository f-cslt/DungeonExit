package controller;

import model.GameModel;
import view.GameView;
import view.GameWindow;

/**
 * This class acts as the main controller of the game. It instantiates other controllers as well as the
 * main model and the main view. It contains the game loop, which updates and draws the game at the correct
 * frame rate and update rate.
 *
 * @see GameModel  The model of the game that holds the game state.
 * @see GameView   The view responsible for rendering the game.
 * @see GameWindow The window where the game is displayed.
 */
public class GameController implements Runnable {

	private final GameModel gameModel;  // The main game model which holds the game data
	private final GameView gameView;    // The view of the game that displays everything to the user
	private final GameStateController gameStateController;  // Controller that manages the state of the game

	// You can add new controllers here if needed.
	// For example, a PlayerController or MapController might be added for more specific control
	// Might be better to handle that in a PlayingController instead.

	private Thread gameThread;  // Thread responsible for running the game loop
	private final int UPS = 150;  // Updates per second (how often the game state is updated)
	private int FPS = 60;  // Frames per second (how often the game is drawn)

	/**
	 * Constructor that initializes the GameController.
	 * Instantiates the model, view, and the game state controller, and starts the game thread.
	 */
	public GameController() {
		// Initialize game model, view, and state controller
		this.gameModel = new GameModel();
		this.gameView = new GameView(gameModel);
		this.gameStateController = new GameStateController(gameModel, gameView);

		// Register the GameStateController to listen to user input
		gameView.addKeyListener(gameStateController);
		gameView.addMouseListener(gameStateController);
		gameView.addMouseMotionListener(gameStateController);
		gameView.addMouseWheelListener(gameStateController);

		// Instantiate the game window with the game view
		GameWindow window = new GameWindow(gameView);
		gameView.setWindow(window);

		// Start the game loop in a separate thread
		startGameThread();
	}

	public GameStateController getGameStateController(){
		return gameStateController;
	}

	/**
	 * Starts the game thread that will run the game loop.
	 */
	private void startGameThread() {
		this.gameThread = new Thread(this);  // Create a new thread to run the game loop
		this.gameThread.start();  // Start the game loop
	}

	/**
	 * Updates the game state by calling the game state controller's update method.
	 */
	private void update() {
		gameStateController.update();
	}

	/**
	 * Draws the game on the screen by calling the view's repaint method.
	 */
	private void draw() {
		gameView.repaint();  // Repaint the view to reflect the current game state
	}

	/**
	 * The game loop using the delta method to update and draw the game at the correct frame rate and update rate.
	 * The loop runs continuously, adjusting the game updates and drawing based on the desired FPS and UPS.
	 * The player can change the FPS (for example, through a menu) without affecting the game speed.
	 */
	@Override
	public void run() {
		double frameInterval = 1000000000.0 / FPS;  // The interval between frames in nanoseconds
		double updateInterval = 1000000000.0 / UPS;  // The interval between updates in nanoseconds

		long oneSecondCheck = System.currentTimeMillis();  // Used to track FPS and UPS per second
		long lastTime = System.nanoTime();  // The time of the last iteration of the game loop
		long currentTime;

		int frames = 0;  // The number of frames drawn in the last second
		int updates = 0;  // The number of updates performed in the last second

		double deltaUpdate = 0;  // Accumulated time for updates
		double deltaFrame = 0;   // Accumulated time for frames

		// Main game loop
		while (gameThread != null) {
			currentTime = System.nanoTime();  // Get the current time in nanoseconds

			// Calculate the accumulated time for updates and frames
			deltaUpdate += (currentTime - lastTime) / updateInterval;
			deltaFrame += (currentTime - lastTime) / frameInterval;
			lastTime = currentTime;  // Update the last time to the current time

			// If enough time has passed for an update, perform the update
			if (deltaUpdate >= 1) {
				update();  // Update the game state
				updates++;  // Increment the update count
				deltaUpdate--;  // Decrease the delta for updates
			}

			// If enough time has passed for a frame, perform the drawing
			if (deltaFrame >= 1) {
				draw();  // Draw the game
				frames++;  // Increment the frame count
				deltaFrame--;  // Decrease the delta for frames
			}

			// Once a second has passed, log the FPS and UPS
			if (System.currentTimeMillis() - oneSecondCheck >= 1000) {
				oneSecondCheck = System.currentTimeMillis();  // Reset the one-second timer
				frames = 0;  // Reset the frames count
				updates = 0;  // Reset the updates count
			}
		}
	}
}
