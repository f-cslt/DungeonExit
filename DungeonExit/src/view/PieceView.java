package view;

import java.awt.*;
import javax.swing.*;
import model.Piece;
import model.PieceBlueprint;
import model.Tile;
import static utils.Constants.SCREEN_WIDTH;

/**
 * The PieceView class is responsible for rendering a graphical representation of a Piece.
 * It extends JPanel and overrides the paintComponent method to draw the piece.
 */
public class PieceView extends JPanel {

    private final Piece piece; // The piece to be displayed
    private int PIECE_SIZE = SCREEN_WIDTH / 100;

    private Color color;

    /**
     * Constructs a PieceView for a given Piece.
     * The JPanel is set to be transparent (opaque = false) to allow for custom rendering.
     *
     * @param piece The Piece object that will be visually represented.
     */
    public PieceView(Piece piece, Color color) {
        this.piece = piece;
        this.color = color;
        setOpaque(false); // Makes the background transparent
    }

    /**
     * Overrides the paintComponent method to draw the piece.
     * The piece is drawn as a series of filled squares following its track.
     *
     * @param g The Graphics object used for rendering.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Toolkit.getDefaultToolkit().sync(); // Helps with rendering performance
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int row = 0;
        int col = 0;

        if (piece.getBlueprint() == PieceBlueprint.BRIDGE) {
            g2.drawImage(Tile.TileType.BRIDGE.image, 0, 0, PIECE_SIZE * 3, PIECE_SIZE * 3, null);
            return ;
        }
        // Draw the piece track
        g2.setColor(color);
        for (int i = 0; i < piece.getTrack().size(); i++) {
            g2.fillRect(col * PIECE_SIZE, row * PIECE_SIZE, PIECE_SIZE - 1, PIECE_SIZE - 1); // Draw each block
            switch (piece.getBaseTrack().get(i)) {
                case UP -> row--;
                case DOWN -> row++;
                case LEFT -> col--;
                case RIGHT -> col++;
            }
        }
        // Draw the final piece block
        g2.fillRect(col * PIECE_SIZE, row * PIECE_SIZE, PIECE_SIZE - 1, PIECE_SIZE - 1);
    }
}
