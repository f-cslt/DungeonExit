package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.Board;
import model.PlacementPreview;
import model.PlayerModel;
import model.Tile;
import model.Tile.TileType;
import static utils.Constants.SCREEN_WIDTH;
import utils.FileHandler;

/**
 * The BoardView class is responsible for rendering the game board.
 * It extends JPanel and displays the tiles and pieces present on the board.
 */
public class BoardView extends JPanel {

    private final Board board; // The game board model
    private int boardSize; // The size of the board in pixels
    private int tileSize; // The size of each tile in pixels
    private Color color;

    private final PlayerView playerView;
    private boolean animPlayer;
    private int speedAnim = 10;

    private Color player1Color = new Color(21,151,209);
    private Color player2Color = new Color(54,171,113);

    private final BufferedImage defaultImage;
    private final BufferedImage defaultImage2;
    private final BufferedImage defaultImage3;
    private final BufferedImage basePath[];
    private final BufferedImage oneOpenPath[];
    private final BufferedImage twoOpenPath[];
    private final BufferedImage coinPath[];

    private final BufferedImage dragonImage[];
    private final BufferedImage chestImageOpen;
    private final BufferedImage chestImageClose;

    private final BufferedImage threeConnect;
    private final BufferedImage fourConnect;

    private boolean takeKey = false;
    private boolean dragonKill = false;
    private boolean chestOpen = false;

    private int frameTime = 0;
    private int frameIndex = 0;
    private int frameSpeed = 50;


