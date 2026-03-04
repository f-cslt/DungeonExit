package model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The Tile class represents a single tile in the game board.
 * Each tile has a type that determines its properties and behavior in the game.
 */
public class Tile {

    private TileType type; // The type of the tile (e.g., entry, exit, path, etc.)
    private int player;
    public BufferedImage image;

    /**
     * Constructs a Tile with the default type.
     * The default tile type is set to DEFAULT.
     */
    public Tile() {
        type = TileType.DEFAULT; // Initialize the tile type to the default value
    }

    /**
     * Constructs a copy of a tile
     * @param other the tile to copy
     */
    public Tile(Tile other) {
        this.type = other.type;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }
    /**
     * Returns a string representation of the tile's type.
     * The string will be the name of the tile's type (from the TileType enum).
     *
     * @return the name of the tile's type as a string.
     */
    @Override
    public String toString() {
        return type.name(); // Returns the name of the tile's type
    }

    /**
     * Gets the current type of the tile.
     *
     * @return the type of the tile.
     */
    public TileType getType() {
        return type; // Returns the type of the tile
    }

    /**
     * Sets the type of the tile.
     *
     * @param type the new type to assign to the tile.
     */
    public void setType(TileType type) {
        this.type = type; // Sets the new type for the tile
    }

    public char getLetter() {
        return switch (type) {
            case EMPTY -> '1';
            case DEFAULT -> '.';
            case PATH -> 'O';
            case KEY -> 'K';
            case EXIT -> '>';
            case CHEST -> 'C';
            case DRAGON -> 'D';
            case ENTRY -> 'E';
            case BRIDGE -> 'B';
            case STAIRS -> 'S';
            case PATH2 -> '2';
        };
    }


        public enum TileType {
        EMPTY("/tiles/new/default.png"),
        DEFAULT("/tiles/new/default2.png"),
        ENTRY("/tiles/new/entry.png"),
        KEY("/tiles/new/key.png"),
        CHEST("/tiles/new/chest/chest1.png"),
        DRAGON("/tiles/new/dragon.png"),
        EXIT("/tiles/new/exit.png"),
        PATH("/tiles/new/player1/2openPath.png"),
        BRIDGE("/tiles/new/bridge.png"),
        STAIRS("/tiles/new/stairs.png"),
        PATH2("/tiles/new/player2/2openPath.png");

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        public BufferedImage image;

        TileType(String imagePath) {
            try {
                image = ImageIO.read(getClass().getResource(imagePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
}
}
