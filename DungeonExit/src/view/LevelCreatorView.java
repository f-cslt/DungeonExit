package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import listeners.LevelCreatorListener;
import model.GameModel;
import model.LevelCreator;
import model.LevelCreator.CreatorPieceBag;
import model.Piece;
import model.PieceBag;
import model.Tile;
import model.Tile.TileType;
import static utils.Constants.SCREEN_HEIGHT;
import static utils.Constants.SCREEN_WIDTH;
import utils.SoundManager;

public class LevelCreatorView extends StyledPanel {

    private final GameModel gameModel;
    private final LevelCreator levelCreator;
    private LevelCreatorListener listener;

    private final GridBagLayout layout;
    private final GridBagConstraints gbc;

    private final StyledButton backButton;
    private final StyledButton addButton;
    private final StyledButton removeButton;
    private final StyledButton upButton;
    private final StyledButton downButton;
    private final StyledButton selectPieceButton;
    private final StyledButton saveButton;
    private final StyledButton checkWinButton;
    private final StyledButton solveButton;
    private final StyledButton cleanButton;

    // Custom dialog components
    private JDialog nameDialog;
    private StyledTextField nameTextField;

    private final JPanel cpScrollPanel;
    private final JScrollPane cpScrollPane;

    // LevelViews
    private final List<BoardView> boardView;
    private final PieceBagView pieceBagView;
    private final CreatorPieceBagView fullPieceBagView;

    private final Color color;
    private static final int BORDER_THICKNESS = 3;
    private static final int PADDING = 10;
    private static final Color BORDER_COLOR = new Color(150, 80, 150); // Darker pink/purple


    public LevelCreatorView(GameModel gameModel) {
        super(0);

        // Instanciate fields
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();
        this.color = new Color(21,151,209);
        this.cpScrollPanel = new JPanel();
        this.cpScrollPane = new JScrollPane(cpScrollPanel);

        this.gameModel = gameModel;
        this.levelCreator = gameModel.getLevelCreator();
        this.boardView = new ArrayList<>();
        this.pieceBagView = new PieceBagView(levelCreator.getPieceBag(), color);
        this.fullPieceBagView = new CreatorPieceBagView(levelCreator.getFullPieceBag(), color);
        
        this.setLayout(layout);
        setPreferredSize(new Dimension(800, 600));
        this.setFocusable(false);

        // Instanciate Buttons
        this.backButton = new StyledButton("BACK");
        this.addButton = new StyledButton("ADD");
        this.removeButton = new StyledButton("REMOVE");
        this.upButton = new StyledButton("UP");
        this.downButton = new StyledButton("DOWN");
        this.selectPieceButton = new StyledButton("SELECT");
        this.saveButton = new StyledButton("SAVE");
        this.checkWinButton = new StyledButton("DONE");
        this.solveButton = new StyledButton("SOLVE");
        this.cleanButton = new StyledButton("CLEAN");

        // Init Methods
        initActionListeners();
        initCpScrollPanel();
        initCpScrollPane();
        initPanelCp();
        initLevelViews();
        initAddObjects();
    }

    public void setLevelCreatorListener(LevelCreatorListener listener) {
        this.listener = listener;
    }

    private void handleButtonClick(Runnable action) {
        SoundManager.getInstance().playSoundEffect("button_click");
        action.run();
    }

    public void animPlay() {
        checkWinButton.setEnabled(false);
        backButton.setEnabled(false);
        cleanButton.setEnabled(false);
        solveButton.setEnabled(false);
        saveButton.setEnabled(false);
    }

    public void animFinish() {
        checkWinButton.setEnabled(true);
        backButton.setEnabled(true);
        cleanButton.setEnabled(true);
        solveButton.setEnabled(true);
        saveButton.setEnabled(true);
    }

    /***************************************
     *                                     *
     *          INIT METHODS               *
     *                                     *
     ***************************************/

