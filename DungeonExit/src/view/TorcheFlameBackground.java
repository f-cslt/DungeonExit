package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
import static utils.Constants.SCREEN_HEIGHT;
import static utils.Constants.SCREEN_WIDTH;
import utils.SoundManager;

public class TorcheFlameBackground extends JPanel {
	

    public static final Color DARK_BACKGROUND = new Color(40, 40, 40);
	// Animation elements
    private ArrayList<TorchFlame> torches;
    private Timer animationTimer;


	public TorcheFlameBackground() {
		this.setFocusable(false);
        setBackground(DARK_BACKGROUND);
		this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		initializeAnimation();
	}

	private void initializeAnimation() {
        // Create torch flames
        torches = new ArrayList<>();
        
        // We'll add the torches later in the paintComponent when we know the component size
        
        // Create animation timer (20 fps)
        animationTimer = new Timer(50, e -> {
            // Update all torches
            for (TorchFlame torch : torches) {
                torch.update();
            }
            // Request repaint to draw the updated animation
            repaint();
        });
        
        // Start the animation
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // TODO Auto-generated method stub
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        // If torches array is empty, initialize torch positions based on the component size
        if (torches.isEmpty()) {
            // Create two torches on either side of the title
            int centerX = getWidth() / 2;
            int topY = 80; // Position near the title
            
            torches.add(new TorchFlame(centerX - 350, topY));
            torches.add(new TorchFlame(centerX + 350, topY));
            
            // Add torches around menu buttons (positioned at the sides)
            int buttonY = 300; // Approximate Y position of the button area
            torches.add(new TorchFlame(centerX - 280, buttonY));
            torches.add(new TorchFlame(centerX + 280, buttonY));
            
            // Add more torches at the bottom corners for ambiance
            torches.add(new TorchFlame(centerX - 340, getHeight() - 100));
            torches.add(new TorchFlame(centerX + 340, getHeight() - 100));
            
            // Add some floating torches that move more freely
            for (int i = 0; i < 10; i++) {
                float x = (float)(Math.random() * getWidth());
                float y = (float)(Math.random() * getHeight());
                
                TorchFlame floatingTorch = new TorchFlame(x, y);
                floatingTorch.setMoving(true);
                floatingTorch.setMovementRange(60);
                floatingTorch.setMoveSpeed(0.8f + (float) (Math.random() * 0.6f));
                
                torches.add(floatingTorch);
            }
        }
        for (TorchFlame torch : torches) {
            torch.draw(g2d);
        }
    }

     /**
     * Cleanup resources when the panel is removed
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        
        // Stop the animation timer when the panel is removed
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        // Stop background music when menu is closed
        SoundManager.getInstance().stopAll();
    }
}
