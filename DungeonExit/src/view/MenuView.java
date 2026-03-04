package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import listeners.MenuStateListener;
import model.GameModel;
import static utils.Constants.SCREEN_HEIGHT;
import static utils.Constants.SCREEN_WIDTH;
import utils.SoundManager;

/**
 * MenuView is responsible for rendering the main menu, including buttons for navigating
 * through different options (e.g., selecting a level or quitting the game).
 * This class extends StyledPanel to manage graphical rendering and user interactions.
 */
public class MenuView extends JLayeredPane {

    // Game model
    private final GameModel gameModel;
    private MenuStateListener listener; // Reference to listener

    private StyledPanel mainPane;
    
    // Menu UI constants
    private static final int TITLE_SPACING = 60;
    private static final int BUTTON_SPACING = 15;
    private static final int BUTTON_WIDTH = 250;
    private static final int FOOTER_SPACING = 80; // not used for now
    private static final Font GAME_TITLE_FONT = new Font("Arial", Font.BOLD, 42);
    private static final Color SEPARATOR_COLOR = new Color(100, 100, 100);
    
    private TorcheFlameBackground torcheFlameBackground;
	
    // Used sounds
    private static final String BACKGROUND_MUSIC_PATH = "res/sounds/main-menu-music.wav";
    private static final String BUTTON_CLICK_SOUND_PATH = "res/sounds/click-sound.wav";

    /**
     * Constructor for the MenuView class.
     * Initializes the game model to handle menu actions.
     *
     * @param gameModel The game model containing the current game state.
     */
    public MenuView(GameModel gameModel) {
        mainPane = new StyledPanel(40);
        mainPane.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        mainPane.setOpaque(false);
        this.gameModel = gameModel;
        
        // Initialize torches
        torcheFlameBackground = new TorcheFlameBackground();

        // Set layout to BoxLayout with vertical alignment
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        
        // Create a glue for top padding with flexible space
        mainPane.add(Box.createVerticalGlue());
        
        // Add title to the top of the panel
        StyledLabel titleLabel = new StyledLabel("DUNGEON EXIT", GAME_TITLE_FONT);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(220, 220, 220));
        mainPane.add(titleLabel);
        
        StyledLabel subtitleLabel = StyledLabel.createSubtitle("Groupe YJ2");
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        subtitleLabel.setForeground(new Color(180, 180, 180));
        mainPane.add(subtitleLabel);
        
        mainPane.add(Box.createVerticalStrut(TITLE_SPACING));
        
        // Add separator above buttons
        JSeparator topSeparator = createStyledSeparator();
        mainPane.add(topSeparator);
        mainPane.add(Box.createVerticalStrut(TITLE_SPACING / 2));
        
        // Create a container for buttons with fixed width
        StyledPanel buttonPanel = new StyledPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
        buttonPanel.setBackground(new Color(0, 0, 0, 0)); // Make transparent
        buttonPanel.setBorder(null); // Remove border
        buttonPanel.setMaximumSize(new Dimension(BUTTON_WIDTH, 300));

        // Button to access the level selector
        StyledButton levelSelectorButton = createMenuButton("PLAY GAME");
        levelSelectorButton.addActionListener(e -> {
            if(listener != null){
                listener.switchToLevelSelector();
            }
        });
        buttonPanel.add(levelSelectorButton);
        buttonPanel.add(Box.createVerticalStrut(BUTTON_SPACING));

        // Button to access the editor
        StyledButton editorSelectorButton = createMenuButton("LEVEL CREATOR");
        editorSelectorButton.addActionListener(e -> {
            if (listener != null) {
                listener.switchToLevelCreator();
            }
        });
        buttonPanel.add(editorSelectorButton);
        buttonPanel.add(Box.createVerticalStrut(BUTTON_SPACING));