    private void initActionListeners(){
        backButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.switchToMenu();
            }
        }));
        backButton.setFocusable(false);

        addButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.addBoard();
            }
        }));
        addButton.setFocusable(false);

        removeButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.removeBoard();
            }
        }));
        removeButton.setFocusable(false);

        upButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.changeLevel(levelCreator.getCurrentBoard() + 1);
            }
        }));
        upButton.setFocusable(false);

        downButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.changeLevel(levelCreator.getCurrentBoard() - 1);
            }
        }));
        downButton.setFocusable(false);

        selectPieceButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.openFullPieceBag();
            }
        }));
        selectPieceButton.setFocusable(false);

        saveButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.save();
            }
        }));
        saveButton.setFocusable(false);

        checkWinButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.checkWin();
            }
        }));
        checkWinButton.setFocusable(false);
        
        cleanButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.clean();
            }
        }));
        cleanButton.setFocusable(false);
        
        solveButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.solve();
            }
        }));
        solveButton.setFocusable(false);

    }

    /**
     * Shows a dialog to enter level name before saving
     */
    public void showLevelNameDialog() {
        // Create a custom dialog
        nameDialog = new JDialog();
        nameDialog.setTitle("Level Name");
        nameDialog.setModal(true);
        nameDialog.setSize(450, 300);
        
        // Set dialog appearance
        nameDialog.setUndecorated(true); // Remove default title bar
        
        // Calculate position to center on parent
        int x = getLocationOnScreen().x + (getWidth() - 450) / 2;
        int y = getLocationOnScreen().y + (getHeight() - 300) / 2;
        nameDialog.setLocation(x, y);
        
        // Create content panel with styling
        StyledPanel contentPanel = new StyledPanel(20, BORDER_COLOR);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(40, 40, 40));
        
        // Create title bar with custom styling
        JPanel titleBar = new JPanel();
        titleBar.setLayout(new BoxLayout(titleBar, BoxLayout.X_AXIS));
        titleBar.setBackground(BORDER_COLOR);
        titleBar.setMaximumSize(new Dimension(450, 40));
        titleBar.setPreferredSize(new Dimension(450, 40));
        
        // Title label
        StyledLabel titleLabel = new StyledLabel("NAME YOUR LEVEL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        // Add title to title bar
        titleBar.add(Box.createHorizontalStrut(15));
        titleBar.add(titleLabel);
        titleBar.add(Box.createHorizontalGlue());
        
        // Close button for title bar
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(BORDER_COLOR);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                nameDialog.dispose();
                listener.changeMusicVictoryToBasic();
            }
        }));
            
        
        
        titleBar.add(closeButton);
        
        // Description
        StyledLabel descLabel = new StyledLabel("Enter a name for your winning level:");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descLabel.setForeground(new Color(220, 220, 220));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Text field for name input
        nameTextField = new StyledTextField("", 20);
        nameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameTextField.setMaximumSize(new Dimension(350, 40));
        nameTextField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameTextField.setFocusable(true);
        
        // Add key listener to handle Enter key
        nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    String levelName = nameTextField.getText().trim();
                    if (!levelName.isEmpty()) {
                        nameDialog.dispose();
                        saveLevel(levelName);
                    } else {
                        // Show warning for empty name
                        JOptionPane.showMessageDialog(nameDialog, 
                            "Please enter a valid name for your level.", 
                            "Empty Name", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    nameDialog.dispose();
                }
            }
        });
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(350, 50));
        
        // Styled buttons
        StyledButton saveButtonBis = new StyledButton("SAVE");
        saveButtonBis.setPreferredSize(new Dimension(120, 40));
        
        StyledButton cancelButton = new StyledButton("CANCEL");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        
        // Add action listeners
        saveButtonBis.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                String levelName = nameTextField.getText().trim();
                if (!levelName.isEmpty()) {
                    nameDialog.dispose();
                    saveLevel(levelName);
                } else {
                    // Show warning for empty name
                    JOptionPane.showMessageDialog(nameDialog, 
                        "Please enter a valid name for your level.", 
                        "Empty Name", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }));
        cancelButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                nameDialog.dispose();
                listener.changeMusicVictoryToBasic();
            }
        }));
        
        // Add components to button panel
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(saveButtonBis);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        // Add all components to main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        
        // Add components to the content panel
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(nameTextField);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Add components to main panel
        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Set dialog content and display
        nameDialog.setContentPane(mainPanel);
        nameDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        nameDialog.setVisible(true);
        
        // Request focus on text field
        nameTextField.requestFocusInWindow();
    }

    /**
     * Saves the level with the specified name
     */
    private void saveLevel(String levelName) {
        if (listener != null) {
            listener.saveWithName(levelName);
        }
    }

    private void initAddObjects(){
        removeAll();

        addobjects(this, boardView.getFirst(), layout, gbc, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
        addobjects(this, pieceBagView, layout, gbc, 1, 1, 1, 4, GridBagConstraints.LINE_START, 10);
        addobjects(this, cpScrollPane, layout, gbc, 6, 1, 1, 4, GridBagConstraints.LINE_END, 10);
        
        addobjects(this, backButton, layout, gbc, 0, 0, 1, 1, GridBagConstraints.PAGE_START, 0);
        addobjects(this, saveButton, layout, gbc, 6, 0, 1, 1, GridBagConstraints.LINE_END, 0);
        addobjects(this, solveButton, layout, gbc, 5, 0, 1, 1, GridBagConstraints.LINE_END, 0);
        addobjects(this, checkWinButton, layout, gbc, 4, 0, 1, 1, GridBagConstraints.LINE_END, 0);
        addobjects(this, cleanButton, layout, gbc, 3, 0, 1, 1, GridBagConstraints.CENTER, 0);
        
        addobjects(this, selectPieceButton, layout, gbc, 1, 5, 1, 1, GridBagConstraints.LINE_START, 10);
        addobjects(this, upButton, layout, gbc, 4, 5, 1, 1, GridBagConstraints.CENTER, 10);
        addobjects(this, downButton, layout, gbc, 5, 5, 1, 1, GridBagConstraints.CENTER,10);
        addobjects(this, addButton, layout, gbc, 2, 5, 1, 1, GridBagConstraints.CENTER, 10);
        addobjects(this, removeButton, layout, gbc, 3, 5, 1, 1, GridBagConstraints.CENTER, 10);
       

        revalidate();
    }

    private void initLevelViews() {
        for (int i = 0; i < gameModel.getLevelCreator().getNbBoard(); i++) {
            this.boardView.add(new BoardView(levelCreator.getBoard(i), color));
        }     
    }

    private void initCpScrollPanel() {
        cpScrollPanel.setLayout(new BoxLayout(cpScrollPanel, BoxLayout.Y_AXIS));
        cpScrollPanel.setBackground(new Color(30, 30, 30)); // Darker background for contrast
        cpScrollPanel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING)); // Set a styled border with padding
        cpScrollPanel.setBorder(new CompoundBorder(
            new LineBorder(color, BORDER_THICKNESS, true), // Rounded corners
            new EmptyBorder(PADDING, PADDING, PADDING, PADDING))); // Inner padding
    }

    private void initCpScrollPane() {
        Dimension scrollPaneSize = new Dimension(SCREEN_WIDTH / 6, SCREEN_HEIGHT - SCREEN_HEIGHT / 3);
        cpScrollPane.setPreferredSize(scrollPaneSize);
        cpScrollPane.setMaximumSize(scrollPaneSize);
        cpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cpScrollPane.setBorder(new LineBorder(color, 1));
        cpScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100);
                this.trackColor = new Color(50, 50, 50);
            }
        });
    }

    private void initPanelCp() {
        Tile.TileType cps[] = {Tile.TileType.ENTRY, Tile.TileType.KEY, Tile.TileType.CHEST, Tile.TileType.DRAGON, Tile.TileType.EXIT, Tile.TileType.STAIRS};

        for (TileType cp : cps) {
            cpScrollPanel.add(Box.createVerticalStrut(10));

            JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 40));
            panel.setMaximumSize(new Dimension(180, 80));
            panel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cpScrollPanel.add(panel);

            JButton button = new JButton();
            button.setFocusable(false);
            button.setContentAreaFilled(false);
            button.setBorder(new LineBorder(new Color(0, 0, 0, 0), 2, true));
            button.setBackground(new Color(60, 60, 60));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setPreferredSize(new Dimension(100, 70));
            button.addActionListener(e -> {
                if (listener != null) {
                    listener.checkpointClicked(cp);
                }
            });
            panel.add(button);

            JPanel cpView = new CheckPointView(cp, color);
            button.add(cpView);
        }
        cpScrollPanel.add(Box.createVerticalStrut(10));
        revalidate();
        repaint();
    }   


    

    /***************************************
     *                                     *
     * ADD BOARD / REMOVE BOARD METHODS    *
     *                                     *
     ***************************************/

    public void addBoardView() {
        if (boardView.isEmpty()) {
            this.boardView.add(new BoardView(levelCreator.getBoard().getLast(), color));
            addobjects(this, boardView.get(levelCreator.getCurrentBoard()), layout, gbc, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
            revalidate();
            requestFocusInWindow();
        }
        this.boardView.add(new BoardView(levelCreator.getBoard().getLast(), color));
    }

    public void removeBoardView() {
        // remove last
        remove(boardView.getLast());
        this.boardView.removeLast();
    }

    public void addCurrentBoardView() {
        addobjects(this, boardView.get(levelCreator.getCurrentBoard()), layout, gbc, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
        revalidate();
        requestFocusInWindow();
    }

    public void removeCurrentBoardView(){
        remove(boardView.get(levelCreator.getCurrentBoard()));
    }

    public void changeLevelView() {
        addobjects(this, boardView.get(levelCreator.getCurrentBoard()), layout, gbc, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
        revalidate();
        requestFocusInWindow();
    }


    /***************************************
     *                                     *
     *      DISPLAY METHODS / PAINT ETC    *
     *                                     *
     ***************************************/

    public void displayFullPieceBag(PieceBag fullPieceBag) {
        // Remove the regular piece bag from view
        remove(pieceBagView);
        // Add full piece bag to the view
        addobjects(this, fullPieceBagView, layout, gbc, 1, 1, 1, 4, GridBagConstraints.LINE_START, 10);
        // Update select button text
        selectPieceButton.setText("BACK TO BAG");
        revalidate();
    }
    
    public void displayRegularPieceBag() {
        // Remove the full piece bag from view if it exists
        remove(fullPieceBagView);    
        // Add regular piece bag back to the view
        addobjects(this, pieceBagView, layout, gbc, 1, 1, 1, 4, GridBagConstraints.LINE_START, 10);
        // Reset select button text
        selectPieceButton.setText("SELECT");
        revalidate();
    }

    @Override
    public void paintComponent(Graphics g) {
        Toolkit.getDefaultToolkit().sync(); // Helps to prevent rendering issues
        super.paintComponent(g);
        
        boardView.get(levelCreator.getCurrentBoard()).repaint(); // Ensure the board view is repainted
        
    }


    public void addobjects(Container c, Component componente, GridBagLayout layout,
        GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int insets){

        gbc.gridx = gridx;
        gbc.gridy = gridy;

        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;

        gbc.anchor = anchor;
        gbc.insets = new Insets(insets, insets, insets, insets);

        layout.setConstraints(componente, gbc);
        c.add(componente);
    }



    /***************************************
     *                                     *
     *      GETTERS / SETTERS              *
     *                                     *
     ***************************************/

    public List<BoardView> getBoardView() {
        return boardView;
    }

    public PieceBagView getPieceBagView() {
        return pieceBagView;
    }

    public CreatorPieceBagView getFullPieceBagView() {
        return fullPieceBagView;
    }



    /***************************************
     *                                     *
     *        VERY USEFUL CLASSES          *
     *                                     *
     ***************************************/



    /**
     * A specialized PieceBagView that allows pieces to be selected with a visual outline
     */
    public class CreatorPieceBagView extends PieceBagView {

        private final Color SELECTION_COLOR;
        private CreatorPieceBag creatorPieceBag;

        public CreatorPieceBagView(PieceBag pieceBag, Color color) {
            super(pieceBag, color);
            this.SELECTION_COLOR = new Color(230, 126, 34);
            if (pieceBag instanceof CreatorPieceBag creatorPB){
                this.creatorPieceBag = creatorPB;
            } else {
                throw new IllegalArgumentException("CreatorPieceBagView requires a CreatorPieceBag");
            }
        }
       
        @Override
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

                if (creatorPieceBag != null) {
                    if (creatorPieceBag.getSelectedPieces().contains(piece)) {
                        panel.setBorder(BorderFactory.createLineBorder(color, 3));
                    } else {
                        panel.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
                
                button.addActionListener(e -> {
                    listener.handlePieceSelection(piece);
                }); // Selects the clicked piece

                
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
    }


    public class CheckPointView extends JPanel {
        private Tile.TileType checkpoint;
        private Color color;

        private int frameTime = 0;
        private int frameIndex = 0;
        private int frameSpeed = 50;
        private final BufferedImage dragonImage[];


        public CheckPointView(Tile.TileType checkpoint, Color color) {
            this.checkpoint = checkpoint;
            this.color = color;
            setOpaque(false);

            this.dragonImage = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                try {
                    dragonImage[i] = ImageIO.read(getClass().getResource("/tiles/new/dragon.png")).getSubimage(i * 128, 0, 128, 128);
                } catch (IOException e) {}
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Toolkit.getDefaultToolkit().sync(); // Helps with rendering performance
            super.paintComponent(g);

            if (checkpoint == TileType.DRAGON) {
                g.drawImage(
                    getDragonImage(),
                    0,
                    0,
                    50,
                    50,
                    null
                );
            }else {
                g.drawImage(
                checkpoint.image, 
                0,
                0,
                50,
                50,
                null
                );
            }
           
        }

        private BufferedImage getDragonImage() {
            frameTime++;
            if (frameTime >= frameSpeed) {
                frameIndex++;
                frameTime = 0;
            }
            if (frameIndex >= 4) {
                frameIndex = 0;
            }
            return dragonImage[frameIndex];
        }
    }
}
