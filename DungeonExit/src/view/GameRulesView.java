package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import utils.SoundManager;

/**
 * GameRulesView displays the game rules in a scrollable panel.
 * This class extends StyledPanel to maintain consistent styling with the rest of the game.
 */
public class GameRulesView extends StyledPanel {

    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font SECTION_FONT = new Font("Arial", Font.BOLD, 26);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final int SECTION_SPACING = 25;
    private static final int PARAGRAPH_SPACING = 15;
    private static final String BUTTON_CLICK_SOUND_PATH = "res/sounds/click-sound.wav";

    private Runnable returnToMenuCallback;

    /**
     * Constructor for the GameRulesView.
     * 
     * @param returnToMenuCallback Callback to return to the main menu
     */
    public GameRulesView(Runnable returnToMenuCallback) {
        super(40);
        this.returnToMenuCallback = returnToMenuCallback;
        
        setLayout(new BorderLayout());
        
        // Create the header panel with title and back button
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create scrollable content panel
        JScrollPane scrollPane = createScrollableContentPanel();
        add(scrollPane, BorderLayout.CENTER);
        
        // Load the button click sound
        SoundManager.getInstance().loadSoundEffect("button_click", BUTTON_CLICK_SOUND_PATH);
    }
    
    /**
     * Creates the header panel with title and back button
     * 
     * @return The configured header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new StyledPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Create title label
        StyledLabel titleLabel = new StyledLabel("GAME RULES", TITLE_FONT, new Color(220,200,150));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create back button
        StyledButton backButton = new StyledButton("BACK");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.setDrawBorder(false);
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(e -> {
            SoundManager.getInstance().playSoundEffect("button_click");
            if (returnToMenuCallback != null) {
                returnToMenuCallback.run();
            }
        });
        
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setOpaque(false);
        backButtonPanel.add(backButton);
        headerPanel.add(backButtonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Adds a section to the content panel with a title, text, and optionally multiple images
     * 
     * @param panel The panel to add the section to
     * @param title The section title
     * @param content The section content text
     * @param imagePaths Optional list of paths to images to display (can be null)
     */
    private void addSection(JPanel panel, String title, String content, String... imagePaths) {
        // Create main section panel with horizontal layout
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.X_AXIS));
        sectionPanel.setOpaque(false);
        sectionPanel.setAlignmentX(LEFT_ALIGNMENT);

        // Create left panel for text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setAlignmentX(LEFT_ALIGNMENT);

        // Add section title
        StyledLabel titleLabel = new StyledLabel(title, SECTION_FONT);
        titleLabel.setForeground(new Color(220, 200, 150));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(PARAGRAPH_SPACING));

        // Add section content with proper indentation for bullet points
        String[] paragraphs = content.split("\n");
        for (String paragraph : paragraphs) {
            if (paragraph.trim().startsWith("-")) {
                // Create a panel for indented bullet points
                JPanel bulletPanel = new JPanel();
                bulletPanel.setLayout(new BoxLayout(bulletPanel, BoxLayout.X_AXIS));
                bulletPanel.setOpaque(false);
                bulletPanel.setAlignmentX(LEFT_ALIGNMENT);
                
                // Add indentation
                bulletPanel.add(Box.createHorizontalStrut(20));
                
                // Add bullet point text
                StyledLabel contentLabel = new StyledLabel(paragraph, CONTENT_FONT);
                contentLabel.setForeground(new Color(200, 200, 200));
                bulletPanel.add(contentLabel);
                
                textPanel.add(bulletPanel);
            } else {
                StyledLabel contentLabel = new StyledLabel(paragraph, CONTENT_FONT);
                contentLabel.setForeground(new Color(200, 200, 200));
                contentLabel.setAlignmentX(LEFT_ALIGNMENT);
                textPanel.add(contentLabel);
            }
            textPanel.add(Box.createVerticalStrut(PARAGRAPH_SPACING));
        }

        // Add text panel to section panel
        sectionPanel.add(textPanel);

        // Add images if provided
        if (imagePaths != null && imagePaths.length > 0) {
            // Create a panel for images
            JPanel imagesPanel = new JPanel();
            imagesPanel.setLayout(new BoxLayout(imagesPanel, BoxLayout.Y_AXIS));
            imagesPanel.setOpaque(false);
            imagesPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

            for (String imagePath : imagePaths) {
                try {
                    Image image = ImageIO.read(new File(imagePath));
                    // Scale image to a reasonable size (e.g., 150x150)
                    Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                    imageLabel.setAlignmentX(LEFT_ALIGNMENT);
                    imagesPanel.add(imageLabel);
                    imagesPanel.add(Box.createVerticalStrut(10)); // Add spacing between images
                } catch (Exception e) {
                    System.err.println("Error loading image: " + imagePath);
                    e.printStackTrace();
                }
            }
            sectionPanel.add(imagesPanel);
        }

        // Add the section panel to the main panel
        panel.add(sectionPanel);
        panel.add(Box.createVerticalStrut(SECTION_SPACING));
    }

    private void addKeyPointsWithImages(JPanel panel, String[] keyPoints, String[] imagePaths) {
        for (int i = 0; i < keyPoints.length; i++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);

            // Indent
            row.add(Box.createHorizontalStrut(20));

            // Key point label
            StyledLabel label = new StyledLabel("- " + keyPoints[i], CONTENT_FONT);
            label.setForeground(new Color(200, 200, 200));
            row.add(label);

            // Space between text and image
            row.add(Box.createHorizontalStrut(10));

            // Image
            try {
                Image image = ImageIO.read(new File(imagePaths[i]));
                Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                row.add(imageLabel);
            } catch (Exception e) {
                // If image fails, just skip it
            }

            panel.add(row);
            panel.add(Box.createVerticalStrut(PARAGRAPH_SPACING));
        }
    }

    /**
     * Creates the scrollable content panel with game rules
     * 
     * @return A JScrollPane containing the rules content
     */
    private JScrollPane createScrollableContentPanel() {
        // Create content panel to hold the rules
        JPanel contentPanel = new StyledPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        
        // Add Introduction section
        addSection(contentPanel, "Introduction", 
            "Welcome to DungeonExit, a puzzle adventure where every piece you place can mean the difference between freedom and being dragon food.\n"+
            "This section covers how to play, the rules, and what to expect across different modes.");
        
        // Add Objective section
        addSection(contentPanel, "Objective", 
            "Whether in single-player or versus mode, your ultimate goal is the same:\n" +
            "Build a continuous path using your pieces that connects the following checkpoints in the correct order:\n"
        );

        JPanel keyPointsPanel = new JPanel();
        keyPointsPanel.setLayout(new BoxLayout(keyPointsPanel, BoxLayout.Y_AXIS));
        keyPointsPanel.setOpaque(false);
        keyPointsPanel.setAlignmentX(LEFT_ALIGNMENT);

        String[] keyPoints = {"Entry", "Key", "Chest", "Dragon", "Exit"};
        String[] keyImages = {
            "res/rules/entry.png",
            "res/rules/key.png",
            "res/rules/chest.png",
            "res/rules/dragon.png",
            "res/rules/exit.png"
        };
        addKeyPointsWithImages(keyPointsPanel, keyPoints, keyImages);
        contentPanel.add(keyPointsPanel);

        addSection(contentPanel, "", "Along the way, you'll encounter multi-storey dungeons, tricky staircases, and even magical bridges. Let the puzzle begin!");
        
        // Add Game Controls section
        addSection(contentPanel, "Game Controls", 
            "- Place Pieces: Left click on a piece from your piece bag and also left click to place it on the board.\n"+
            "- Rotate Pieces: Use the arrow keys or scroll wheel (touchpad for laptops).\n"+
            "- Remove Pieces: Right-click to return a piece to your bag.\n"+
            "- No Retracing: Once placed, paths cannot double back or connect mid-way — only at the ends.");
        
        // Add Buttons section
        addSection(contentPanel, "Buttons & Their Functions", 
            "- CLEAN - Clears the board and returns all pieces to your bag.\n"+
            "- DONE - Press when you believe you've built a correct path. Your character will attempt to traverse it.\n"+
            "- SOLVE - Need help? This button triggers an algorithm to show a valid solution.");
        
        // Add Special Rules section
        addSection(contentPanel, "Special Rules", 
            "- Some levels are single-storey, others are multi-storey.\n"+
            "- STAIRS connect floors and must be used strategically. You may need to:\n"+
            "- Connect to a staircase multiple times\n"+
            "- Return to a floor to complete the correct checkpoint sequence",
            "res/rules/stairs.png");
        
        // Add Special Pieces section
        addSection(contentPanel, "Special Pieces", 
            "Some levels introduce bridges, unique pieces that let you cross over a piece.\n"+
            "Bridge Rules:\n"+
            "- Must connect two ends of the bridge to other pieces.\n"+
            "- The bridge itself is placed on top of an existing piece, forming a pass-through.",
            "res/rules/bridge.png");
        
        // Add Game Modes section with subsections
        addSection(contentPanel, "Game Modes", 
            "Choose from three exciting ways to play DungeonExit:");
        
        // Single Player Mode subsection
        addSection(contentPanel, "Single-Player Mode", 
            "Explore hand-crafted dungeons one by one, using only the pieces in your bag to reach your goal.\n"+
            "- Progress through increasingly challenging levels\n"+
            "- Master different piece combinations\n"+
            "- Unlock new dungeons as you advance",
            "res/rules/solo.png");
        
        // Versus Mode subsection
        addSection(contentPanel, "Versus Mode", 
            "Challenge a friend in head-to-head dungeon races!\n"+
            "- Each round lasts 30 seconds\n"+
            "- The first player to correctly connect all checkpoints in order wins\n"+
            "- Speed and accuracy are key — build fast, but think faster",
            "res/rules/vs.png");
        
        // Level Creator Mode subsection
        addSection(contentPanel, "Level Creator Mode", 
            "Design your own dungeon challenges with custom paths, pieces, and checkpoints.\n"+
            "How it Works:\n"+
            "- Use the SELECT button to select which pieces are available, click one time on a piece to add it to the piece bag and another time to remove it\n"+
            "- Click a checkpoint from the menu, then place it on the board with a left-click\n"+
            "- Note: Once placed, checkpoints cannot be removed\n"+
            "- Use ADD/REMOVE to add or delete upper floors (boards)\n"+
            "- CLEAN/DONE/SOLVE works as in gameplay modes\n\n"+
            "Saving Your Level:\n"+
            "- Click SAVE and enter a name for the level\n"+
            "- Only completed levels can be saved — you'll need to solve it first!");
        
        // Create scroll pane
        StyledScrollPane scrollPane = new StyledScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        
        return scrollPane;
    }
}