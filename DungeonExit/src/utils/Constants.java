package utils;

import java.awt.Toolkit;

public class Constants {

    public static final int SCREEN_WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(); // Default screen width
	public static final int SCREEN_HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight(); // Default screen height

    public static final int BOARD_MINIMUM_WIDTH = 400;
    public static final int BOARD_MINIMUM_HEIGHT = 400;

    public static final int PIECE_BAG_MINIMUM_WIDTH = 180;
    public static final int PIECE_BAG_MINIMUM_HEIGHT = 300;

    // Not used in code
    public static class LevelConstants{
        public static final int TILE_SIZE = 800 / 25;
        public static final int CENTER_X = 800 / 10;
        public static final int CENTER_Y = 600/10;
    }
}
