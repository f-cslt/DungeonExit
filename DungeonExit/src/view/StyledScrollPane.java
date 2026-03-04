package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * StyledScrollPane is a custom scrollable panel that extends JScrollPane
 * with consistent styling for scrollbars, backgrounds, and borders.
 */
public class StyledScrollPane extends JScrollPane {

    /**
     * Creates a StyledScrollPane for the specified component.
     *
     * @param view The component to be displayed in the scroll pane.
     */
    public StyledScrollPane(Component view) {
        super(view);
        styleScrollPane();
    }
    
    /**
     * Creates a StyledScrollPane for the specified component with given policies.
     *
     * @param view The component to be displayed in the scroll pane.
     * @param vsbPolicy The vertical scrollbar policy.
     * @param hsbPolicy The horizontal scrollbar policy.
     */
    public StyledScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
        styleScrollPane();
    }
    
    /**
     * Applies custom styling to the scroll pane.
     */
    private void styleScrollPane() {
        // Set border
        setBorder(new LineBorder(StyledPanel.BORDER_COLOR, 1));
        
        // Style the viewport
        getViewport().setBackground(StyledPanel.DARKER_BACKGROUND);
        
        // Style scrollbars
        setScrollBarStyling(getVerticalScrollBar());
        setScrollBarStyling(getHorizontalScrollBar());
        
        // Remove corner button border
        setCorner(JScrollPane.LOWER_RIGHT_CORNER, createCornerButton());
    }
    
    /**
     * Creates a plain dark corner button for the scroll pane.
     *
     * @return JButton with dark styling.
     */
    private JButton createCornerButton() {
        JButton button = new JButton();
        button.setBackground(StyledPanel.DARKER_BACKGROUND);
        button.setBorder(null);
        return button;
    }
    
    /**
     * Applies custom styling to a scrollbar.
     *
     * @param scrollBar The scrollbar to be styled.
     */
    private void setScrollBarStyling(JScrollBar scrollBar) {
        scrollBar.setBackground(StyledPanel.DARKER_BACKGROUND);
        scrollBar.setUI(new StyledScrollBarUI());
        scrollBar.setUnitIncrement(16); // Smoother scrolling
    }
    
    /**
     * Inner class for styling the scrollbar UI.
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(100, 100, 100);
            this.trackColor = new Color(50, 50, 50);
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (!thumbBounds.isEmpty() && this.scrollbar.isEnabled()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                g2.dispose();
            }
        }
    }
} 