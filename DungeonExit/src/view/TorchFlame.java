package view;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;

/**
 * Inner class representing an animated torch flame
*/
public class TorchFlame {

	private float x, y;                  // Current position
	private float originX, originY;      // Original position
	private float intensity;             // Current brightness
	private boolean isMoving;            // Whether this torch moves around
	private float moveSpeed;             // How quickly the torch moves
	private float movementRange;         // How far from origin the torch can move
	private double moveAngle;            // Current movement direction angle
	private float targetX, targetY;      // Target position for movement
	
	private Color[] flameColors = {
		new Color(255, 180, 0),
		new Color(255, 120, 0),
		new Color(220, 80, 0),
		new Color(180, 50, 0)
	};
	
	public TorchFlame(float x, float y) {
		this.x = this.originX = x;
		this.y = this.originY = y;
		this.intensity = 1.0f;
		this.isMoving = false;
		this.moveSpeed = 1.0f;
		this.movementRange = 300.0f;
		this.moveAngle = Math.random() * Math.PI * 2; // Random initial angle
		pickNewTarget();
	}
	
	/**
	 * Set whether this torch can move around
	 */
	public void setMoving(boolean moving) {
		this.isMoving = moving;
	}
	
	/**
	 * Set how far from its origin the torch can move
	 */
	public void setMovementRange(float range) {
		this.movementRange = range;
	}
	
	/**
	 * Set how quickly the torch moves
	 */
	public void setMoveSpeed(float speed) {
		this.moveSpeed = speed;
	}
	
	/**
	 * Pick a new random target position to move towards
	 */
	private void pickNewTarget() {
		// Pick a random point within the movement range
		double angle = Math.random() * Math.PI * 2;
		float distance = (float)(Math.random() * movementRange);
		
		targetX = originX + (float)(Math.cos(angle) * distance);
		targetY = originY + (float)(Math.sin(angle) * distance);
		
		// Update movement angle to head toward the target
		moveAngle = Math.atan2(targetY - y, targetX - x);
	}
	
	/**
	 * Check if the torch has reached its target position
	 */
	private boolean hasReachedTarget() {
		float dx = targetX - x;
		float dy = targetY - y;
		return Math.sqrt(dx * dx + dy * dy) < 5.0f;
	}
	
	public void update() {
		// Make flame flicker randomly
		intensity = 0.7f + (float)(Math.random() * 0.3f);
		
		// Update movement if this torch moves
		if (isMoving) {
			// Move toward the target
			x += Math.cos(moveAngle) * moveSpeed;
			y += Math.sin(moveAngle) * moveSpeed;
			
			// If we've reached the target, pick a new one
			if (hasReachedTarget()) {
				pickNewTarget();
			}
			
			// Sometimes randomly change direction
			if (Math.random() < 0.01) {
				// 1% chance each frame to change direction
				pickNewTarget();
			}
		}
	}
	
	public void draw(Graphics2D g) {
		// Draw glow
		int glowRadius = 150;
		RadialGradientPaint glow = new RadialGradientPaint(
			x, y, glowRadius,
			new float[] {0.0f, 1.0f},
			new Color[] {
				new Color(255, 180, 0, (int)(80 * intensity)),
				new Color(255, 180, 0, 0)
			}
		);
		g.setPaint(glow);
		g.fillOval((int)x - glowRadius, (int)y - glowRadius, 
					glowRadius * 2, glowRadius * 2);
		
		// Draw flame
		for (int i = 0; i < flameColors.length; i++) {
			Color flameColor = flameColors[i];
			int alpha = (int)(255 * intensity);
			g.setColor(new Color(
				flameColor.getRed(), 
				flameColor.getGreen(), 
				flameColor.getBlue(), 
				alpha
			));
			
			int flameHeight = 30 - (i * 8);
			int flameWidth = 20 - (i * 4);
			
			// Add some randomness to flame shape
			double wobble = Math.sin(System.currentTimeMillis() / 100.0) * 3;
			
			g.fillOval((int)(x - flameWidth/2 + wobble), 
						(int)(y - flameHeight), 
						flameWidth, 
						flameHeight);
		}
	}
}