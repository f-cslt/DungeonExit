package view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * StyledComboBox is a custom combo box that extends JComboBox
 * with consistent styling for dropdown menus.
 */
public class StyledComboBox<E> extends JComboBox<E> {

    private static final Color BACKGROUND_COLOR = new Color(60, 60, 60);
    private static final Color SELECTION_BACKGROUND = StyledPanel.ACCENT_COLOR;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final int PADDING = 8;
    
    /**
     * Creates a StyledComboBox with an array of items.
     * 
     * @param items The array of items to add to the combo box.
     */
    public StyledComboBox(E[] items) {
        super(items);
        initStyling();
    }
    
    /**
     * Creates a StyledComboBox with a ComboBoxModel.
     * 
     * @param model The model for the combo box.
     */
    public StyledComboBox(ComboBoxModel<E> model) {
        super(model);
        initStyling();
    }
    
    /**
     * Creates an empty StyledComboBox.
     */
    public StyledComboBox() {
        super();
        initStyling();
    }
    
    /**
     * Applies custom styling to the combo box.
     */
    private void initStyling() {
        // Set basic properties
        setBackground(BACKGROUND_COLOR);
        setForeground(TEXT_COLOR);
        setFont(new Font("Arial", Font.PLAIN, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add border and padding
        setBorder(new CompoundBorder(
            new LineBorder(StyledPanel.BORDER_COLOR, 1),
            new EmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        
        // Style the renderer for items
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                // Set colors based on selection state
                if (isSelected) {
                    label.setBackground(SELECTION_BACKGROUND);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(new Color(50, 50, 50));
                    label.setForeground(TEXT_COLOR);
                }
                
                // Add padding to items
                label.setBorder(new EmptyBorder(8, 12, 8, 12));
                return label;
            }
        });
        
        // Change UI for popup and arrow button
        UIManager.put("ComboBox.background", BACKGROUND_COLOR);
        UIManager.put("ComboBox.foreground", TEXT_COLOR);
        UIManager.put("ComboBox.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.buttonBackground", BACKGROUND_COLOR);
        UIManager.put("ComboBox.buttonDarkShadow", BACKGROUND_COLOR);
        UIManager.put("ComboBox.buttonHighlight", BACKGROUND_COLOR);
        UIManager.put("ComboBox.buttonShadow", BACKGROUND_COLOR);
    }
} 