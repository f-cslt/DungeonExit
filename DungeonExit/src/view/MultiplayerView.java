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
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import model.MultiplayerModel;
import static utils.Constants.SCREEN_HEIGHT;
import static utils.Constants.SCREEN_WIDTH;

public class MultiplayerView extends JLayeredPane {

    private final Color colorP1 = new Color(21,151,209);
    private final Color colorP2 = new Color(54,171,113);
    private final Color colorTrans = new Color(50, 50, 50, 150);

	private final MultiplayerModel multiplayerModel;

    private ArrayList<BoardView> boardView;
	private PieceBagView	pieceBagViewP1;
	private PieceBagView	pieceBagViewP2;

	private final StyledPanel	mainPane;
	private final GridBagLayout layoutMain;
	private final GridBagConstraints gbcMain;

	private final StyledButton changeLevelButtonUp;
    private final StyledButton changeLevelButtonDown;
    private final StyledButton backButton;
	private final StyledButton changeRoundButton;
    private final StyledButton doneButton;

	private JPanel roundTextPanel;
    private TorcheFlameBackground torcheFlameBackground;

	private StyledTextField textPlayer;
    private StyledTextField textRound;

	private JLabel timerLabel;
    private Timer uiTimer;

	public MultiplayerView(MultiplayerModel multiplayerModel) {
		this.multiplayerModel = multiplayerModel;
        this.torcheFlameBackground = new TorcheFlameBackground();

		this.setFocusable(false);

		mainPane = new StyledPanel();
		mainPane.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		mainPane.setFocusable(false);
        mainPane.setOpaque(false);

		layoutMain = new GridBagLayout();
		gbcMain = new GridBagConstraints();

		mainPane.setLayout(layoutMain);

		backButton = new StyledButton("BACK");
        backButton.setFocusable(false);

		changeRoundButton = new StyledButton("CHANGE");
		changeLevelButtonUp = new StyledButton("/\\");
        changeLevelButtonDown = new StyledButton("\\/");
        doneButton = new StyledButton("DONE");
        initTimerLabel();
        initRoundTextPane();
	}

	public void resetLevel(){
        removeAll();
        if (uiTimer != null) {
            uiTimer.stop();
        }
        // Reset timer label to 00:00
        if (timerLabel != null) {
            timerLabel.setText("00 / 30");
        }
    }

