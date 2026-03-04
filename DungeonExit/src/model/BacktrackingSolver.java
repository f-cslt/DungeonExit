package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.BacktrackingSolver.Placement;

/**
 * <p> The {@code BacktrackingSolver} class is used to find a solution to complete a level.
 * A solution is an arrangement of pieces picked from the given pieceBag. It starts from the ENTRY tile, reaching each
 * checkpoint until the final EXIT tile, completing the game.
 *
 * <p> It uses a naive backtracking algorithm, placing each piece from the pieceBag at the tip of the path until :
 * - no piece can be placed legally on the board, in which case it backtracks.
 * - the level is complete, in which case it returns the winning board
 */
public class BacktrackingSolver {

    private final static List<Tile.TileType> checkpointSequence = Arrays.asList(
        Tile.TileType.ENTRY,
        Tile.TileType.KEY,
        Tile.TileType.CHEST,
        Tile.TileType.DRAGON,
        Tile.TileType.EXIT
    );

  
    public static List<Placement> solve(List<Board> boards, int nbBoard, PieceBag pieceBag) {


        Set<Tile.TileType> checkpointTypes = EnumSet.of(
        Tile.TileType.ENTRY,
        Tile.TileType.KEY,
        Tile.TileType.CHEST,
        Tile.TileType.DRAGON,
        Tile.TileType.STAIRS,
        Tile.TileType.EXIT
        );
    
        // I don't make a snapshot of the pieceBag anymore because it make things harder afterwards
        // // We also copy the pieceBag in order to avoid damaging the original one
        // PieceBag copyPieceBag = new PieceBag(pieceBag);

        // Map <boardIndex, Map <which checkpoint, position> >  
        Map<Integer, Map<Tile.TileType, Point>> checkpoints = new HashMap<>();
        // We need to work on clean boards without any piece already placed 
        List<Board> copyBoards = new ArrayList<>();
        // Stores the path
        List<Placement> path = new ArrayList<>();
        // Build the sequence ENTRY -> KEY -> CHEST -> DRAGON -> EXIT
        List<Tile.TileType> seq = checkpointSequence;

        // We store checkpoints positions for each board
        for (int boardIndex = 0; boardIndex < nbBoard ; boardIndex++) {

            checkpoints.put(boardIndex, new HashMap<>()); 
            // Searching checkpoints position on the current board
            Board currentboard = boards.get(boardIndex);
            // Copy of the board that we will clean from existing paths
            copyBoards.add(new Board(currentboard));

            for (int i = 0; i < currentboard.getRowCount(); i++) {
                for (int j = 0; j < currentboard.getColCount(); j++) {

                    Point p = new Point(j, i); // x, y
                    Tile.TileType type = currentboard.getGrid()[i][j].getType(); // EMPTY, KEY, DRAGON, etc
                    
                    // More efficient than doing an if statement with 5 pipes
                    if (checkpointTypes.contains(type)){
                        checkpoints.get(boardIndex).put(type, p); // Add the checkpoint type and its position
                    }

                    // Not useful anymore. We clean everything before calling this function
                    // Cleaning the board's copy from existing paths
                    else if (type == Tile.TileType.PATH) {
                        copyBoards.get(boardIndex).getGrid()[i][j].setType(Tile.TileType.DEFAULT);
                    }
                }
            }
        }
        
        // Assume that ENTRY is on first board (and even EXIT on last board)
        Point entry = checkpoints.get(0).get(seq.get(0));

        // Now we have everything ready to start the backtack
        boolean found = backtrack(
            copyBoards,
            pieceBag,
            checkpoints,
            1,
            entry,
            path,
            0
        );

        // Avoid returning null if no path found. Empty list won't cause errors.
        return found ? path : Collections.emptyList();
    }



    private static boolean backtrack(
        List<Board>                                 boards,
        PieceBag                                    pieceBag,
        Map<Integer, Map<Tile.TileType,Point>>      cps,
        int                                         cpIdx,
        Point                                       current,
        List<Placement>                             path,
        int boardIndex
    ){

        List<Tile.TileType> seq = checkpointSequence;

        // Base case : end of sequence means we reached our final target
        if (cpIdx >= seq.size()) {
            return true;
        }

        Tile.TileType nextLogicalCp = seq.get(cpIdx);
        Tile.TileType targetType;
        if (cps.get(boardIndex).containsKey(nextLogicalCp)) {
            // This board actually has the checkpoint we want
            targetType = nextLogicalCp;
        } else {
            // Otherwise, we must first go to STAIRS on this board
            targetType = Tile.TileType.STAIRS;
        }

        Point target = cps.get(boardIndex).get(targetType);
        int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};

        // Base case: we connected to the next checkpoint
        boolean reachedCheckpoint = false;
        for (int[] dir : dirs){
            Point around = new Point(current.x + dir[0], current.y + dir[1]);
            if (around.equals(target)){
                reachedCheckpoint = true;
                break;
            }
        }

        if (reachedCheckpoint) {
            // If we reached a STAIRS, then move to other board
            if (targetType == Tile.TileType.STAIRS) {
                int otherBoard = (boardIndex + 1) % boards.size(); // toggle between 0 and 1
                Point stairsOther = cps.get(otherBoard).get(Tile.TileType.STAIRS);
                return backtrack(boards, pieceBag, cps, cpIdx, stairsOther, path, otherBoard);
            }
            // Otherwise we reached our next logical checkpoint (defined by seq)
            else if (targetType == nextLogicalCp) {
                // So start from the checkpoint we reached to the next one 
                return backtrack(boards, pieceBag, cps, cpIdx + 1, target, path, boardIndex);
            }
        }
    
        // For each piece still in the piece bag 
        // Need to make a snapshot to avoid ConcurrentModificationException
        for (Piece piece : new ArrayList<>(pieceBag.getPieces())) {
            // For each possible rotation of the piece
            for (int rot = 0; rot < 4; rot ++) {
                for (int[] dir : dirs) {
                    // We try to place the top left corner of the piece at these coordinates
                    int tryX = current.x + dir[0];
                    int tryY = current.y + dir[1];

                    if (boards.get(boardIndex).addPiece(piece, tryY, tryX)){

                        // Consequences of placing the piece on the board
                        path.add(new Placement(piece, new Point(tryX, tryY), boardIndex));
                        pieceBag.removePiece(piece);
                        pieceBag.increasePose();

                        // Compute next point : this is the end of the piece just placed
                        int[] lastOffset = piece.getShape().getLast();
                        // Position next = new Position(boardIndex, tryX + lastOffset[0], tryY + lastOffset[1]);
                        Point next = new Point(tryX + lastOffset[0], tryY + lastOffset[1]);

                        // Recurse from the new piece's end
                        if (backtrack(boards, pieceBag, cps, cpIdx, next, path, boardIndex)){
                            return true;
                        }

                        // Undo (backtracking)
                        boards.get(boardIndex).removePiece(piece);
                        pieceBag.addPiece(piece);
                        pieceBag.decreasePose();
                        path.removeLast();
                    }
                }
                piece.rotateRight();    // Or left whatever
            }
        }
        // No path found
        return false;
    }

    public static class Placement {
        private final Piece piece;
        private final Point origin; // (x, y)
        private final int boardIndex; // On which board do we place the piece

        public Placement(Piece piece, Point origin, int boardIndex) {
            this.piece  = piece;
            this.origin = origin;
            this.boardIndex = boardIndex;            
        }

        public int getBoardIndex()    { return boardIndex; }
        public Piece getPiece()    { return piece; }
        public Point getOrigin()   { return new Point(origin); }
    }
}