    /**
     * Constructs a BoardView instance to visualize the given board.
     *
     * @param board the Board model that this view will render.
     */
    public BoardView(Board board, Color color) {
        this.animPlayer = false;
        this.board = board;
        this.color = color;
        boardSize = (int)(SCREEN_WIDTH / 2.5); // Default board size in pixels
        tileSize = boardSize / board.getRowCount(); // Calculate the tile size dynamically


        this.speedAnim = tileSize / 15 * 2;
        this.playerView = new PlayerView(new PlayerModel(), tileSize);
        setPlayerPos(new Point(0, 0));

        setPreferredSize(new Dimension(boardSize, boardSize));

        setBorder(BorderFactory.createLineBorder(color, 4)); // Add a red border for debugging

        try {
            this.defaultImage = ImageIO.read(getClass().getResource("/tiles/new/default.png"));
            this.defaultImage2 = ImageIO.read(getClass().getResource("/tiles/new/default2.png"));
            this.defaultImage3 = ImageIO.read(getClass().getResource("/tiles/new/default3.png"));
            this.threeConnect = ImageIO.read(getClass().getResource("/tiles/new/threeConnect.png"));
            this.fourConnect = ImageIO.read(getClass().getResource("/tiles/new/fourConnect.png"));
            this.basePath = initializeImage("basePath.png");
            this.oneOpenPath = initializeImage("1openPath.png");
            this.twoOpenPath = initializeImage("2openPath.png");
            this.coinPath = initializeImage("coinPath.png");

            this.dragonImage = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                dragonImage[i] = ImageIO.read(getClass().getResource("/tiles/new/dragon.png")).getSubimage(i * 128, 0, 128, 128);
            }

            this.chestImageOpen = ImageIO.read(getClass().getResource("/tiles/new/chest/chest5.png"));
            this.chestImageClose = ImageIO.read(getClass().getResource("/tiles/new/chest/chest1.png"));
         } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Random r = new Random();
        for (int i = 0; i < board.getColCount(); i++) {
            for (int j = 0; j < board.getRowCount(); j++) {
                if (board.getTile(i, j).getType() == TileType.DEFAULT) {
                    int value = r.nextInt(10);
                    if (value < 5) {
                        board.getTile(i, j).setImage(defaultImage3);
                    } else if (value < 7) {
                        board.getTile(i, j).setImage(defaultImage2);
                    } else {
                        board.getTile(i, j).setImage(defaultImage);
                    }
                }
            } 
        }
    }

    private BufferedImage[] initializeImage(String name) throws IOException {
        BufferedImage[] image = new BufferedImage[2];
        image[0] = ImageIO.read(getClass().getResource("/tiles/new/player1/" + name));
        image[1] = ImageIO.read(getClass().getResource("/tiles/new/player2/" + name));
        return image;
    }

    public void setColor(Color color) {
        this.color = color;
        setBorder(BorderFactory.createLineBorder(color, 4)); // Add a red border for debugging
        revalidate();
        requestFocusInWindow();
        repaint();
    }

    /**
     * Paints the components of the board, including tiles and pieces.
     * This method is automatically called when the panel is repainted.
     *
     * @param g the Graphics object used to draw the components.
     */
    @Override
    public void paintComponent(Graphics g) {
        Toolkit.getDefaultToolkit().sync(); // Helps prevent rendering lag
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw each tile of the board
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColCount(); j++) {
                g2.drawImage(
                        getImageTile(board.getTile(i, j), i, j), // Retrieve the image corresponding to the tile type
                        j * tileSize, i * tileSize, // Position on the panel
                        tileSize, tileSize, // Size of the tile
                        null
                );
                if (board.isCheckpoint(board.getTile(i, j).getType())) {
                    if (board.getTile(i, j).getType() == TileType.DRAGON && !dragonKill) {
                        g2.drawImage(
                            getDragonImage(),
                            j * tileSize, i * tileSize,
                            tileSize, tileSize,
                            null
                        );
                    } else if (board.getTile(i, j).getType() == TileType.CHEST) {
                        g2.drawImage(
                            (chestOpen) ? chestImageOpen : chestImageClose,
                            j * tileSize, i * tileSize,
                            tileSize, tileSize,
                            null
                        );
                    } else if (board.getTile(i, j).getType() == TileType.KEY && !takeKey) {
                        g2.drawImage(
                            board.getTile(i, j).getType().image,
                            j * tileSize, i * tileSize,
                            tileSize, tileSize,
                            null
                        );
                    } else if (board.getTile(i, j).getType() != TileType.KEY && board.getTile(i, j).getType() != TileType.CHEST
                        && board.getTile(i, j).getType() != TileType.DRAGON){
                        g2.drawImage(
                            board.getTile(i, j).getType().image,
                            j * tileSize, i * tileSize,
                            tileSize, tileSize,
                            null
                        );
                    }
                }
                if (board.getPieceFromSpecialPieceGridAt(i, j) != null && board.getPieceFromSpecialPieceGridAt(i, j).isBridge()) {
                    g2.drawImage(
                        TileType.BRIDGE.image,
                        j * tileSize, i * tileSize,
                        tileSize, tileSize,
                        null
                    );
                }
            }
        }
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColCount(); j++) {
                if (board.isCheckpoint(board.getTile(i, j).getType()) && board.getTile(i, j).getType() != Tile.TileType.STAIRS) {
                    g2.setColor(((board.getTile(i, j).getPlayer() == 1) ? player1Color : player2Color));
                    g2.setStroke(new java.awt.BasicStroke(3));
                    g2.drawRect(j * tileSize, i * tileSize, tileSize, tileSize);
                }
            }
        }
        if(board.getPreview() != null){
            if (board.isDragging()) {
                drawGhostPiece(g2);
            }
            drawPiecePreview(g2);
        }
        if (animPlayer) {
            if (!playerView.getDirection().equals("R")) {
                g2.drawImage(playerView.getImage(), playerView.getY(), playerView.getX(), tileSize, tileSize, null);
            } else {
                g2.drawImage(playerView.getImage(), playerView.getY() + tileSize, playerView.getX(), -tileSize, tileSize, null);
            }
            playerView.update();
        }
        
    }

    public void setPlayerPos(Point p) {
        playerView.setX(p.x);
        playerView.setY(p.y);
    }
    
    public void makeAnimPlayer(Thread thread, String s, int advance) {
        animPlayer = true;
        int i = 0;
        for (char c : s.toCharArray()) {
            switch (c) {
                case 'U' -> {movePlayerOneTile(thread, new Point(playerView.getX() - tileSize, playerView.getY()));}
                case 'D' -> {movePlayerOneTile(thread, new Point(playerView.getX() + tileSize, playerView.getY()));}
                case 'L' -> {movePlayerOneTile(thread, new Point(playerView.getX(), playerView.getY() - tileSize));}
                case 'R' -> {movePlayerOneTile(thread, new Point(playerView.getX(), playerView.getY() + tileSize));}
                case 'k' -> {takeKey = true;}
                case 'c' -> {if (advance > 1) {playerView.playChestAnim(); chestOpen = true;}}
                case 'd' -> {if (advance > 2) {playerView.playDragonKill(); dragonKill = true;}}
            }
            i++;
        }
        animPlayer = false;
    }

    public void stopAnim() {
        animPlayer = false;
        takeKey = false;
        dragonKill = false;
        chestOpen = false;
    }

    public void movePlayerOneTile(Thread thread, Point pos) {
        while (!(playerView.getX() == pos.x && playerView.getY() == pos.y)) {
            if (pos.x > playerView.getX()) {
                playerView.changeDirection("D");
                playerView.moveDown();
            } else if (pos.x < playerView.getX()) {
                playerView.changeDirection("U");
                playerView.moveUp();
            } else if (pos.y < playerView.getY()) {
                playerView.changeDirection("L");
                playerView.moveLeft();
            } else {
                playerView.changeDirection("R");
                playerView.moveRight();
            }
            try {
                thread.sleep(speedAnim);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage getDragonImage() {
        frameTime++;
        if (frameTime >= frameSpeed) {
            frameIndex++;
            frameTime = 0;
        }
        if (frameIndex >= 4) {
            frameIndex = 0;
        }
        return dragonImage[frameIndex];
    }

    /**
     * Draws a semi-transparent preview of a game piece on the board to visualize its potential placement.
     * The preview's color indicates validity: green for a valid placement, red for an invalid one.
     * 
     * @param g       the Graphics2D object used for drawing.
     */
    private void drawPiecePreview(Graphics2D g) {
        PlacementPreview preview = board.getPreview();
        if (preview == null) return;
    
         // preview color : red if invalid placement, green if valid placement
        Color previewColor = preview.isValid() ? 
            new Color(0, 255, 0, 120) : 
            new Color(255, 0, 0, 120);
        
        g.setColor(previewColor);
        for (int[] offset : preview.getSelectedPiece().getShape()) {
            int x = (preview.getCol() + offset[0]) * tileSize;
            int y = (preview.getRow() + offset[1]) * tileSize;
            // Semi-transparent overlay
            g.fillRect(x, y, tileSize, tileSize);
        }
    }

    /**
     * Draws the puzzle piece in its original position while dragging and dropping it. 
     * The piece appears on the board as a transparent grey piece. 
     * 
     * @param g Graphics2D context used to draw
     */
    private void drawGhostPiece(Graphics2D g) {
        List<int[]> originalShape = board.getOriginalDragShape();
        if (originalShape == null) return;
        g.setColor(new Color(255, 255, 255, 80));
        
        for (int[] offset : originalShape) {
            int x = (board.getOriginalDragCol() + offset[0]) * tileSize;
            int y = (board.getOriginalDragRow() + offset[1]) * tileSize;
            g.fillRect(x, y, tileSize, tileSize);
        }
    }

    /**
     * Gets the current tile size in pixels.
     *
     * @return the size of a single tile.
     */
    public int getTileSize() {
        return tileSize;
    }

    public void setAnimSpeed(int speed) {
        speedAnim = speed;
    }

    private int getNumberConnection(int i, int j) {
        int connect = 0;

        if (i - 1 >= 0 && board.getTile(i - 1, j).getType() != TileType.DEFAULT) {
            connect++;
        }
        if (j - 1 >= 0 && board.getTile(i, j - 1).getType() != TileType.DEFAULT) {
            connect++;
        }
        if (i + 1 <= board.getRowCount() - 1 && board.getTile(i + 1, j).getType() != TileType.DEFAULT) {
            connect++;
        }
        if (j + 1 <= board.getColCount() - 1 && board.getTile(i, j + 1).getType() != TileType.DEFAULT) {
            connect++;
        }
        return connect;
    }

    private BufferedImage getImageTile(Tile tile, int i, int j) {
        if (tile.getType() == TileType.DEFAULT) {
            return tile.getImage();
        }
        else {
            int nbConnection = getNumberConnection(i, j);
            switch (nbConnection) {
                case 1 -> {
                    return oneConnectionCase(tile.getPlayer() - 1, i, j);
                }
                case 2 -> {
                    return twoConnectionCase(tile.getPlayer() - 1, i, j);
                }
                case 3 -> {
                    return threeConnectionCase(tile.getPlayer() - 1, i, j);
                }
                case 4 -> {
                    return fourConnect;
                }
            }
        }
        return basePath[tile.getPlayer() - 1];
    }

    private BufferedImage oneConnectionCase(int player, int i, int j) {
        if (i - 1 >= 0 && board.getTile(i - 1, j).getType() != TileType.DEFAULT) {
            return FileHandler.rotateImage(oneOpenPath[player], -90);
        }
        if (j - 1 >= 0 && board.getTile(i, j - 1).getType() != TileType.DEFAULT) {
            return FileHandler.rotateImage(oneOpenPath[player], 180);
        }
        if (j + 1 <= board.getColCount() - 1 && board.getTile(i, j + 1).getType() != TileType.DEFAULT) {
            return oneOpenPath[player];
        }
        return FileHandler.rotateImage(oneOpenPath[player], 90);
    }

    private BufferedImage twoConnectionCase(int player, int i, int j) {
        String direction = "";
        if (i - 1 >= 0 && board.getTile(i - 1, j).getType() != TileType.DEFAULT) {
            direction += "N";
        }
        if (i + 1 <= board.getColCount() - 1 && board.getTile(i + 1, j).getType() != TileType.DEFAULT) {
            direction += "S";
        }
        if (j - 1 >= 0 && board.getTile(i, j - 1).getType() != TileType.DEFAULT) {
            direction += "W";
        }
        if (j + 1 <= board.getColCount() - 1 && board.getTile(i, j + 1).getType() != TileType.DEFAULT) {
            direction += "E";
        }
        switch (direction) {
            case "NS" -> {return FileHandler.rotateImage(twoOpenPath[player], 90);}
            case "NE" -> {return coinPath[player];}
            case "NW" -> {return FileHandler.rotateImage(coinPath[player], -90);}
            case "SW" -> {return FileHandler.rotateImage(coinPath[player], -180);}
            case "SE" -> {return FileHandler.rotateImage(coinPath[player], 90);}        
            default -> {return twoOpenPath[player];}
        }
    }

    private BufferedImage threeConnectionCase(int player, int i, int j) {
        if (i - 1 >= 0 && board.getTile(i - 1, j).getType() == TileType.DEFAULT) {
            return FileHandler.rotateImage(threeConnect, 180);
        }
        if (j - 1 >= 0 && board.getTile(i, j - 1).getType() == TileType.DEFAULT) {
            return FileHandler.rotateImage(threeConnect, 90);
        }
        if (j + 1 <= board.getColCount() - 1 && board.getTile(i, j + 1).getType() == TileType.DEFAULT) {
            return FileHandler.rotateImage(threeConnect, -90);
        }
        return threeConnect;
    }


    public void setSpeedAnim(int speed) {
        this.speedAnim = speed;
    }
}
