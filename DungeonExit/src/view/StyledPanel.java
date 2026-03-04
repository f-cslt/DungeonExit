package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * StyledPanel is a custom panel class that extends JPanel, designed to have
 * a consistent style with dark backgrounds, rounded corners, borders, and padding.
 * It provides a foundation for creating visually cohesive UI elements.
 */
public class StyledPanel extends JPanel {
    
    // Constants for styling
    public static final Color DARK_BACKGROUND = new Color(40, 40, 40);
    public static final Color DARKER_BACKGROUND = new Color(30, 30, 30);
    public static final Color BORDER_COLOR = new Color(100, 100, 100);
    public static final Color ACCENT_COLOR = new Color(150, 80, 150);
    public static final int BORDER_THICKNESS = 2;
    public static final int DEFAULT_PADDING = 15;
    
    /**
     * Creates a StyledPanel with default styling.
     */
    public StyledPanel() {
        this(DEFAULT_PADDING, BORDER_COLOR);
    }
    
    /**
     * Creates a StyledPanel with custom padding.
     * 
     * @param padding The padding inside the panel in pixels.
     */
    public StyledPanel(int padding) {
        this(padding, BORDER_COLOR);
    }
    
    /**
     * Creates a StyledPanel with custom padding and border color.
     * 
     * @param padding The padding inside the panel in pixels.
     * @param borderColor The color of the panel's border.
     */
    public StyledPanel(int padding, Color borderColor) {
        setBackground(DARK_BACKGROUND);
        setBorder(createStyledBorder(padding, borderColor));
        //setLayout(new BorderLayout());
    }
    
    /**
     * Creates a StyledPanel with a specific layout manager.
     * 
     * @param layout The layout manager for the panel.
     */
    public StyledPanel(LayoutManager layout) {
        this();
        setLayout(layout);
    }
    
    /**
     * Creates a StyledPanel with a specific layout manager and padding.
     * 
     * @param layout The layout manager for the panel.
     * @param padding The padding inside the panel in pixels.
     */
    public StyledPanel(LayoutManager layout, int padding) {
        this(padding);
        setLayout(layout);
    }
    
    /**
     * Creates a styled border with rounded corners and padding.
     * 
     * @param padding The padding inside the border in pixels.
     * @param borderColor The color of the border.
     * @return A CompoundBorder with both line border and padding.
     */
    public static CompoundBorder createStyledBorder(int padding, Color borderColor) {
        return new CompoundBorder(
            new LineBorder(borderColor, BORDER_THICKNESS, true), // Rounded border
            new EmptyBorder(padding, padding, padding, padding)  // Inner padding
        );
    }
    
    /**
     * Creates a title label with consistent styling.
     * 
     * @param title The text for the title.
     * @return A styled JLabel.
     */
    public static JLabel createTitleLabel(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(5, 0, 15, 0));
        return label;
    }
    
    /**
     * Adds a title to the top of the panel.
     * 
     * @param title The text for the title.
     */
    public void addTitle(String title) {
        add(createTitleLabel(title), BorderLayout.NORTH);
    }
} 