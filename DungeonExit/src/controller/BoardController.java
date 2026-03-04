package controller;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import model.Board;
import model.Piece;
import model.PieceBag;
import model.PlacementPreview;
import model.Tile;
import utils.KeyAdapter;
import utils.MouseAdapter;
import utils.SoundManager;
import view.BoardView;
import view.PieceBagView;

class Pair {
    public boolean key;
    public String value;

    public Pair(boolean bool, String string) {
        this.key = bool;
        this.value = string;
    }

    public String getValue() {
        return value;
    }

    public boolean getKey() {
        return key;
    }

}

/**
 * The controller for the game board interface. It handles mouse events and
 * interacts with the game model data. This class implements the
 * {@link MouseListener} interface to listen to mouse events on the board,
 * allowing the placement of pieces onto the {@link Board} model.
 *
 * The class manages interaction with the board, specifically placing pieces
 * from the {@link PieceBag} onto the {@link Board} via mouse clicks.
 *
 * When a piece is selected from the {@link PieceBag}, a mouse click on the
 * board attempts to place this piece at the corresponding location. If the
 * placement is valid, the piece is placed on the board and removed from the
 * piece bag.
 *
 * @see MouseListener
 * @see Board
 * @see PieceBag
 * @see BoardView
 */
public class BoardController implements MouseAdapter, KeyAdapter {

    /**
     * The horizontal offset (in grid columns) between the mouse click position
     * and the top-left corner of the dragged puzzle piece. This ensures the
     * piece moves relative to where it was clicked, not just its top-left
     * corner.
     */
    private int dragOffsetX;

    private Color player1Color = new Color(21,151,209);
    private Color player2Color = new Color(54,171,113);

    /**
     * The vertical offset (in grid rows) between the mouse click position and
     * the top-left corner of the dragged puzzle piece. This ensures the piece
     * moves relative to where it was clicked, not just its top-left corner.
     */
    private int dragOffsetY;

    /**
     * The model representing the game board.
     */
    private final List<Board> board;

    /**
     * The view representing the display of the game board.
     */
    private final List<BoardView> boardView;

    private int currentBoard;
    /**
     * The piece bag, representing the pieces that the user can place on the
     * board.
     */
    private final PieceBag pieceBag;

    private final PieceBagView pieceBagView;

    private PieceBag[] pieceBagMult;
    private PieceBagView[] pieceBagViewMult;

    private boolean multEnable;

    private Thread animThread;

    private boolean animPlayer;

    private int currentPlayer;

    /**
     * Constructor for the controller. Initializes references to the
     * {@link Board}, {@link PieceBag} models and the {@link BoardView}. It also
     * registers this controller as a listener for mouse events on the
     * {@link BoardView}.
     *
     * @param board The game board model.
     * @param boardView The game board view.
     * @param pieceBag The piece bag containing selectable pieces.
     */
    public BoardController(List<Board> board, List<BoardView> boardView, PieceBag pieceBag, PieceBagView pieceBagView) {
        this.board = board;
        this.boardView = boardView;
        this.pieceBag = pieceBag;
        this.pieceBagView = pieceBagView;

        this.multEnable = false;
        initializeListener();
    }

    private void initializeListener() {
        // Register the controller to listen for mouse events on the BoardView
        for (int i = 0; i < boardView.size(); i++) {
            boardView.get(i).addMouseListener(this); // for mouseClicked etc
            boardView.get(i).addMouseMotionListener(this); // for mouseDrag/ mouseMoved...
            boardView.get(i).addKeyListener(this);
            boardView.get(i).addMouseWheelListener(this);
        }
    }

    public void addListeners(){
        boardView.getLast().addMouseListener(this); // for mouseClicked etc
        boardView.getLast().addMouseMotionListener(this); // for mouseDrag/ mouseMoved...
        boardView.getLast().addKeyListener(this);
        boardView.getLast().addMouseWheelListener(this);
    }

    public void removeListeners(){
        boardView.getLast().removeMouseListener(this); // for mouseClicked etc
        boardView.getLast().removeMouseMotionListener(this); // for mouseDrag/ mouseMoved...
        boardView.getLast().removeKeyListener(this);
        boardView.getLast().removeMouseWheelListener(this);
    }

