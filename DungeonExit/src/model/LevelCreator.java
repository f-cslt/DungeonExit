package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import model.BacktrackingSolver.Placement;
import static utils.FileHandler.writeInMapFile;

public class LevelCreator {
    
    // Just for level
    private final List<Board> board;
    private int nbBoard;
    private int currentBoard;
    private final PieceBag pieceBag; 
    private final CreatorPieceBag fullPieceBag;



    public LevelCreator() {
        this.board = new ArrayList<>();
        this.pieceBag = new PieceBag(); // Empty piece bag
        this.fullPieceBag = new CreatorPieceBag(); 
        this.nbBoard = 1;
        this.currentBoard = 0;
        init();
    }


    private void init() {
        for (int i = 0; i < nbBoard; i++) {
            this.board.add(new Board()); // Empty board
        }
        
        // Put all different pieces in fullPieceBag
        for (PieceBlueprint pieceBlueprint : PieceBlueprint.values()) {
            boolean isBridge = (pieceBlueprint == PieceBlueprint.BRIDGE);
            fullPieceBag.addPiece(new Piece(pieceBlueprint, isBridge, 1));
        }
    }

    public void addCheckpointSelectionToPieceBag(Tile.TileType checkpoint) {
        this.pieceBag.setSelectedCheckpoint(checkpoint);
    }

    /**
     * Save the current level with a specified name
     * @param levelName The name to save the level with
     */
    public void save(String levelName) {
        writeInMapFile(levelName, fullPieceBag.getSelectedPieces(), board);
    }

    public void solve() {
        cleanBeforeSolve();
        List<Placement> solution = BacktrackingSolver.solve(board, nbBoard, pieceBag);
        if (solution.isEmpty()){
            System.out.println("No path found.");
            return;
        }
        // Put pieces on the boards
        buildSolution(solution);
    }

    private void buildSolution(List<Placement> solution) {
        for (Placement step : solution) {
            Board b = board.get(step.getBoardIndex());
            Point o = step.getOrigin();
            b.addPiece(step.getPiece(), o.y, o.x);
        }
    }

    public void cleanBeforeSolve() {
        for (Board b : board){
            for (int i = 0; i < b.getRowCount(); i++){
                for (int j = 0; j < b.getColCount(); j++){

                    // Clean grid
                    Tile.TileType type = b.getGrid()[i][j].getType();
                    if (type == Tile.TileType.PATH) {
                        b.getGrid()[i][j].setType(Tile.TileType.DEFAULT);
                    }

                    // Clean pieceGrid
                    Piece piece = b.getPieceGrid()[i][j];
                    if (piece != null) {
                        b.removePiece(piece);
                        pieceBag.addPiece(piece);
                        pieceBag.decreasePose();
                    }
                   
                    // Clean specialPieceGrid (BRIDGE)
                    Piece bridge = b.getSpecialPieceGrid()[i][j];
                    if (bridge != null){
                        b.removePiece(bridge);
                        pieceBag.addPiece(bridge);
                        pieceBag.decreasePose();
                    }
                }
            }
        }
    }

