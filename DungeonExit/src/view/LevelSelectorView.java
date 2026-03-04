package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import listeners.LevelSelectorListener;
import model.GameModel;
import static utils.Constants.*;
import utils.SoundManager;

/**
 * The LevelSelectorView class represents the level selection screen.
 * It displays a list of buttons for selecting different levels.
 */
public class LevelSelectorView extends JLayeredPane {

    /**
     * Constructs the LevelSelectorView.
     * Sets up the layout, styles, and buttons for selecting levels.
     */

    private final JScrollPane jScrollPane;   
    private final JPanel levelSelector;
    private StyledPanel surePane;
    private StyledPanel mainPane;
    
    // Fixed widths for consistent layout
    private static final int BUTTON_WIDTH = 200;
    private static final int HIGHSCORE_LABEL_WIDTH = 150;
    private static final int ICON_SIZE = SCREEN_WIDTH / 25;
    private static final int PANEL_PADDING = 15;

    private ModeSelectionView modeSelectionPane;

    private String mode;
    // Used sounds
    private static final String BUTTON_CLICK_SOUND_PATH = "res/sounds/click-sound.wav";

    private LevelSelectorListener listener;

    public LevelSelectorView(GameModel gameModel) {

        modeSelectionPane = new ModeSelectionView(this);

        modeSelectionPane.getBackButton().addActionListener(e -> {
            if (listener != null) {
                listener.switchToMenu();
            }
        });

        // Initialize the main panel with StyledPanel
        mainPane = new StyledPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        
        // Initialize the level selector panel with dark styling
        levelSelector = new JPanel();
        levelSelector.setLayout(new BoxLayout(levelSelector, BoxLayout.Y_AXIS));
        levelSelector.setBackground(StyledPanel.DARKER_BACKGROUND);
        levelSelector.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));

        // Create a styled scroll pane
        jScrollPane = new StyledScrollPane(
            levelSelector, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        jScrollPane.setPreferredSize(new Dimension(SCREEN_WIDTH - 100, SCREEN_HEIGHT - 200));
        
        putAllPane();
        createSurePane();
        
        // Initialize sound
        initializeSound();
        
        // Generate level selection buttons
        /*try {
            updatePane();
        } catch (FileNotFoundException e) {
            // Handle error silently
        }*/
        mainPane.add(jScrollPane, BorderLayout.CENTER);
        switchToModeSelection();
    }

    public void switchToModeSelection() {
        removeAll();
        add(modeSelectionPane);
        setLayer(modeSelectionPane, 1);
        revalidate();
        requestFocusInWindow();
    }

    public void switchToLevelSelection() {
        removeAll();
        add(mainPane);
        setLayer(mainPane, 1);
        try {
            updatePane();
        } catch (FileNotFoundException e) {
        }
        revalidate();
        requestFocusInWindow();
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setLevelSelectionListener(LevelSelectorListener listener) {
        this.listener = listener;
    }

    private void initializeSound() {
        // Load sound effects
        SoundManager.getInstance().loadSoundEffect("button_click", BUTTON_CLICK_SOUND_PATH);
        SoundManager.getInstance().loadSoundEffect("piece_placement", BUTTON_CLICK_SOUND_PATH);
    }

    /**
     * Helper method to handle button clicks with sound
     */
    private void handleButtonClick(Runnable action) {
        SoundManager.getInstance().playSoundEffect("button_click");
        action.run();
    }

    public void updatePane() throws FileNotFoundException {
        File listFile[] = new File("res/maps/").listFiles();
        if (listFile != null) {
            Arrays.sort(listFile, Comparator.comparing(File::getName));
        }

        levelSelector.removeAll();
        levelSelector.setPreferredSize(new Dimension(SCREEN_WIDTH, listFile.length * SCREEN_HEIGHT / 5));
        
        for (File file : listFile) {
            String[] nameSplit = file.getName().split("\\.");
            if (!nameSplit[1].equals(mode)) {
                continue ;
            }
            // Create a styled panel for each level
            StyledPanel cartPanel = new StyledPanel(new BorderLayout(), 10);
            cartPanel.setMaximumSize(new Dimension(SCREEN_WIDTH - 150, 100));
            cartPanel.setPreferredSize(new Dimension(SCREEN_WIDTH - 150, 80));
            
            // Create a panel for the button and info with fixed layout
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
            infoPanel.setBackground(StyledPanel.DARK_BACKGROUND);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Button container panel for centering
            JPanel buttonContainer = new JPanel();
            buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
            buttonContainer.setBackground(StyledPanel.DARK_BACKGROUND);
            buttonContainer.setPreferredSize(new Dimension(BUTTON_WIDTH, 45));
            buttonContainer.setMaximumSize(new Dimension(BUTTON_WIDTH, 45));
            buttonContainer.setMinimumSize(new Dimension(BUTTON_WIDTH, 45));
            
            // Create the level select button
            String levelName = file.getName().substring(0, (int)file.getName().length() - (mode.length() + 1));
            StyledButton button = new StyledButton(levelName);
            button.setAlignmentX(CENTER_ALIGNMENT);
            button.addActionListener(e -> handleButtonClick(() -> {
                if(listener != null){
                    listener.launchLevel(file);
                }
            }));
            
            // Add button to container with centering
            buttonContainer.add(Box.createVerticalGlue());
            buttonContainer.add(button);
            buttonContainer.add(Box.createVerticalGlue());

            // Parse level information
            Scanner fileScanner = new Scanner(file);
            String[] firstLine = {};
            String[] secondLine = {};
            
            if (fileScanner.hasNextLine()) {
                firstLine = fileScanner.nextLine().split(":");
                if (fileScanner.hasNextLine()) {
                    secondLine = fileScanner.nextLine().split(":");
                }
            }
            
            // Fixed-width panel for highscore
            JPanel highscorePanel = new JPanel();
            highscorePanel.setLayout(new BoxLayout(highscorePanel, BoxLayout.Y_AXIS));
            highscorePanel.setBackground(StyledPanel.DARK_BACKGROUND);
            highscorePanel.setPreferredSize(new Dimension(HIGHSCORE_LABEL_WIDTH, 70));
            highscorePanel.setMaximumSize(new Dimension(HIGHSCORE_LABEL_WIDTH, 70));
            highscorePanel.setMinimumSize(new Dimension(HIGHSCORE_LABEL_WIDTH, 70));

            // Display highscore
            int high = firstLine.length > 1 ? Integer.parseInt(firstLine[1]) : 0;
            StyledLabel highField = new StyledLabel("Highscore: " + ((high == 0) ? "NONE" : high));
            highField.setAlignmentX(LEFT_ALIGNMENT);
            
            // Add highscore label to panel with vertical centering
            highscorePanel.add(Box.createVerticalGlue());
            highscorePanel.add(highField);
            highscorePanel.add(Box.createVerticalGlue());
            
            // Display completion status in its own panel
            JPanel iconPanel = new JPanel();
            iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.Y_AXIS));
            iconPanel.setBackground(StyledPanel.DARK_BACKGROUND);
            
            boolean completed = secondLine.length > 1 && secondLine[1].equals("true");
            ImageIcon i1 = new ImageIcon(completed ? "res/ui/greendone.png" : "res/ui/redcross.png");
            Image i2 = i1.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_DEFAULT);
            JLabel doneLabel = new JLabel(new ImageIcon(i2));
            doneLabel.setAlignmentX(CENTER_ALIGNMENT);
            
            // Center icon vertically
            iconPanel.add(Box.createVerticalGlue());
            iconPanel.add(doneLabel);
            iconPanel.add(Box.createVerticalGlue());

            // Create delete button container for centering
            JPanel deleteButtonPanel = new JPanel();
            deleteButtonPanel.setLayout(new BoxLayout(deleteButtonPanel, BoxLayout.Y_AXIS));
            deleteButtonPanel.setBackground(StyledPanel.DARK_BACKGROUND);
            
            StyledButton deleteButton = new StyledButton("Delete");
            deleteButton.setAlignmentX(CENTER_ALIGNMENT);
            deleteButton.addActionListener(e -> handleButtonClick(() -> putPaneDeleteFile(file)));
            
            // Center delete button vertically
            deleteButtonPanel.add(Box.createVerticalGlue());
            deleteButtonPanel.add(deleteButton);
            deleteButtonPanel.add(Box.createVerticalGlue());

            // Add all components to the panel
            infoPanel.add(buttonContainer);
            infoPanel.add(Box.createHorizontalStrut(20));
            infoPanel.add(highscorePanel);
            infoPanel.add(Box.createHorizontalStrut(10));
            infoPanel.add(iconPanel);
            infoPanel.add(Box.createHorizontalGlue());
            infoPanel.add(deleteButtonPanel);

            cartPanel.add(infoPanel, BorderLayout.CENTER);
            
            levelSelector.add(cartPanel);
            levelSelector.add(Box.createVerticalStrut(15)); // Adds spacing between buttons
            
            fileScanner.close();
        }
    }

    private void createSurePane(){
        surePane = new StyledPanel(15, new Color(150, 50, 50));
        surePane.setSize(new Dimension(SCREEN_WIDTH / 3, SCREEN_HEIGHT / 4));
        surePane.setLayout(new BoxLayout(surePane, BoxLayout.Y_AXIS));
        surePane.setLocation(SCREEN_WIDTH / 2 - SCREEN_WIDTH / 6, SCREEN_HEIGHT / 2 - SCREEN_HEIGHT / 8);
    }

    private void putPaneDeleteFile(File file){
        surePane.removeAll();
        
        // Add confirmation title
        StyledLabel text = StyledLabel.createTitle("Delete Level?");
        text.setAlignmentX(CENTER_ALIGNMENT);
        
        // Add message
        StyledLabel message = new StyledLabel("Are you sure you want to delete " + file.getName() + "?");
        message.setAlignmentX(CENTER_ALIGNMENT);
        
        // Add buttons in a panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(StyledPanel.DARK_BACKGROUND);
        
        StyledButton yesButton = new StyledButton("YES");
        yesButton.addActionListener(e -> handleButtonClick(() -> deleteFile(file)));
        
        StyledButton noButton = new StyledButton("NO");
        noButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null){
                listener.removeSurePane();
            }
        }));

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(yesButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(noButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        surePane.add(text);
        surePane.add(Box.createVerticalStrut(10));
        surePane.add(message);
        surePane.add(Box.createVerticalStrut(20));
        surePane.add(buttonPanel);
        
        if(listener != null){
            listener.putSurePane();
        }
    }

    private void deleteFile(File file){
        file.delete();
        if(listener != null)
            listener.removeSurePane();
    }

    public void putAllPane(){
        // Create back button
        StyledButton backButton = new StyledButton("BACK");
        backButton.addActionListener(e -> {
            switchToModeSelection();
        });
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        
        // Create title label
        StyledLabel titleLabel = StyledLabel.createTitle("SELECT LEVEL");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        // Add components to main panel
        mainPane.add(backButton);
        mainPane.add(Box.createVerticalStrut(10));
        mainPane.add(titleLabel);
        mainPane.add(Box.createVerticalStrut(20));

        add(mainPane);
    }

    public JPanel getSurePane(){
        return surePane;
    }
}