    public BoardController(ArrayList<Board> board, ArrayList<BoardView> boardView, PieceBag pieceBag1, PieceBag pieceBag2,
        PieceBagView pieceBagView1, PieceBagView pieceBagView2) {

        this.multEnable = true;

        this.board = board;
        this.boardView = boardView;
        this.pieceBag = pieceBag1;
        this.pieceBagView = pieceBagView1;

        this.pieceBagMult = new PieceBag[2];
        this.pieceBagViewMult = new PieceBagView[2];
        this.pieceBagMult[0] = pieceBag1;
        this.pieceBagMult[1] = pieceBag2;

        this.pieceBagViewMult[0] = pieceBagView1;
        this.pieceBagViewMult[1] = pieceBagView2;
        for (int i = 0; i < boardView.size(); i++) {
            boardView.get(i).addMouseListener(this); // for mouseClicked etc
            boardView.get(i).addMouseMotionListener(this); // for mouseDrag/ mouseMoved...
            boardView.get(i).addKeyListener(this);
            boardView.get(i).addMouseWheelListener(this);
        }

        this.currentPlayer = 0;
    }

    public void resetAnim(){
        
    }
        
    /**
     * Method called to rotate a Piece selected in the piece bag.
     * It will delegates the rotation to the {@link rotatePiece}
     * 
     */
    private void rotateSelectedPiece(int keyCode) {
        Piece selectedPiece = (multEnable) ? pieceBagMult[currentPlayer].getSelectedPiece() : pieceBag.getSelectedPiece();
        if (selectedPiece != null) {
            rotatePiece(selectedPiece, keyCode);
            updatePreviewValidity();
        }
    }

    private int getAdvance(String s) {
        int advance = 0;
        for (char c : s.toCharArray()) {
            if (c == 'k' && advance == 0) {
                advance++;
            }
            if (c == 'c' && advance == 1) {
                advance++;
            }
            if (c == 'd' && advance == 2) {
                advance++;
            }
        }
        return advance;
    }

    public boolean checkWinCondition(Thread thread, int player) {
        int startBoard = 0;

        Point entryPoint = board.get(startBoard).getEntryPos(player);
        if (entryPoint == null) {
            entryPoint = board.get(++startBoard).getEntryPos(player);
        }
        Pair pathString = getPlayerPath(player, new Point(entryPoint.x, entryPoint.y), new Point(-1, -1), startBoard, "");

        // System.out.println("Valid: " + pathString.getKey() +" PlayerPath: " + pathString.getValue());
        makeAnimPlayer(thread, player, entryPoint, startBoard, pathString.getValue());
        return pathString.getKey();
    }
 
    private void makeAnimPlayer(Thread thread, int player, Point entry, int indexBoard, String path) {
        final int tilesize = boardView.get(indexBoard).getTileSize();
        boardView.get(indexBoard).setPlayerPos(
            new Point((int)entry.getX() * tilesize, (int)entry.getY() * tilesize));

        animPlayer = true;
        Point pos = entry;
        int index = indexBoard;
        if (path.contains("s")) {
            String[] multiPath = path.split("s");
            for (String s : multiPath) {
                boardView.get(index).makeAnimPlayer(animThread, s, getAdvance(path));
                index = (index == 0) ? 1 : 0;
                pos = board.get(index).getStairsPos();
                boardView.get(index).setPlayerPos(new Point(pos.x * tilesize, pos.y * tilesize));
            }
        } else {
            boardView.get(indexBoard).makeAnimPlayer(animThread, path, getAdvance(path));
        }
        animPlayer = false;
        for (BoardView b : boardView) {
            b.stopAnim();
        }
    }

    private Pair getPlayerPath(int player, Point pos, Point prev, int indexBoard, String path) {
        String nextTile;

        Piece isBridge = board.get(indexBoard).getPieceFromSpecialPieceGridAt(pos.x, pos.y);
        if (isBridge != null && isBridge.isBridge()) {
            nextTile = board.get(indexBoard).getNextTileBridge(player, pos, prev);
        } else {
            nextTile = board.get(indexBoard).getNextTile(player, pos, prev);
        }

        Point newPose = null;
        switch (nextTile) {
            case "L" -> {newPose = new Point(pos.x, pos.y - 1);}
            case "R" -> {newPose = new Point(pos.x, pos.y + 1);}
            case "U" -> {newPose = new Point(pos.x - 1, pos.y);}
            case "D" -> {newPose = new Point(pos.x + 1, pos.y);}
        }
        
        if (newPose == null) {
            return new Pair(checkPlayerPath(path), path);
        }
        String stringCheck = board.get(indexBoard).getStringCheckPoint(newPose);
        if (board.get(indexBoard).getTile(newPose.x, newPose.y).getType() == Tile.TileType.STAIRS) {
            return stairsTouch(player, board.get((indexBoard == 1) ? 0 : 1).getStairsPos(), (indexBoard == 1) ? 0 : 1, path + nextTile + stringCheck);
        }
        return getPlayerPath(player, newPose, pos, indexBoard, path + nextTile + stringCheck);
    }

