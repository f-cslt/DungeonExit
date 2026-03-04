package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import static utils.Constants.SCREEN_HEIGHT;
import static utils.Constants.SCREEN_WIDTH;

public class ModeSelectionView extends StyledPanel {
	
	private LevelSelectorView levelSelectorView;

    private final Color baseColor = new Color(50, 50, 50); // Default background color
    private final Color hoverColor = new Color(80, 80, 80); // Color when mouse hovers over button
    private final Color clickColor = new Color(30, 30, 30); // Color when button is clicked
	private final Color borderColor = new Color(100, 100, 100, 80); // More subtle border color with transparency

    private StyledButton    multiButton;
    private StyledButton    soloButton;
	private StyledButton	backButton;

	private GridBagLayout layout;
    private GridBagConstraints gbc;

	private StyledTextField textField;

	private String modeSelect;

	public ModeSelectionView(LevelSelectorView levelSelectorView) {
		this.levelSelectorView = levelSelectorView;

		layout = new GridBagLayout();
        gbc = new GridBagConstraints();

        this.setLayout(layout);
        this.setFocusable(false);
		this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

		
		multiButton = new StyledButton("");
		multiButton.addActionListener(e -> {
			levelSelectorView.setMode("mult");
			levelSelectorView.switchToLevelSelection();
		});

		multiButton.setIcon(new ImageIcon("./res/ui/vs.png"));

		soloButton = new StyledButton("");
		soloButton.addActionListener(e -> {
			levelSelectorView.setMode("map");
			levelSelectorView.switchToLevelSelection();
		});

		soloButton.setIcon(new ImageIcon("./res/ui/solo.png"));

		backButton = new StyledButton("BACK");

		textField = new StyledTextField("CHOOSE GAME MODE");
		textField.setFont(new Font("Arial", Font.PLAIN, 50));

		addobjects(this, backButton, layout, gbc, 0, 0, 1, 1, GridBagConstraints.FIRST_LINE_START, 0);
		addobjects(this, textField, layout, gbc, 1, 0, 2, 1, GridBagConstraints.CENTER, 10);
		addobjects(this, soloButton, layout, gbc, 1, 1, 1, 1, GridBagConstraints.CENTER, 10);
		addobjects(this, multiButton, layout, gbc, 2, 1, 1, 1, GridBagConstraints.CENTER, 41);

    }

	public String getMode() {
		return modeSelect;
	}
	
	public StyledButton getBackButton() {
		return backButton;
	}

	public final void addobjects(Container c, Component componente, GridBagLayout layout,
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
