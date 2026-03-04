package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import model.Piece;
import model.PieceBag;
import static utils.Constants.SCREEN_HEIGHT;
import static utils.Constants.SCREEN_WIDTH;
import static utils.Constants.PIECE_BAG_MINIMUM_WIDTH;
import static utils.Constants.PIECE_BAG_MINIMUM_HEIGHT;

/**
 * The PieceBagView class represents a graphical interface for displaying a collection of Tetris-like pieces.
 * It provides a scrollable panel where each piece is displayed as a button that the player can select.
 */
public class PieceBagView extends JPanel {

    protected final PieceBag pieceBag;
    protected final JPanel scrollPanel;
    protected static final int BORDER_THICKNESS = 3;
    protected static final Color BORDER_COLOR = new Color(150, 80, 150); // Darker pink/purple
    protected static final int PADDING = 10;
    protected Color color;

    /**
     * Constructs a PieceBagView that visually represents a given PieceBag.
     * The pieces are displayed in a vertical scrollable list.
     *
     * @param pieceBag The PieceBag instance containing the list of available pieces.
     */
    public PieceBagView(PieceBag pieceBag, Color color) {
        this.pieceBag = pieceBag;
        this.color = color;
        
        // Set layout and background
        setFocusable(false);
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40)); // Dark background

        // Set a styled border with padding
        setBorder(new CompoundBorder(
            new LineBorder(color, BORDER_THICKNESS, true), // Rounded corners
            new EmptyBorder(PADDING, PADDING, PADDING, PADDING))); // Inner padding

        // Create a panel to hold the pieces with a dark background
        scrollPanel = createScrollPanel();

        // Add a title label
        JLabel titleLabel = createTitleLabel();
        add(titleLabel, BorderLayout.NORTH);

        // Wrap the scroll panel in a JScrollPane with custom styling
        JScrollPane scrollPane = createJScrollPane();
        // Add the scroll pane to this panel
        add(scrollPane, BorderLayout.CENTER);

        // Populate the view with pieces
        initializeView();
    }
    
    /**
     * Initialize the view - calls draw() by default.
     * Subclasses can override this method if they need additional setup before drawing.
     */
    protected void initializeView() {
        draw();
    }

    protected JScrollPane createJScrollPane() {
        Dimension scrollPaneSize = new Dimension(SCREEN_WIDTH / 6, SCREEN_HEIGHT - SCREEN_HEIGHT / 3);
        JScrollPane scrollPane = new JScrollPane(scrollPanel);
        scrollPane.setPreferredSize(scrollPaneSize);
        scrollPane.setMaximumSize(scrollPaneSize);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(new LineBorder(color, 1));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100);
                this.trackColor = new Color(50, 50, 50);
            }
        });
        return scrollPane;
    }

    protected JPanel createScrollPanel() {
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        scrollPanel.setBackground(new Color(30, 30, 30)); // Darker background for contrast
        scrollPanel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        return scrollPanel;
    }


    protected JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("Piece Selection");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titleLabel;
    }

    protected JPanel createPieceButton() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 40));
        panel.setMaximumSize(new Dimension(180, 80));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }   

    protected JButton createStyledButton() {
        // Create a styled button for each piece
        JButton button = new JButton();
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        button.setBorder(new LineBorder(new Color(0, 0, 0, 0), 2, true));
        button.setBackground(new Color(60, 60, 60));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 70));
        return button;
    }
    

    /**
     * Draws the pieces inside the scroll panel.
     * Each piece is represented as a button that allows selection.
     */
    public void draw() {
        scrollPanel.removeAll(); // Clear existing components

        // Add spacing at the top
        scrollPanel.add(Box.createVerticalStrut(10));
        
        // Iterate through the list of pieces and add them as buttons
        for (Piece piece : pieceBag.getPieces()) {
            JPanel panel = createPieceButton();
            scrollPanel.add(panel);

            // Create a styled button for each piece
            JButton button = createStyledButton();
           
            button.addActionListener(e -> pieceBag.setSelectedPiece(piece)); // Selects the clicked piece
            panel.add(button);

            // Create a visual representation of the piece and add it to the button
            PieceView pieceView = new PieceView(piece, color);
            button.add(pieceView);
            
            // Add spacing between buttons
            scrollPanel.add(Box.createVerticalStrut(10));
        }

        // Refresh the UI
        revalidate();
        repaint();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(PIECE_BAG_MINIMUM_WIDTH, PIECE_BAG_MINIMUM_HEIGHT);
    }
}