    private boolean checkPlayerPath(String path) {
        boolean[] checkpoint = {false, false, false};
        for (char c : path.toCharArray()) {
            switch (c) {
                case 'k' -> {checkpoint[0] = true;}
                case 'c' -> {checkpoint[1] = checkpoint[0];}
                case 'd' -> {checkpoint[2] = checkpoint[1];}
                case 'e' -> {return checkpoint[2];}
            }
        }
        return false;
    }

    private Pair stairsTouch(int player, Point pos, int indexBoard, String path) {
        String connectStairs = board.get(indexBoard).getNextTileStairs(player, pos);
        Point newPose1 = null;
        Point newPose2 = null;

        switch (connectStairs) {
            case "L" -> {newPose1 = new Point(pos.x, pos.y - 1);}
            case "R" -> {newPose1 = new Point(pos.x, pos.y + 1);}
            case "U" -> {newPose1 = new Point(pos.x - 1, pos.y);}
            case "D" -> {newPose1 = new Point(pos.x + 1, pos.y);}
            case "UL" -> {newPose1 = new Point(pos.x - 1, pos.y);
                newPose2 = new Point(pos.x, pos.y - 1);}
            case "UR" -> {newPose1 = new Point(pos.x - 1, pos.y);
                newPose2 = new Point(pos.x, pos.y + 1);}
            case "UD" -> {newPose1 = new Point(pos.x - 1, pos.y);
                newPose2 = new Point(pos.x + 1, pos.y);}
            case "DL" -> {newPose1 = new Point(pos.x + 1, pos.y);
                newPose2 = new Point(pos.x, pos.y - 1);}
            case "DR" -> {newPose1 = new Point(pos.x + 1, pos.y);
                newPose2 = new Point(pos.x, pos.y + 1);}
            case "LR" -> {newPose1 = new Point(pos.x, pos.y - 1);
                newPose2 = new Point(pos.x, pos.y + 1);}
        }
        if (newPose1 == null) {
            return new Pair(checkPlayerPath(path), path);
        }
        if (newPose2 == null) {
            return getPlayerPath(player, newPose1, pos, indexBoard, path + connectStairs);
        }
        Pair p1 = getPlayerPath(player, newPose1, pos, indexBoard, path + connectStairs.toCharArray()[0]);
        if (p1.getKey()) {
            return p1;
        }
        return getPlayerPath(player, newPose2, pos, indexBoard, path + connectStairs.toCharArray()[1]);
    }

    /*
     * Method called to rotate a piece while being dragged.
     */
    private void rotateDraggedPiece(int keyCode) {
        Piece draggedPiece = board.get(currentBoard).getPreview().getSelectedPiece();
        if (draggedPiece != null) {
            rotatePiece(draggedPiece, keyCode);
            updatePreviewValidity();
        }
    }

    public void setCurrentPlayer(int p) {
        this.currentPlayer = p;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Rotates a puzzle piece from its position to 90° left or right
     * depending on the input.
     * 
     * @param piece     the piece to rotate
     * @param keyCode   left arrow to rotate 90° to left and same for right with right arrow
     * 
     */
    private void rotatePiece(Piece piece, int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT) {
            piece.rotateLeft();
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            piece.rotateRight();
        }
    }

    private void rotatePieceMouse(Piece piece, int mouseCode) {
        if (mouseCode == 0) {
            piece.rotateLeft();
        } else if (mouseCode == 1) {
            piece.rotateRight();
        }
    }

    /**
     * Calls the solve() method of BacktrackingSolver
     */
    public void solve() {
        // BacktrackingSolver.solve(board.get(currentBoard), pieceBag);
    }

    /**
     * Updates the preview after rotation
     */
    private void updatePreviewValidity() {
        PlacementPreview preview = board.get(currentBoard).getPreview();
        if (preview != null) {
            boolean isValid = board.get(currentBoard).canPlacePiece(
                preview.getSelectedPiece(),
                preview.getRow(),
                preview.getCol()
            );
            board.get(currentBoard).getPreview().update(preview.getRow(), preview.getCol(), isValid);
        }
    }

    public void setCurrentBoard(int index, int nbBoard) {
        if (index >= nbBoard || index < 0)
            return ;
        this.currentBoard = index;
    }
    

