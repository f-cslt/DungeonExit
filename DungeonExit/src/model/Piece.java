package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The Piece class represents a puzzle piece used in the game. It defines the piece's path based on a blueprint
 * and allows rotating the piece in either direction (left or right). The piece's movement is tracked as a list of directions.
 */
public class Piece {

    /**
     * The Direction enum defines the possible directions a piece can move.
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private List<int[]> shape;  // Stores the shape of the piece
    private List<int[]> baseShape;
    private ArrayList<Direction> track;
    private ArrayList<Direction> baseTrack;
    private PieceBlueprint id;
    private final String uuid;

    private int player;

    private final boolean isBridge;

    public Piece(PieceBlueprint id, int player) {
        this(id, false, player); // Default constructor
    }

    // New constructor
    public Piece(PieceBlueprint id, boolean isBridge, int player) {
        track = new ArrayList<>();
        this.id = id;
        this.track = computeTrackFromBlueprint(id.getBlueprint());
        this.baseTrack = track;
        this.shape = computeShapeFromTrack();
        this.baseShape = shape;
        // Each piece has an id to prevent removing the wrong piece
        this.uuid = UUID.randomUUID().toString(); 

        this.player = player;
        this.isBridge = isBridge;
    }

    public boolean isBridge() {
        return isBridge;
    }

    public int getPlayer() {
        return player;
    }
    
    public void setPlayer(int p) {
        this.player = p;
    }


    // /**
    //  * Constructs a Piece from a blueprint, which is a 2D grid of 0s and 1s representing the piece's path.
    //  * A 1 represents a valid path, and the piece's movement is determined by following the path in the blueprint.
    //  * The directions of the piece's path are stored in the track.
    //  *
    //  * @param blueprint a 2D array (grid) of integers (0 and 1) representing the path of the piece.
    //  */
    // public Piece(int[][] blueprint) {
    //     track = new ArrayList<>();
    //     this.track = computeTrackFromBlueprint(blueprint);
    //     this.shape = computeShapeFromTrack();
    //     // Each piece has an id to prevent removing the wrong piece
    //     this.uuid = UUID.randomUUID().toString(); 
    // }

    // Other constructor used to make a copy of a piece with a different id
    public Piece(Piece other) {
        this.shape = new ArrayList<>(other.shape);
        this.track = new ArrayList<>(other.track);
        this.baseShape = shape;
        this.baseTrack = track;
        this.player = other.player;
        this.uuid = UUID.randomUUID().toString(); // New ID
        this.id = other.id;
        
        this.isBridge = other.isBridge();
    }

    private List<int[]> computeShapeFromTrack() {
        List<int[]> shape = new ArrayList<>();
        int x = 0, y = 0;
        shape.add(new int[]{x, y});
        
        for (Direction dir : track) {
            switch (dir) {
                case UP -> y--;
                case DOWN -> y++;
                case LEFT -> x--;
                case RIGHT -> x++;
            }
            shape.add(new int[]{x, y});
        }
        return shape;
    }

    private ArrayList<Direction> computeTrackFromBlueprint(int[][] blueprint) {
        track = new ArrayList<>();
        int row = 0;
        int col = 0;
        // Traverse the blueprint to create a track for the piece
        while (true) {
            if (col + 1 < blueprint[0].length && blueprint[row][col + 1] == 1) {
                blueprint[row][col + 1] = 0;
                track.add(Direction.RIGHT);
                col++;
            } else if (row + 1 < blueprint.length && blueprint[row + 1][col] == 1) {
                blueprint[row + 1][col] = 0;
                track.add(Direction.DOWN);
                row++;
            } else if (row - 1 >= 0 && blueprint[row - 1][col] == 1) {
                blueprint[row - 1][col] = 0;
                track.add(Direction.UP);
                row--;
            } else {
                break;
            }
        }
        return track;
    }

    /**
     * Rotates the piece 90 degrees to the left. The directions in the track are modified accordingly.
     * The left rotation works as follows:
     * - UP -> LEFT
     * - DOWN -> RIGHT
     * - LEFT -> DOWN
     * - RIGHT -> UP
     */
    public void rotateLeft() {
        ArrayList<Direction> newTrack = new ArrayList<>();
        for (Direction d : track) {
            switch (d) {
                case UP -> newTrack.add(Direction.LEFT);
                case DOWN -> newTrack.add(Direction.RIGHT);
                case LEFT -> newTrack.add(Direction.DOWN);
                case RIGHT -> newTrack.add(Direction.UP);
            }
        }
        track = newTrack;
        shape = computeShapeFromTrack();
    }

    /**
     * Rotates the piece 90 degrees to the right. The directions in the track are modified accordingly.
     * The right rotation works as follows:
     * - UP -> RIGHT
     * - DOWN -> LEFT
     * - LEFT -> UP
     * - RIGHT -> DOWN
     */
    public void rotateRight() {
        ArrayList<Direction> newTrack = new ArrayList<>();
        for (Direction d : track) {
            switch (d) {
                case UP -> newTrack.add(Direction.RIGHT);
                case DOWN -> newTrack.add(Direction.LEFT);
                case LEFT -> newTrack.add(Direction.UP);
                case RIGHT -> newTrack.add(Direction.DOWN);
            }
        }
        track = newTrack;
        shape = computeShapeFromTrack();
    }

    /**
     * Retrieves the track of the piece, which is a list of directions that represents the movement path of the piece.
     *
     * @return an ArrayList of Directions representing the piece's path.
     */
    public ArrayList<Direction> getTrack() {
        return track;
    }

    public ArrayList<Direction> getBaseTrack(){
        return baseTrack;
    }

    public void resetPiece(){
        this.track = baseTrack;
        this.shape = baseShape;
    }

    public List<int[]> getShape() {
        return shape;
    }
    
    public String getUuid() {
        return uuid;
    }

    public PieceBlueprint getBlueprint() {
        return id;
    }

    public int getIndex() {
        return PieceBlueprint.getIdBlueprint(id);
    }

    @Override
    public String toString() {
        String blueprintId = id.toString();

        // Build a string like "(0,0),(1,0),(1,1),…"
        String shapeStr = getShape().stream()
            .map(off -> String.format("(%d,%d)", off[0], off[1]))
            .collect(Collectors.joining(","));

        return String.format(
            "Piece[blueprint=%s, shape=[%s]]",
            blueprintId,
            shapeStr
        );
    }



}