    public boolean checkWin() {
        // Check if all pieces are placed
        if (pieceBag.getNbPose() != pieceBag.getLimits()) {
            return false;
        }

        // The required sequence order
        Tile.TileType[] requiredSequence = {
            Tile.TileType.ENTRY,
            Tile.TileType.KEY,
            Tile.TileType.CHEST,
            Tile.TileType.DRAGON,
            Tile.TileType.EXIT
        };
        
        // Find all checkpoints on all boards
        Map<Integer, Map<Tile.TileType, Point>> checkpoints = new HashMap<>();
        
        // Collect all checkpoint positions for each board
        for (int boardIndex = 0; boardIndex < nbBoard; boardIndex++) {
            Board currBoard = board.get(boardIndex);
            Map<Tile.TileType, Point> boardCheckpoints = new HashMap<>();
            
            for (int i = 0; i < currBoard.getRowCount(); i++) {
                for (int j = 0; j < currBoard.getColCount(); j++) {
                    Tile.TileType type = currBoard.getTile(i, j).getType();
                    if (type == Tile.TileType.ENTRY || type == Tile.TileType.KEY ||
                        type == Tile.TileType.CHEST || type == Tile.TileType.DRAGON ||
                        type == Tile.TileType.EXIT || type == Tile.TileType.STAIRS) {
                        boardCheckpoints.put(type, new Point(j, i));
                    }
                }
            }
            
            checkpoints.put(boardIndex, boardCheckpoints);
        }
        
        // Find the board with ENTRY
        Integer startBoardIndex = null;
        for (Integer boardIndex : checkpoints.keySet()) {
            if (checkpoints.get(boardIndex).containsKey(Tile.TileType.ENTRY)) {
                startBoardIndex = boardIndex;
                break;
            }
        }
        
        if (startBoardIndex == null) {
            return false;
        }
        
        // Start checking the path
        return checkPath(startBoardIndex, checkpoints, 0, requiredSequence);
    }
    /**
     * Recursively checks if there's a valid path through the checkpoints
     * 
     * @param currentBoardIndex Current board we're checking
     * @param checkpoints Map of all checkpoints on all boards
     * @param sequenceIndex Current position in the required sequence
     * @param requiredSequence The required checkpoint sequence
     * @return true if a valid path exists
     */
    private boolean checkPath(int currentBoardIndex, 
                             Map<Integer, Map<Tile.TileType, Point>> checkpoints,
                             int sequenceIndex,
                             Tile.TileType[] requiredSequence) {
        
        // Base case: we've reached the end of the sequence
        if (sequenceIndex == requiredSequence.length - 1) {
            // Check if the last checkpoint (EXIT) exists on the current board
            return checkpoints.get(currentBoardIndex).containsKey(requiredSequence[sequenceIndex]);
        }
        
        Tile.TileType currentType = requiredSequence[sequenceIndex];
        Tile.TileType nextType = requiredSequence[sequenceIndex + 1];
        
        // Get the current checkpoint position
        Point currentPos = checkpoints.get(currentBoardIndex).get(currentType);
        
        // Check if the next checkpoint is on the current board
        if (checkpoints.get(currentBoardIndex).containsKey(nextType)) {
            Point nextPos = checkpoints.get(currentBoardIndex).get(nextType);
            
            // Check if there's a path between current and next checkpoint
            if (hasPath(board.get(currentBoardIndex), currentPos, nextPos)) {
                // Continue checking the sequence
                return checkPath(currentBoardIndex, checkpoints, sequenceIndex + 1, requiredSequence);
            }
        }
        
        // If the next checkpoint is not on this board or there's no direct path,
        // check if we can use STAIRS to go to the other board
        if (checkpoints.get(currentBoardIndex).containsKey(Tile.TileType.STAIRS)) {
            Point stairs = checkpoints.get(currentBoardIndex).get(Tile.TileType.STAIRS);
            
            // Check if there's a path to the STAIRS
            if (hasPath(board.get(currentBoardIndex), currentPos, stairs)) {
                // Find the other board with STAIRS
                for (Integer otherBoardIndex : checkpoints.keySet()) {
                    if (otherBoardIndex != currentBoardIndex && 
                        checkpoints.get(otherBoardIndex).containsKey(Tile.TileType.STAIRS)) {
                        
                        // Check if the next checkpoint is on the other board
                        if (checkpoints.get(otherBoardIndex).containsKey(nextType)) {
                            Point otherStairs = checkpoints.get(otherBoardIndex).get(Tile.TileType.STAIRS);
                            Point nextPos = checkpoints.get(otherBoardIndex).get(nextType);
                            
                            // Check if there's a path from the other stairs to the next checkpoint
                            if (hasPath(board.get(otherBoardIndex), otherStairs, nextPos)) {
                                // Continue checking the sequence on the other board
                                return checkPath(otherBoardIndex, checkpoints, sequenceIndex + 1, requiredSequence);
                            }
                        }
                    }
                }
            }
        }
        
        // If we couldn't find a valid path
        return false;
    }
    