    public void activeRoundPanel() {
        textRound.setText("PLAYER " + multiplayerModel.getCurrentRound());
        textRound.setForeground((multiplayerModel.getCurrentRound() == 1) ? colorP1 : colorP2);
        add(roundTextPanel);
        setLayer(roundTextPanel, 2);
        revalidate();
        requestFocusInWindow();
        Thread thread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            long nowTime = System.currentTimeMillis();
            while (nowTime - startTime < 1000) {
                nowTime = System.currentTimeMillis();
            }
            remove(roundTextPanel);
        });
        thread.start();
    }

    private void initTimerLabel() {
        timerLabel = new JLabel("00 / 30", SwingConstants.CENTER);
        timerLabel.setFocusable(false);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Create a timer that updates every second
        uiTimer = new Timer(1000, e -> {
            if (multiplayerModel != null) {
                timerLabel.setText(multiplayerModel.getFormattedTime());
            }
        });
    }

    private void initRoundTextPane() {
        textRound = new StyledTextField("PLAYER 1");
        textRound.setFont(new Font("Arial", Font.BOLD, 100));
        textRound.setFocusable(false);
        roundTextPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // TODO Auto-generated method stub
                super.paintComponent(g);
                g.setColor(colorTrans);
                g.fillRect(0, SCREEN_HEIGHT / 2 - SCREEN_HEIGHT / 8, SCREEN_WIDTH, SCREEN_HEIGHT / 4);
                textRound.repaint();
            }
        };
        roundTextPanel.setFocusable(false);
        roundTextPanel.setLayout(layoutMain);
        roundTextPanel.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        roundTextPanel.setBackground(colorTrans);
        roundTextPanel.setOpaque(false);
        addobjects(roundTextPanel, textRound, layoutMain, gbcMain, 0, 0, 1, 1, GridBagConstraints.CENTER, 0);
    }

    public void initMultiplayerViews(){
        removeAll();
		mainPane.removeAll();
    
        this.boardView = new ArrayList<>();
        for (int i = 0; i < multiplayerModel.getNbBoard(); i++) {
            this.boardView.add(new BoardView(multiplayerModel.getBoard(i), colorP1));
        }
        this.pieceBagViewP1 = new PieceBagView(multiplayerModel.getPieceBagPlayer1(), colorP1);
		this.pieceBagViewP2 = new PieceBagView(multiplayerModel.getPieceBagPlayer2(), colorP2);

		textPlayer = new StyledTextField("PLAYER 1");
		textPlayer.setFont(new Font("Arial", Font.BOLD, 30));
		textPlayer.setForeground(colorP1);

 
        // Reset timer label to 00:00 and start the timer
        if (timerLabel != null) {
            timerLabel.setText("00 / 30");
        }


        timerLabel.setForeground(colorP1);
        // Start the timer

        if (uiTimer != null) {
            uiTimer.start();
        }


        addobjects(mainPane, backButton, layoutMain, gbcMain, 1, 0, 1, 1, GridBagConstraints.PAGE_START, 0);
        addobjects(mainPane, pieceBagViewP1, layoutMain, gbcMain, 1, 1, 1, 4, GridBagConstraints.LINE_START, 10);
        addobjects(mainPane, boardView.getFirst(), layoutMain, gbcMain, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
		addobjects(mainPane, changeRoundButton, layoutMain, gbcMain, 1, 5, 1, 2, GridBagConstraints.CENTER, 10);
		addobjects(mainPane, textPlayer, layoutMain, gbcMain, 3, 0, 3, 1, GridBagConstraints.CENTER, 0);
        addobjects(mainPane, timerLabel, layoutMain, gbcMain, 2, 0, 1, 1, GridBagConstraints.CENTER, 0);
        addobjects(mainPane, doneButton, layoutMain, gbcMain, 5, 5, 1, 1, GridBagConstraints.PAGE_END, 0);

        if (boardView.size() != 1) {
            addobjects(mainPane, changeLevelButtonUp, layoutMain, gbcMain, 6, 3, 1, 1, GridBagConstraints.LINE_START, 0);
            addobjects(mainPane, changeLevelButtonDown, layoutMain, gbcMain, 6, 5, 1, 1, GridBagConstraints.LINE_START, 0);
        }

		this.add(mainPane);
		this.setLayer(mainPane, 1);

        this.add(torcheFlameBackground);
        this.setLayer(torcheFlameBackground, 0);

        revalidate();
		requestFocusInWindow();
    }

	public void switchRound(int player) {
		mainPane.removeAll();
		for (BoardView b : boardView) {
			b.setColor((player == 1) ? colorP1 : colorP2);
		}
		addobjects(mainPane, backButton, layoutMain, gbcMain, 1, 0, 1, 1, GridBagConstraints.PAGE_START, 0);
        addobjects(mainPane, (player == 1) ? pieceBagViewP1 : pieceBagViewP2,
			layoutMain, gbcMain, 1, 1, 1, 4, GridBagConstraints.LINE_START, 10);
        addobjects(mainPane, boardView.get(multiplayerModel.getCurrentBoard()), layoutMain, gbcMain, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
		addobjects(mainPane, changeRoundButton, layoutMain, gbcMain, 1, 5, 1, 2, GridBagConstraints.CENTER, 10);

		textPlayer.setText((player == 1) ? "PLAYER 1" : "PLAYER 2");
		textPlayer.setForeground((player == 1) ? colorP1 : colorP2);
		addobjects(mainPane, textPlayer, layoutMain, gbcMain, 3, 0, 3, 1, GridBagConstraints.CENTER, 0);

        timerLabel.setForeground((player == 1) ? colorP1 : colorP2);
        addobjects(mainPane, timerLabel, layoutMain, gbcMain, 2, 0, 1, 1, GridBagConstraints.CENTER, 0);
        addobjects(mainPane, doneButton, layoutMain, gbcMain, 5, 5, 1, 1, GridBagConstraints.PAGE_END, 0);

        if (boardView.size() != 1) {
            addobjects(mainPane, changeLevelButtonUp, layoutMain, gbcMain, 6, 3, 1, 1, GridBagConstraints.LINE_START, 0);
            addobjects(mainPane, changeLevelButtonDown, layoutMain, gbcMain, 6, 5, 1, 1, GridBagConstraints.LINE_START, 0);
        }
        revalidate();
		requestFocusInWindow();
	}

    public void removeCurrentBoardView(){
        mainPane.remove(boardView.get(multiplayerModel.getCurrentBoard()));
    }

    public void animPlay() {
        doneButton.setEnabled(false);
        backButton.setEnabled(false);
    }

    public void animFinish() {
        doneButton.setEnabled(true);
        backButton.setEnabled(true);
    }

    public void changeLevelView() {
        addobjects(mainPane, boardView.get(multiplayerModel.getCurrentBoard()),
			layoutMain, gbcMain, 2, 1, 4, 4, GridBagConstraints.CENTER, 0);
        revalidate();
        requestFocusInWindow();
    }

	public StyledButton getBackButton() {
		return backButton;
	}

	public StyledButton getChangeRoundButton() {
		return changeRoundButton;
	}

    public StyledButton getDoneButton() {
		return doneButton;
	}

	public StyledButton getChangeLevelButtonUp() {
		return changeLevelButtonUp;
	}

	public StyledButton getChangeLevelButtonDown() {
		return changeLevelButtonDown;
	}

	public ArrayList<BoardView> getBoardView() {
		return boardView;
	}

	public PieceBagView getPieceBagViewPlayer1() {
		return pieceBagViewP1;
	}

	public PieceBagView getPieceBagViewPlayer2() {
		return pieceBagViewP2;
	}

    @Override
    public void paintComponent(Graphics g) {
        Toolkit.getDefaultToolkit().sync(); // Helps to prevent rendering issues
        super.paintComponent(g);
        boardView.get(multiplayerModel.getCurrentBoard()).repaint(); // Ensure the board view is repainted
        torcheFlameBackground.repaint();
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