    /**
     * Useful method to compute the row and col in the grid from x and y mouse
     * coordinates.
     *
     * @param mouseX mouse's x coordinate
     * @param mouseY mouse's y coordinate
     * @return a Point where x is col and y is row
     */
    private Point convertMouseToGrid(int mouseX, int mouseY) {
        double tileSize = boardView.get(currentBoard).getTileSize();
        int col = (int) (mouseX / tileSize);
        int row = (int) (mouseY / tileSize);
        return new Point(col, row);
    }

    /**
     * Called when the mouse is moving in the board. It is used to get the
     * coordinates of the mouse to display a preview of the piece at this
     * position.
     *
     * @param e the mouse event
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (animPlayer) {
            return ;
        }
        boolean isSelect = (multEnable) ? pieceBagMult[currentPlayer].isPieceSelected() : pieceBag.isPieceSelected();
        if (isSelect && !board.get(currentBoard).isDragging()) {
            PlacementPreview currentPreview = board.get(currentBoard).getPreview();
            Point gridPos = convertMouseToGrid(e.getX(), e.getY());
            // We don't need to change the preview if it is the same as the current one
            if (currentPreview == null || currentPreview.getRow() != gridPos.y || currentPreview.getCol() != gridPos.x) {
                Piece selectedPiece = (multEnable) ? pieceBagMult[currentPlayer].getSelectedPiece() : pieceBag.getSelectedPiece();
                boolean isValid = board.get(currentBoard).canPlacePiece(selectedPiece, gridPos.y, gridPos.x); // Will show preview in red if false
                board.get(currentBoard).setPlacementPreview(gridPos.y, gridPos.x, isValid, selectedPiece);
            }
        }
    }

    /**
     * Called when a mouse click event occurs. This method attempts to place the
     * selected piece at the clicked location on the board. If the placement is
     * valid, the piece is placed on the board and removed from the piece bag.
     *
     * @param e The mouse event containing the click position.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (animPlayer) {
            return ;
        }
        Point gridPos = convertMouseToGrid(e.getX(), e.getY());
        switch (e.getButton()) {
            // Left click to put down a puzzle piece on the board
            case MouseEvent.BUTTON1 -> {
                boolean isSelect = (multEnable) ? pieceBagMult[currentPlayer].isPieceSelected(): pieceBag.isPieceSelected();
                if (isSelect) {
                    Piece selectPiece = (multEnable) ? pieceBagMult[currentPlayer].getSelectedPiece() : pieceBag.getSelectedPiece();
                    if (board.get(currentBoard).addPiece(selectPiece, gridPos.y, gridPos.x) && (pieceBag.checkNbPiece() || multEnable)) {
                        if (multEnable) {
                            pieceBagMult[currentPlayer].removeSelectedPiece();
                            pieceBagMult[currentPlayer].increasePose();
                            board.get(currentBoard).clearPreview();
                            pieceBagViewMult[currentPlayer].draw();
                        } else {
                            pieceBag.removeSelectedPiece();
                            pieceBag.increasePose();
                            board.get(currentBoard).clearPreview();
                            pieceBagView.draw();
                        }
                            // Play click sound when piece is successfully placed
                        SoundManager.getInstance().playSoundEffect("piece_placement");
                    }
                }
                else if (pieceBag.isCheckpointSelected()) {
                    Tile.TileType cp = pieceBag.getSelectedCheckpoint();
                    // Maybe here we need to add an if statement isCheckpointAlreadyPlacedOnBoard

                    if (board.get(currentBoard).addCheckpointToBoard(cp, gridPos.y, gridPos.x)){
                        pieceBag.removeSelectedCheckpoint();
                    }
                }


                
            }
            // Right click to remove a piece
            case MouseEvent.BUTTON3 -> {
                    //In this prototype we can remove a piece by right clicking on it.
                Piece clickedPiece = board.get(currentBoard).getPieceAt(gridPos.y, gridPos.x);
                // If the right click is actually on a piece
                if (clickedPiece != null) {
                    if (multEnable && (clickedPiece.getPlayer() != currentPlayer + 1)) {
                        return ;
                    }
                    board.get(currentBoard).removePiece(clickedPiece);
                    clickedPiece.resetPiece();
                    if (multEnable) {
                        pieceBagMult[currentPlayer].addPiece(clickedPiece);
                        pieceBagMult[currentPlayer].decreasePose();
                        pieceBagViewMult[currentPlayer].draw();
                    } else {
                        pieceBag.addPiece(clickedPiece);
                        pieceBag.decreasePose();
                        pieceBagView.draw();
                    }
                }

                Piece selectPiece = (multEnable) ? pieceBagMult[currentPlayer].getSelectedPiece() : pieceBag.getSelectedPiece();
                if (selectPiece != null) {
                    if (multEnable) {
                        pieceBagMult[currentPlayer].setSelectedPiece(null);
                    } else {
                        pieceBag.setSelectedPiece(null);
                    }
                    board.get(currentBoard).clearPreview();
                }
            }
        }
    }

    /**
     * Called when a mouse button is pressed. Used to move a puzzle piece in the
     * grid by pressing left clicking on it.
     *
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (animPlayer) {
            return ;
        }
        if (MouseEvent.BUTTON1 == e.getButton()) {
            // 1. Detect which piece we need to move.
            Point gridPos = convertMouseToGrid(e.getX(), e.getY());
            Piece clickedPiece = board.get(currentBoard).getPieceAt(gridPos.y, gridPos.x);

            // If there is a piece where the clicked
            if (clickedPiece != null) {
                Point origin = board.get(currentBoard).getPieceOrigin(clickedPiece);
                if (origin != null && (!multEnable || (multEnable && (clickedPiece.getPlayer() == currentPlayer + 1)))) {
                    // Compute offset (relative position of the tile in the puzzle piece)
                    dragOffsetX = gridPos.x - origin.x; // Columns offset 
                    dragOffsetY = gridPos.y - origin.y; // Rows offset
                    board.get(currentBoard).startDrag(clickedPiece, origin.y, origin.x);
                    board.get(currentBoard).removePiece(clickedPiece);
                }
            }
        }
    }

    /**
     * Called when the mouse exits the component.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (animPlayer) {
            return ;
        }
        if (board.get(currentBoard).isDragging()) {
            // Mark preview as invalid (out of bounds)
            board.get(currentBoard).getPreview().update(
                    board.get(currentBoard).getPreview().getRow(),
                    board.get(currentBoard).getPreview().getCol(),
                    false // Invalid position
            );
        } else {
            board.get(currentBoard).clearPreview();
        }
    }

    /**
     * Called when the mouse is dragging. Used to move a piece.
     *
     * @param e the mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (animPlayer) {
            return ;
        }
        if (board.get(currentBoard).isDragging()) {
            Point gridPos = convertMouseToGrid(e.getX(), e.getY());
            // Compute the new origin relative to the offset
            int newOriginCol = gridPos.x - dragOffsetX;
            int newOriginRow = gridPos.y - dragOffsetY;
            boolean isValid = board.get(currentBoard).canPlacePiece(board.get(currentBoard).getPreview().getSelectedPiece(), newOriginRow, newOriginCol);
            board.get(currentBoard).getPreview().update(newOriginRow, newOriginCol, isValid);
        }
    }

    /**
     * Called when the mouse button is released. Used to confirm the position of
     * the piece after moving it.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (animPlayer) {
            return ;
        }
        if (board.get(currentBoard).isDragging()) {
            // 4. Confirm or cancel moving the piece
            PlacementPreview preview = board.get(currentBoard).getPreview();
            if (preview.isValid()) {
                board.get(currentBoard).addPiece(board.get(currentBoard).getPreview().getSelectedPiece(), preview.getRow(), preview.getCol());
                // Play click sound when piece is successfully placed after dragging
                SoundManager.getInstance().playSoundEffect("piece_placement");
            } // If the moving of the puzzle piece is cancelled, then the piece is put back in its original place.
            else {
                // Restore to original position before drag
                board.get(currentBoard).restoreOriginalPiece();
                board.get(currentBoard).addPiece(board.get(currentBoard).getPreview().getSelectedPiece(), 
                    board.get(currentBoard).getOriginalDragRow(), board.get(currentBoard).getOriginalDragCol());
            }
            board.get(currentBoard).clearPreview();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
            boolean isSelect = (multEnable) ? pieceBagMult[currentPlayer].isPieceSelected() : pieceBag.isPieceSelected();
            if (isSelect) {
                rotateSelectedPiece(keyCode);
            } else if (board.get(currentBoard).isDragging()) {
                rotateDraggedPiece(keyCode);
            }
        }
        if (keyCode == KeyEvent.VK_ENTER) {
            //checkWinCondition(0);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (animPlayer) {
            return ;
        }
        Piece selectPiece = (multEnable) ? pieceBagMult[currentPlayer].getSelectedPiece() : pieceBag.getSelectedPiece();
        if (selectPiece == null) {
            if (board.get(currentBoard).getPreview() != null)
                selectPiece = board.get(currentBoard).getPreview().getSelectedPiece();
        }
        if (selectPiece == null) {
            return ;
        }
        if (e.getWheelRotation() < 0) {
            rotatePieceMouse(selectPiece, 0);
        } else {
            rotatePieceMouse(selectPiece, 1);
        }
    }
}