    /**
     * Checks if there's a path between two points using BFS
     */
    private boolean hasPath(Board board, Point start, Point target) {
        if (start == null || target == null) {
            return false;
        }
        
        // Use BFS to find the path
        Queue<Point> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        
        // Add start point
        queue.add(start);
        visited.add(start);
        
        // Directions: up, down, left, right
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            
            // Check if we reached the target
            if (current.equals(target)) {
                return true;
            }
            
            // Try all four directions
            for (int[] dir : directions) {
                int newRow = current.y + dir[0];
                int newCol = current.x + dir[1];
                Point newPoint = new Point(newCol, newRow);
                
                // Check if this is a valid point to move to
                if (isValidPathPoint(board, newRow, newCol, visited)) {
                    visited.add(newPoint);
                    queue.add(newPoint);
                }
            }
        }
        
        // If we didn't find the target
        return false;
    }
    
    /**
     * Checks if a point is valid for movement in the path-finding algorithm.
     */
    private boolean isValidPathPoint(Board board, int row, int col, Set<Point> visited) {
        // Check bounds
        if (row < 0 || row >= board.getRowCount() || col < 0 || col >= board.getColCount()) {
            return false;
        }
        
        Point point = new Point(col, row);
        
        // Skip if already visited
        if (visited.contains(point)) {
            return false;
        }
        
        // Check tile type
        Tile.TileType type = board.getTile(row, col).getType();
        
        // Valid tiles to move through are PATH tiles or checkpoint tiles
        return type == Tile.TileType.PATH || 
               type == Tile.TileType.ENTRY ||
               type == Tile.TileType.KEY ||
               type == Tile.TileType.CHEST ||
               type == Tile.TileType.DRAGON ||
               type == Tile.TileType.EXIT ||
               type == Tile.TileType.STAIRS;
    }



















    public void handlePieceSelection(Piece piece) {

        if (fullPieceBag.getSelectedPieces().contains(piece)) {
            if (pieceBag.isPieceInPieceBag(piece)){
                fullPieceBag.removeSelectedPiece(piece);
                pieceBag.removePiece(piece);
            }
        }else {
            fullPieceBag.addSelectedPiece(piece);
            pieceBag.addPiece(piece);
        }
    }



    public void changeLevel(int index) {
        if (index < 0 || index >= nbBoard)
            return ;
        currentBoard = index;
    }
    
    public void addBoard() {
        if (nbBoard < 2) {
            this.board.add(new Board()); // Add empty board
            this.nbBoard ++;            
        }
    }

    public void removeBoard() {
        if (nbBoard > 1) {
            this.board.removeLast();
            this.nbBoard --;
            this.currentBoard = 0;
        }
    }

    public Board getBoard(int index) {
        return board.get(index);
    }

    public List<Board> getBoard() {
        return board;
    }

    public int getNbBoard() {
        return nbBoard;
    }

    public void setNbBoard(int nbBoard) {
        this.nbBoard = nbBoard;
    }

    public int getCurrentBoard() {
        return currentBoard;
    }

    public void setCurrentBoard(int currentBoard) {
        this.currentBoard = currentBoard;
    }

    public PieceBag getPieceBag() {
        return pieceBag;
    }

    public PieceBag getFullPieceBag() {
        return fullPieceBag;
    }


    public class CreatorPieceBag extends PieceBag {

        private final Set<Piece> selectedPieces;

        public CreatorPieceBag() {
            super();
            this.selectedPieces = new HashSet<>();
        }

        public void addSelectedPiece(Piece selectedPiece) {
            if (!selectedPieces.contains(selectedPiece)) {
                this.selectedPieces.add(selectedPiece);
            }
        }

        public void removeSelectedPiece(Piece selectedPiece) {
            if (selectedPieces.contains(selectedPiece)) {
                this.selectedPieces.remove(selectedPiece);
            }
        }

        public Set<Piece> getSelectedPieces() {
            return selectedPieces;
        }

    }
}