        // Button to read the game rules
        StyledButton gameRulesButton = createMenuButton("GAME RULES");
        gameRulesButton.addActionListener(e -> {
            if (listener != null) {
                listener.switchToGameRules();
            }
        });
        buttonPanel.add(gameRulesButton);
        buttonPanel.add(Box.createVerticalStrut(BUTTON_SPACING));

        // Button to quit the game
        StyledButton quitGameButton = createMenuButton("QUIT GAME");
        quitGameButton.addActionListener(e -> {
            if(listener != null){
                listener.quitGame();
            }
        });
        buttonPanel.add(quitGameButton);
        
        mainPane.add(buttonPanel);
        mainPane.add(Box.createVerticalStrut(TITLE_SPACING / 2));
        
        // Add separator below buttons
        JSeparator bottomSeparator = createStyledSeparator();
        mainPane.add(bottomSeparator);
        
        // Add flexible space
        mainPane.add(Box.createVerticalGlue());
        
        // Add copyright/version at the bottom
        StyledLabel versionLabel = new StyledLabel("Dungeon Exit v1.0", StyledLabel.SMALL_FONT);
        versionLabel.setForeground(new Color(150, 150, 150));
        versionLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainPane.add(versionLabel);
        
        mainPane.add(Box.createVerticalStrut(15));
        

        add(torcheFlameBackground);
        setLayer(torcheFlameBackground, 0);

        add(mainPane);
        setLayer(mainPane, 1);

        // Initialize sound
        initializeSound();
    }


    public void displayRules() {
        // Hide this panel
        setVisible(false);
        
        GameRulesView gameRulesView = new GameRulesView(() -> {
            // Return to menu callback
            if (getParent() != null) {
                // Remove rules panel and show menu again
                getParent().remove(getParent().getComponentCount() - 1);
                setVisible(true);
                getParent().revalidate();
                getParent().repaint();
            }
        });
        if (getParent() != null) {
            getParent().add(gameRulesView);
            getParent().revalidate();
            getParent().repaint();
        }
    }



    
    /**
     * Called when the view becomes visible
     */
    @Override
    public void addNotify() {
        super.addNotify();
        startMenuMusic();
    }
    
    /**
     * Initializes the animation elements and starts the animation timer
     */

    
    /**
     * Override paintComponent to draw our animated background with torches
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);   
        // Draw all torches
        torcheFlameBackground.repaint();
        g2d.dispose();
    }
    
   
    
    /**
     * Creates a standardized menu button with consistent styling
     * 
     * @param text The button text
     * @return A styled button configured for the main menu
     */
    private StyledButton createMenuButton(String text) {
        StyledButton button = new StyledButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(BUTTON_WIDTH, 50));
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setDrawBorder(false); // Disable the border for a cleaner look
        
        // Add sound effect to button click
        button.addActionListener(e -> {
            SoundManager.getInstance().playSoundEffect("button_click");
        });
        
        return button;
    }
    
    /**
     * Creates a styled separator line for visual grouping
     * 
     * @return A configured JSeparator
     */
    private JSeparator createStyledSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(BUTTON_WIDTH + 40, 1));
        separator.setForeground(SEPARATOR_COLOR);
        separator.setAlignmentX(CENTER_ALIGNMENT);
        return separator;
    }

    /**
     * Sets the menu state listener for handling menu actions.
     * 
     * @param listener The listener for menu state changes.
     */
    public void setMenuStateListener(MenuStateListener listener){
        this.listener = listener;
    }

    private void initializeSound() {
        // Load and play background music
        SoundManager.getInstance().playBackgroundMusic(BACKGROUND_MUSIC_PATH);
        
        // Load sound effects
        SoundManager.getInstance().loadSoundEffect("button_click", BUTTON_CLICK_SOUND_PATH);
    }

    /**
     * Starts the menu background music
     */
    private void startMenuMusic() {
        SoundManager.getInstance().playBackgroundMusic(BACKGROUND_MUSIC_PATH);
    }
}
