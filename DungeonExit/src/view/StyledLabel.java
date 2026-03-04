package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * StyledLabel is a custom label class that extends JLabel
 * with consistent styling for text display.
 */
public class StyledLabel extends JLabel {
    
    // Style constants
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 14);
    
    /**
     * Creates a StyledLabel with default styling and the specified text.
     * 
     * @param text The text to be displayed.
     */
    public StyledLabel(String text) {
        this(text, NORMAL_FONT, Color.WHITE);
    }
    
    /**
     * Creates a StyledLabel with the specified text and font.
     * 
     * @param text The text to be displayed.
     * @param font The font to use.
     */
    public StyledLabel(String text, Font font) {
        this(text, font, Color.WHITE);
    }
    
    /**
     * Creates a StyledLabel with the specified text, font, and color.
     * 
     * @param text The text to be displayed.
     * @param font The font to use.
     * @param color The text color.
     */
    public StyledLabel(String text, Font font, Color color) {
        super(text);
        setFont(font);
        setForeground(color);
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }
    
    /**
     * Creates a title label with large bold font and centered alignment.
     * 
     * @param text The title text.
     * @return A StyledLabel configured as a title.
     */
    public static StyledLabel createTitle(String text) {
        StyledLabel label = new StyledLabel(text, TITLE_FONT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(10, 10, 15, 10));
        return label;
    }
    
    /**
     * Creates a subtitle label with medium bold font.
     * 
     * @param text The subtitle text.
     * @return A StyledLabel configured as a subtitle.
     */
    public static StyledLabel createSubtitle(String text) {
        StyledLabel label = new StyledLabel(text, SUBTITLE_FONT);
        label.setBorder(new EmptyBorder(8, 8, 8, 8));
        return label;
    }
    
    /**
     * Creates a heading label with medium bold font and proper padding.
     * 
     * @param text The heading text.
     * @return A StyledLabel configured as a heading.
     */
    public static StyledLabel createHeading(String text) {
        StyledLabel label = new StyledLabel(text, SUBTITLE_FONT);
        label.setBorder(new EmptyBorder(10, 5, 10, 5));
        return label;
    }
    
    /**
     * Creates an instruction label with smaller font and blue hint color.
     * 
     * @param text The instruction text.
     * @return A StyledLabel configured as an instruction.
     */
    public static StyledLabel createInstructionLabel(String text) {
        StyledLabel label = new StyledLabel(text, SMALL_FONT, new Color(150, 200, 255));
        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        return label;
    }
} 