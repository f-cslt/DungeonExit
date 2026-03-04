package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.PlayerModel;

/**
 * The PlayerView class is responsible for rendering a visual representation of a player
 * on the screen. It takes the player model data (animations and animation number)
 * and draws the appropriate animation at a specific position on the screen.
 */
public class PlayerView {

    // PlayerModel instance that contains player-specific data
    private final PlayerModel playerModel;


    private int tileSize;

    /**
     * Constructor for creating a PlayerView instance.
     * This constructor requires a PlayerModel object, which contains the player's
     * animation data and state.
     *
     * @param playerModel The PlayerModel object that holds the player's data.
     */
    public PlayerView(PlayerModel playerModel, int tileSize){
        this.tileSize = tileSize;
        this.playerModel = playerModel;
    }

    public void playChestAnim() {
        playerModel.changeChestAnim();
        while (!playerModel.getAnimChest()) {
            System.out.print("");
        }
    }

    public void playDragonKill() {
        playerModel.changeDragonAnim();
        while (!playerModel.getAnimDragon()) {
            System.out.print("");
        }
    }

    public void update() {
        playerModel.update();
    }

    public void changeDirection(String s) {
        playerModel.changeDirection(s);
    }

    public void moveUp() {
        playerModel.moveUp();
    }

    public void moveDown() {
        playerModel.moveDown();
    }

    public void moveLeft() {
        playerModel.moveLeft();
    }

    public void moveRight() {
        playerModel.moveRight();
    }

    public int getX() {
        return playerModel.getX();
    }

    public int getY() {
        return playerModel.getY();
    }

    public void setX(int x) {
        playerModel.setX(x);
    }

    public void setY(int y) {
        playerModel.setY(y);
    }

    public BufferedImage getImage() {
        return playerModel.getAnimations()[playerModel.getAnimNumber()];
    }

    public String getDirection() {
        return playerModel.getDirection();
    }

    /**
     * Draws the player using the current animation frame from the PlayerModel.
     * The animation is drawn on the Graphics2D context at position (100, 100), scaled by a factor of 3.
     * The method fetches the player's current animation frame and renders it at a specific position on the screen.
     *
     * @param g The Graphics2D object used to draw the player's animation on the screen.
     */
    public void draw(Graphics2D g2){
        // Draw the current animation frame at the specified position (100, 100) with scaling factor 3
        if (!playerModel.getDirection().equals("R")) {
            g2.drawImage(
                    playerModel.getAnimations()[0],  // The current animation frame
                    playerModel.getY(),  // X position on the screen
                    playerModel.getX(),  // Y position on the screen
                    tileSize,  // Width (scaled by a factor of 3)
                    tileSize,  // Height (scaled by a factor of 3)
                    null  // ImageObserver (null because it’s not needed here)
            );
        } else {
             g2.drawImage(
                    playerModel.getAnimations()[playerModel.getAnimNumber()],  // The current animation frame
                    playerModel.getY() + tileSize,  // X position on the screen
                    playerModel.getX(),  // Y position on the screen
                    -tileSize,  // Width (scaled by a factor of 3)
                    tileSize,  // Height (scaled by a factor of 3)
                    null  // ImageObserver (null because it’s not needed here)
            );
        }
        g2.setColor(Color.WHITE);
        g2.fillRect(playerModel.getX(), playerModel.getY(), tileSize, tileSize);
        playerModel.update();
    }
}
