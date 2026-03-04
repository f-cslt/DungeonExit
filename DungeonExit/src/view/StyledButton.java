package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * StyledButton is a custom button class that extends JButton, designed to have
 * a unique style with hover and click effects, rounded corners, and a shadow effect.
 * It allows for a more visually engaging user interaction.
 */
public class StyledButton extends JButton {

    // Colors for button states
    static final Color baseColor = new Color(50, 50, 50); // Default background color
    static final Color hoverColor = new Color(80, 80, 80); // Color when mouse hovers over button
    static final Color clickColor = new Color(30, 30, 30); // Color when button is clicked
    
    // Whether to draw a visible border
    private boolean drawBorder = false;
    private Color borderColor = new Color(100, 100, 100, 80); // More subtle border color with transparency

    /**
     * Constructor to create a styled button with a specific text label.
     * Initializes the button's appearance, behavior, and mouse interactions.
     *
     * @param text The label text to be displayed on the button.
     */
    public StyledButton(String text) {
        super(text);

        // Disable focus visibility and modify other properties for custom appearance
        setFocusable(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setBackground(baseColor);
        setFont(new Font("Arial", Font.BOLD, 18));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding inside button
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor when hovering over button

        // Add mouse listeners for interactive hover and click effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor); // Change background color on hover
                repaint(); // Repaint the button to reflect the color change
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(baseColor); // Restore background color when mouse exits
                repaint(); // Repaint the button to reflect the color change
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(clickColor); // Change background color when button is clicked
                repaint(); // Repaint the button to reflect the color change
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor); // Restore hover color when button is released
                repaint(); // Repaint the button to reflect the color change
            }
        });
    }

    
    /**
     * Set whether the button should display a visible border
     * 
     * @param drawBorder true to show border, false to hide it
     */
    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
        repaint();
    }
    
    /**
     * Set the border color for this button
     * 
     * @param color the color to use for the border
     */
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    /**
     * Paints the button with custom styling: rounded corners, shadow, and optional border.
     * This method overrides the default `paintComponent` method to apply custom visuals.
     *
     * @param g The Graphics object used for rendering.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); // Create a Graphics2D object for better rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Anti-aliasing for smooth edges

        // Draw a shadow with slight offset for a 3D effect
        int width = getWidth();
        int height = getHeight();
        g2.setColor(new Color(0, 0, 0, 40)); // Slightly lighter shadow with alpha transparency
        g2.fillRoundRect(3, 5, width - 6, height - 6, 25, 25); // Draw shadow with rounded corners

        // Fill the button background with its current color
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width - 1, height - 1, 25, 25); // Draw button with rounded corners

        // Draw the border only if enabled
        if (drawBorder) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f)); // Thinner border
            g2.drawRoundRect(0, 0, width - 1, height - 1, 25, 25); // Draw the rounded border
        }

        // Call the super method to ensure the button's text is painted
        super.paintComponent(g2);
        g2.dispose(); // Dispose of the Graphics2D object after use
    }
}
