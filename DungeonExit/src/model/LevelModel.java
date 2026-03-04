package model;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import model.BacktrackingSolver.Placement;
import static utils.FileHandler.*;



/**
 * The LevelModel class is responsible for managing the level of the game. It handles loading
 * and parsing the map, as well as managing the game board and the collection of puzzle pieces
 * (represented by a PieceBag). It works with the MapParser to load map data from a file.
 */
public class LevelModel {

    private ArrayList<Board> board;
    private int nbBoard;
    private int currentBoard;
    private PieceBag pieceBag;
     
    // Timer variables
    private long startTime;
    private long elapsedTime; // Stored in seconds

    // Best time variables
    private String bestTime;
    private File currentLevelFile;
    private int highscore;

    /**
     * Creates a level based on a txt file configuration.
     * It initializes the board and the piece bag. 
     * 
     * @param path the path of the file config
     */
    public void initLevelModels(File path){
        currentLevelFile = path;
        ArrayList<char[][]> map;
        board = new ArrayList<>();
        try {
            // Load best time
            try {
                bestTime = getBestTimeFromFile(path);
                highscore = getHighscore(path);
            } catch (FileNotFoundException e) {
                bestTime = "";
            }
            map = parseMap(path);
            this.pieceBag = parsePieceBag(path);
            for (@SuppressWarnings("unused") Piece piece : pieceBag.getPieces()) {
            }
            for (int i = 0; i < map.size(); i++) {
                this.board.add(new Board(map.get(i)));
            }
            nbBoard = map.size();
            currentBoard = 0;
            // Initialize timer
            startTime = System.currentTimeMillis();
            elapsedTime = 0;

        } catch (FileNotFoundException e) {}  
    }

    private int getHighscore(File path) throws FileNotFoundException {
        Scanner scanner = new Scanner(path);
        String line;

        line = scanner.nextLine();
        return Integer.parseInt(line.split(":")[1]);
    }

    public void changeLevel(int index) {
        if (index < 0 || index >= nbBoard)
            return ;
        currentBoard = index;
    }



     /**
     * Updates the level model. This method can be used for updating game-specific logic or mechanics
     * related to the level, although currently it does not perform any actions.
     */
    public void update() {
        // Update the elapsed time
        if (startTime > 0) {
            elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        }
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

    private void buildSolution(List<Placement> solution) {
        for (Placement step : solution) {
            Board b = board.get(step.getBoardIndex());
            Point o = step.getOrigin();
            b.addPiece(step.getPiece(), o.y, o.x);
        }
    }

     /**
     * Compare current time with the best time and return true if current time
     * if better (i.e smaller)
     * 
     * @param currentTime   current time in format MM:SS
     * @param bestTime      best time in format MM:SS
     * @return              true if currentTime < bestTime
     */
    public static boolean isNewBestTime(String currentTime, String bestTime) {
        // If the best time is empty, the current time is always better
        if (bestTime == null || bestTime.isEmpty()) {
            return true;
        }
        
        // Converts time in seconds
        int currentSeconds = timeToSeconds(currentTime);
        int bestSeconds = timeToSeconds(bestTime);
        
        // Compare seconds
        return currentSeconds < bestSeconds;
    }
    
    /**
     * Converts a time in MM:SS format into seconds.   
     * 
     * @param time  Time in format MM:SS
     * @return      Time in seconds
     */
    private static int timeToSeconds(String time) {
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    /**
     * Resets the timer for the level.
     */
    public void resetTimer() {
        startTime = System.currentTimeMillis();
        elapsedTime = 0;
    }

    /**
     * Checks if the current time is better than the best time and updates it if necessary.
     * 
     * @return true if a new best time was set, false otherwise
     */
    public boolean checkAndUpdateBestTimeAndHighscore() {
        String currentTime = getFormattedTime();
        
        if (isNewBestTime(currentTime, bestTime)) {
            // Update the best time in memory
            bestTime = currentTime;
        }
        if (pieceBag.getNbPose() < highscore || highscore == 0) {
            highscore = pieceBag.getNbPose();
        }
        // Update the best time in the file
        try {
            updateBestTimeAndHighscore(currentLevelFile, bestTime, highscore);
            return true;
        } catch (IOException e) {
        }
        return false;
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







    /*****************************************************************************
     *                                                                           *
     *                                                                           *
     *                        GETTERS / SETTERS                                  *
     *                                                                           *
     *                                                                           *
     *****************************************************************************/




    /**
     * Retrieves the current Board object, representing the game board for this level.
     *
     * @return the current board
     */
    public Board getBoard(int index) {
        return board.get(index);
    }

    public ArrayList<Board> getBoard() {
        return board;
    }

    public int getCurrentBoard() {
        return currentBoard;
    }

    public int getNbBoard() {
        return nbBoard;
    }

    /**
     * Retrieves the current PieceBag, which contains the collection of puzzle pieces available for use
     * in the level.
     *
     * @return the current PieceBag
     */
    public PieceBag getPieceBag() {
        return pieceBag;
    }

    /**
     * Gets the elapsed time in seconds since the level was initialized.
     * 
     * @return The elapsed time in seconds
     */
    public long getElapsedTimeInSeconds() {
        return elapsedTime;
    }
    
    /**
     * Gets the formatted time string in MM:SS format.
     * 
     * @return The formatted time string
     */
    public String getFormattedTime() {
        long minutes = elapsedTime / 60;
        long seconds = elapsedTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Gets the best time for this level.
     * 
     * @return The best time in MM:SS format, or empty string if no best time exists
     */
    public String getBestTime() {
        return bestTime;
    }

}