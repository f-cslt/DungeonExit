package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import listeners.LevelListener;
import model.LevelModel;
import static utils.Constants.SCREEN_HEIGHT;
import static utils.Constants.SCREEN_WIDTH;
import utils.SoundManager;

/**
 * The LevelView class represents the visual rendering of a game level.
 * It contains the board view, the piece bag view, and a button to check the win condition.
 */
public class LevelView extends JLayeredPane {

    private final LevelModel levelModel;
    private ArrayList<BoardView> boardView;
    private PieceBagView pieceBagView;
    
    private final StyledPanel mainPane;
    private StyledButton checkWinButton;
    private StyledButton changeLevelButtonUp;
    private StyledButton changeLevelButtonDown;
    private final StyledButton backButton;
    private final StyledButton solveButton;
    private final StyledButton cleanButton;
    private final GridBagLayout layout;
    private final GridBagConstraints gbc;
    private int nbPose;

    private final JLabel nbPiecePose;

    private TorcheFlameBackground torcheFlameBackground;

    // Timer components
    private JLabel timerLabel;
    private JLabel bestTimeLabel;
    private Timer uiTimer;

    private final Color color;

    private LevelListener listener;

    private static final String BUTTON_CLICK_SOUND_PATH = "res/sounds/click-sound.wav";

