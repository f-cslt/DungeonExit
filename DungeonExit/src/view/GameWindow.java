package view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import static utils.Constants.BOARD_MINIMUM_HEIGHT;
import static utils.Constants.BOARD_MINIMUM_WIDTH;

/**
 * The GameWindow class represents the main game window.
 * It extends JFrame and is responsible for displaying the game view.
 */
public class GameWindow extends JFrame {

	private boolean isFullscreen = false;
	private GraphicsDevice device;

	/**
	 * Constructs the GameWindow with the specified GameView.
	 * Initializes the window properties such as title, close operation, size, and visibility.
	 *
	 * @param gameView the GameView instance to be displayed inside the window.
	 */
	public GameWindow(GameView gameView) {


		this.setTitle("Dungeon Exit"); // Sets the window title
		try {
			this.setIconImage(ImageIO.read(new File("res/tiles/new/player1/basePath.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); // Closes the application when the window is closed
		this.add(gameView); // Adds the main game view to the window
		this.pack(); // Adjusts the window size to fit the preferred size of its components
		// this.setResizable(false); // Prevents resizing of the window
		this.setLocationRelativeTo(null); // Centers the window on the screen
		this.setVisible(true); // Makes the window visible

		// Initialize fullscreen capability
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		device.setFullScreenWindow(this);

		// Add window state listener to handle native fullscreen
		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				if ((e.getOldState() & Frame.MAXIMIZED_BOTH) == 0 && 
					(e.getNewState() & Frame.MAXIMIZED_BOTH) != 0) {
					// Window is being maximized/fullscreened
					setResizable(false);
					isFullscreen = true;
				} else if ((e.getOldState() & Frame.MAXIMIZED_BOTH) != 0 && 
						 (e.getNewState() & Frame.MAXIMIZED_BOTH) == 0) {
					// Window is being restored from maximized/fullscreen
					device.setFullScreenWindow(null);
					setResizable(false);
					pack();
					setLocationRelativeTo(null);
					isFullscreen = false;
				}
			}
		});
	}

	/**
	 * Toggles fullscreen mode for the game window.
	 */
	public void toggleFullscreen() {
		if (isFullscreen) {
			// Exit fullscreen
			device.setFullScreenWindow(null);
			setResizable(false);
			pack();
			setLocationRelativeTo(null);
		} else {
			// Enter fullscreen
			setResizable(true);
			device.setFullScreenWindow(this);
		}
		isFullscreen = !isFullscreen;
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(BOARD_MINIMUM_WIDTH, BOARD_MINIMUM_HEIGHT);
	}
}
