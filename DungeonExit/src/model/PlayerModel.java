package model;

import java.awt.image.BufferedImage;
import static utils.FileHandler.*;

/**
 * The PlayerModel class handles the player's animations, including loading and updating animation frames.
 * This class is used for experimenting with player or NPC animations in the game.
 */
public class PlayerModel {

    private BufferedImage[] currentAnim;
    private BufferedImage[] animUp; // Array to store animation frames
    private BufferedImage[] animDown;
    private BufferedImage[] animLeft;
    private BufferedImage[] chestAnim;
    private BufferedImage[] dragonAnim;

    private String direction = "";

    private int speed = 1;
    private int x;
    private int y;

    private int animFrame, animNumber, animSpeed = 10; // Animation control variables: frame counter, animation number, and speed
    private int animMax = 10;

    private boolean animChest = false;
    private boolean animDragon = false;

    /**
     * Constructs a PlayerModel and loads the player's animations.
     */
    public PlayerModel() {
        loadAnimations(); // Load the animations when the player model is created
        this.currentAnim = animDown;
        this.animNumber = 0;
    }

    /**
     * Updates the animation frame for the current animation. This method is called periodically to update the animation.
     */
    public void update() {
        updateAnimationFrame(); // Updates the animation frame
    }

    /**
     * Loads the animations for the player from the sprite sheet.
     * For now, it assumes a single row of sprites, each with a width of 96 pixels.
     */
    private void loadAnimations() {
        animUp = new BufferedImage[10]; // Initialize the array for 10 frames of animation
        animDown = new BufferedImage[10];
        animLeft = new BufferedImage[10];

        for (int i = 0; i < 10; i++) {
            animDown[i] = getSpriteImage("/player/new/playerSheets.png", 128 * i, 0, 128, 128, 1);
            animUp[i] = getSpriteImage("/player/new/playerSheets.png", 128 * (i + 20), 0, 128, 128, 1);
            animLeft[i] = getSpriteImage("/player/new/playerSheets.png", 128 * (i + 10), 0, 128, 128, 1);
        }
        this.chestAnim = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            chestAnim[i] = getSpriteImage("/tiles/new/chest/chestAnim.png", i * 128, 0, 128, 128, 1);
        }
        this.dragonAnim = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            dragonAnim[i] = getSpriteImage("/tiles/new/dragonAttack.png", i * 128, 0, 128, 128, 1);
        }
    }

    /**
     * Updates the animation frame counter. This method is responsible for switching between animation frames.
     * The speed of the animation is controlled by the `animSpeed` variable.
     */
    private void updateAnimationFrame() {
        animFrame++; // Increment the animation frame counter
        if (animFrame >= animSpeed) {
            animFrame = 0; // Reset the frame counter when it reaches the animation speed
            animNumber++;  // Move to the next frame in the animation
            if (animNumber >= animMax) { // Loop the animation back to the first frame if the last frame is reached
                animNumber = 0;
                if (animMax == 4) {
                    resetFrame(10);
                    animChest = true;
                    animDragon = true;
                    currentAnim = animUp;
                }
            }
        }
    }

    public void changeChestAnim() {
        animChest = false;
        resetFrame(4);
        currentAnim = chestAnim;
        direction = "N";
    }

    public void changeDragonAnim() {
        animDragon = false;
        resetFrame(4);
        currentAnim = dragonAnim;
    }

    public void changeDirection(String s){
        switch (s) {
            case "U" -> {
                currentAnim = animUp;
            }
            case "D" -> {
                currentAnim = animDown;
            }
            case "L" -> {
                currentAnim = animLeft;
            }
            case "R" -> {
                currentAnim = animLeft;
            }
        }
        direction = s;
    } 

    public boolean getAnimChest() {
        return animChest;
    }

    public boolean getAnimDragon() {
        return animDragon;
    }

    /**
     * Gets the array of animation frames.
     *
     * @return an array of BufferedImage objects representing the animation frames.
     */
    public BufferedImage[] getAnimations() {
        return currentAnim;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveUp() {
        this.x -= speed;
    }

    public void moveDown() {
        this.x += speed;
    }

    public void moveLeft() {
        this.y -= speed;
    }

    public void moveRight() {
        this.y += speed;
    }

    public String getDirection() {
        return direction;
    }

    public void resetFrame(int max) {
        animFrame = 0;
        animNumber = 0;
        animMax = max;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Gets the current animation frame number.
     *
     * @return the current animation frame index (0-9).
     */
    public int getAnimNumber() {
        return animNumber; // Returns the current frame index in the animation
    }
}