    /**
     * Constructs a LevelView with the specified level model.
     * Initializes the board view, the piece bag view, and the check win button.
     *
     * @param levelModel The model containing level data.
     */
    public LevelView(LevelModel levelModel) {
        this.setFocusable(false);
        this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        this.torcheFlameBackground = new TorcheFlameBackground();

        this.mainPane = new StyledPanel(0);
        this.mainPane.setOpaque(false);

        this.color = new Color(21,151,209);
        //this.color = new Color(54,171,113);
        this.levelModel = levelModel;
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();

        this.mainPane.setLayout(layout);
        this.mainPane.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.mainPane.setFocusable(false);
        
        // Initialize sound
        initializeSound();
        
        initCheckWinButton();
        initChangeLevelButton();
        initTimerLabel();

        backButton = new StyledButton("BACK");
        backButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null) {
                listener.switchToLevelSelector();
            }
        }));
        backButton.setFocusable(false);
        solveButton = new StyledButton("SOLVE");
        solveButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null)
                listener.solve();
        }));
        solveButton.setFocusable(false);
        cleanButton = new StyledButton("CLEAN");
        cleanButton.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null)
            listener.clean();
        }));
        cleanButton.setFocusable(false);
        nbPiecePose = new JLabel();
    }

    private void initializeSound() {
        // Load sound effects
        SoundManager.getInstance().loadSoundEffect("button_click", BUTTON_CLICK_SOUND_PATH);
        SoundManager.getInstance().loadSoundEffect("piece_placement", BUTTON_CLICK_SOUND_PATH);
    }

    /**
     * Helper method to handle button clicks with sound
     */
    private void handleButtonClick(Runnable action) {
        SoundManager.getInstance().playSoundEffect("button_click");
        action.run();
    }

    public void setLevelListener(LevelListener listener) {
        this.listener = listener;
    }

    public void resetLevel(){
        removeAll();
        mainPane.removeAll();
        if (uiTimer != null) {
            uiTimer.stop();
        }
        // Reset timer label to 00:00
        if (timerLabel != null) {
            timerLabel.setText("00:00");
        }
        // Reset best time label
        if (bestTimeLabel != null) {
            bestTimeLabel.setText("Best: --:--");
        }
    }

    /**
     * Initializes the timer label and sets up the UI timer
     */
    private void initTimerLabel() {
        timerLabel = new JLabel("00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        bestTimeLabel = new JLabel("Best: --:--", SwingConstants.CENTER);
        bestTimeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Create a timer that updates every second
        uiTimer = new Timer(1000, e -> {
            if (levelModel != null) {
                timerLabel.setText(levelModel.getFormattedTime());
            }
        });
    }

    public void animPlay() {
        checkWinButton.setEnabled(false);
        backButton.setEnabled(false);
        cleanButton.setEnabled(false);
        solveButton.setEnabled(false);
    }

    public void animFinish() {
        checkWinButton.setEnabled(true);
        backButton.setEnabled(true);
        cleanButton.setEnabled(true);
        solveButton.setEnabled(true);
    }

    public void initLevelViews(){
        removeAll();
        mainPane.removeAll();
        this.boardView = new ArrayList<>();
        for (int i = 0; i < levelModel.getNbBoard(); i++) {
            this.boardView.add(new BoardView(levelModel.getBoard(i), color));
        }
        this.pieceBagView = new PieceBagView(levelModel.getPieceBag(), color);
        this.nbPiecePose.setText(levelModel.getPieceBag().getNbPose() + "/" + levelModel.getPieceBag().getLimits());
        this.nbPiecePose.setFont(new Font("arial", Font.BOLD, 30));
        this.nbPiecePose.setForeground(color);
        this.nbPose = 0;

        // Reset timer label to 00:00 and start the timer
        if (timerLabel != null) {
            timerLabel.setText("00:00");
        }

        // Set best time label
        String bestTime = levelModel.getBestTime();
        if (bestTimeLabel != null) {
            if (bestTime != null && !bestTime.isEmpty()) {
                bestTimeLabel.setText("Best: " + bestTime);
            } else {
                bestTimeLabel.setText("Best: --:--");
            }
        }

        timerLabel.setForeground(color);
        bestTimeLabel.setForeground(color);
        // Start the timer
        if (uiTimer != null) {
            uiTimer.start();
        }

        addobjects(mainPane, backButton, layout, gbc, 1, 0, 1, 1, GridBagConstraints.PAGE_START, 0);
        addobjects(mainPane, timerLabel, layout, gbc, 3, 0, 1, 1, GridBagConstraints.LINE_END, 0);
        addobjects(mainPane, bestTimeLabel, layout, gbc, 5, 0, 1, 1, GridBagConstraints.LINE_END, 0);
        addobjects(mainPane, pieceBagView, layout, gbc, 1, 1, 1, 4, GridBagConstraints.LINE_START, 10);
        addobjects(mainPane, boardView.getFirst(), layout, gbc, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
        addobjects(mainPane, checkWinButton, layout, gbc, 5, 5, 1, 1, GridBagConstraints.CENTER, 0);
        addobjects(mainPane, solveButton, layout, gbc, 6, 4, 1, 1, GridBagConstraints.PAGE_END, 0);
        addobjects(mainPane, cleanButton, layout, gbc, 6, 5, 1, 1, GridBagConstraints.PAGE_END, 0);

        if (boardView.size() != 1) {
            addobjects(mainPane, changeLevelButtonUp, layout, gbc, 6, 3, 1, 1, GridBagConstraints.LINE_START, 0);
            addobjects(mainPane, changeLevelButtonDown, layout, gbc, 6, 4, 1, 1, GridBagConstraints.LINE_START, 0);
        }
        addobjects(mainPane, nbPiecePose, layout, gbc, 1, 5, 1, 1, GridBagConstraints.CENTER, 0);

        add(mainPane);
        setLayer(mainPane, 1);

        add(torcheFlameBackground);
        setLayer(torcheFlameBackground, 0);
        revalidate();
    }

    /**
     * Initializes the button that checks the win condition.
     */
    private void initCheckWinButton() {
        checkWinButton = new StyledButton("Done");
        checkWinButton.setFocusable(false);
    }

    public JButton getCheckWinButton() {
        return checkWinButton;
    }


    private void initChangeLevelButton() {
        changeLevelButtonUp = new StyledButton("/\\");
        changeLevelButtonUp.setSize(changeLevelButtonUp.getWidth(), (int)(SCREEN_HEIGHT / 2));
        changeLevelButtonUp.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null)
                listener.changeLevel(levelModel.getCurrentBoard() + 1);
        }));

        changeLevelButtonDown = new StyledButton("\\/");
        changeLevelButtonDown.setSize(changeLevelButtonUp.getWidth(), (int)(SCREEN_HEIGHT / 2));
        changeLevelButtonDown.addActionListener(e -> handleButtonClick(() -> {
            if(listener != null)
                listener.changeLevel(levelModel.getCurrentBoard() - 1);
        }));
    }

    public void removeCurrentBoardView(){
        mainPane.remove(boardView.get(levelModel.getCurrentBoard()));
    }

    public void changeLevelView() {
        addobjects(mainPane, boardView.get(levelModel.getCurrentBoard()), layout, gbc, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
        revalidate();
        requestFocusInWindow();
    }

    /**
     * Updates the best time display with the new best time
     * 
     * @param bestTime The new best time to display
     */
    public void updateBestTimeDisplay(String bestTime) {
        if (bestTimeLabel != null) {
            if (bestTime != null && !bestTime.isEmpty()) {
                bestTimeLabel.setText("Best: " + bestTime);
            } else {
                bestTimeLabel.setText("Best: --:--");
            }
            bestTimeLabel.repaint();
        }
    }

    /**
     * Overrides the JPanel paintComponent method to render the board view.
     * This method is responsible for redrawing the level components.
     *
     * @param g The Graphics object used for drawing.
     */
    @Override
    public void paintComponent(Graphics g) {
        Toolkit.getDefaultToolkit().sync(); // Helps to prevent rendering issues
        super.paintComponent(g);
        if (levelModel.getPieceBag().getNbPose() != nbPose) {
            this.nbPiecePose.setText(levelModel.getPieceBag().getNbPose() + "/" + levelModel.getPieceBag().getLimits());
            nbPose = levelModel.getPieceBag().getNbPose();
            this.nbPiecePose.repaint();
        }
        boardView.get(levelModel.getCurrentBoard()).repaint(); // Ensure the board view is repainted
        torcheFlameBackground.repaint();
    }

    public BoardView getBoardView(int i) {
        return boardView.get(i);
    }

    public ArrayList<BoardView> getBoardView() {
        return boardView;
    }

    public PieceBagView getPieceBagView() {
        return pieceBagView;
    }

    /**
     * Gets the current time displayed on the timer
     * 
     * @return The current time string
     */
    public String getCurrentTime() {
        return timerLabel.getText();
    }
    
    /**
     * Stops the timer
     */
    public void stopTimer() {
        if (uiTimer != null) {
            uiTimer.stop();
        }
    }

    public void addobjects(Container c, Component componente, GridBagLayout layout,
        GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int insets){

        gbc.gridx = gridx;
        gbc.gridy = gridy;

        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;

        gbc.anchor = anchor;
        gbc.insets = new Insets(insets, insets, insets, insets);

        layout.setConstraints(componente, gbc);
        c.add(componente);
    }
}
