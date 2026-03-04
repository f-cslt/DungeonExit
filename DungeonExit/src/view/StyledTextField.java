package view;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * StyledTextField is a custom text field that extends JTextField
 * with consistent styling and interactive focus effects.
 */
public class StyledTextField extends JTextField {

    private static final Color DEFAULT_BACKGROUND = new Color(60, 60, 60);
    private static final Color FOCUSED_BACKGROUND = new Color(70, 70, 70);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(100, 100, 100);
    private static final Color FOCUSED_BORDER_COLOR = StyledPanel.ACCENT_COLOR;
    private static final int BORDER_THICKNESS = 2;
    private static final int PADDING = 8;
    
    /**
     * Creates a StyledTextField with default text.
     */
    public StyledTextField() {
        this("");
    }
    
    /**
     * Creates a StyledTextField with the specified text.
     * 
     * @param text The text to be displayed initially.
     */
    public StyledTextField(String text) {
        super(text);
        initStyling();
    }
    
    /**
     * Creates a StyledTextField with the specified text and columns.
     * 
     * @param text The text to be displayed initially.
     * @param columns The number of columns.
     */
    public StyledTextField(String text, int columns) {
        super(text, columns);
        initStyling();
    }
    
    /**
     * Applies custom styling to the text field.
     */
    private void initStyling() {
        setFont(new Font("Arial", Font.PLAIN, 16));
        setForeground(TEXT_COLOR);
        setBackground(DEFAULT_BACKGROUND);
        setCaretColor(TEXT_COLOR);
        setSelectionColor(StyledPanel.ACCENT_COLOR);
        setSelectedTextColor(Color.WHITE);
        setFocusable(false);
        
        // Set border with padding
        setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, BORDER_THICKNESS),
            new EmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        
        // Add focus effects
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(new CompoundBorder(
                    new LineBorder(FOCUSED_BORDER_COLOR, BORDER_THICKNESS),
                    new EmptyBorder(PADDING, PADDING, PADDING, PADDING)
                ));
                setBackground(FOCUSED_BACKGROUND);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                setBorder(new CompoundBorder(
                    new LineBorder(BORDER_COLOR, BORDER_THICKNESS),
                    new EmptyBorder(PADDING, PADDING, PADDING, PADDING)
                ));
                setBackground(DEFAULT_BACKGROUND);
            }
        });
    }
} 