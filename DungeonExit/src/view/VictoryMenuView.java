package view;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import static utils.Constants.SCREEN_HEIGHT;
import static utils.Constants.SCREEN_WIDTH;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class VictoryMenuView extends StyledPanel {

    private StyledButton mainMenuButton;

    private BufferedImage soloVictory;
    private BufferedImage p1Victory;
    private BufferedImage p2Victory;
    private BufferedImage currentVictory;


    public VictoryMenuView() {

        mainMenuButton = new StyledButton("Return to Main Menu");
        mainMenuButton.setLocation(new Point(SCREEN_WIDTH / 2 - mainMenuButton.getWidth() / 2, SCREEN_HEIGHT - SCREEN_HEIGHT / 5));
        add(mainMenuButton);

        try {
            soloVictory =  ImageIO.read(getClass().getResource("/ui/victory_solo.png"));
            p1Victory =  ImageIO.read(getClass().getResource("/ui/victory_p1.png"));
            p2Victory =  ImageIO.read(getClass().getResource("/ui/victory_p2.png"));
        } catch (IOException e) {
        }
        currentVictory = soloVictory;
    }

    public JButton getMainMenuButton() {
        return mainMenuButton;
    }

    public void setCurrentVictory(int number) {
        switch (number) {
            case 0 -> {currentVictory = soloVictory;}
            case 1 -> {currentVictory = p1Victory;}
            case 2 -> {currentVictory = p2Victory;}
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // TODO Auto-generated method stub
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        g2.drawImage(currentVictory, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
    }
}
