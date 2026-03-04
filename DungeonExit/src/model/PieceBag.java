package model;

import java.util.ArrayList;
import java.util.List;

/**
 * The PieceBag class contains a collection of Tetris-like pieces that the player can place on the board
 * to form a path. Each piece has a specific shape and can be selected for placement.
 */
public class PieceBag {

    protected List<Piece> pieces; // List of available pieces in the bag
    protected int limits;
    protected Piece selectedPiece;     // The currently selected piece to be placed on the board
    protected int nbPose;

    private Tile.TileType selectedCheckpoint;

    /**
     * Constructs a PieceBag by initializing the list of pieces. The pieces are created based on predefined
     * blueprints (from PieceBlueprint enum). Each blueprint represents a different shape.
     */
    public PieceBag(List<Piece> pieces, int limits) {
        this.pieces = pieces;
        this.limits = limits;
        this.selectedCheckpoint = null;
    }

    public PieceBag(PieceBag piece) {
        List<Piece> copy = piece.getPieces();
        this.pieces = new ArrayList<>();
        for (int i = 0; i < copy.size(); i++) {
            this.pieces.add(new Piece(copy.get(i)));
        }
        this.limits = piece.limits;
        this.selectedCheckpoint = null;
    }

    public PieceBag() {
        this.pieces = new ArrayList<>();
        this.limits = 15; // TODO: Change
        this.selectedCheckpoint = null;

    }

    /**
     * Removes the currently selected piece from the PieceBag. The selectedPiece is set to null.
     */
    public void removeSelectedPiece() {
        pieces.remove(selectedPiece); // Remove the selected piece from the list
        selectedPiece = null;      // Reset the selectedPiece
    }

    public void removeSelectedCheckpoint() {
        selectedCheckpoint = null;
    }

    public void removePiece(Piece piece){
        pieces.remove(piece);
        selectedPiece = null;  
    }

    /**
     * Checks if there is a piece currently selected.
     *
     * @return true if a piece is selected, false otherwise.
     */
    public boolean isPieceSelected() {
        return selectedPiece != null; // Returns true if selectedPiece is not null
    }

    public boolean isCheckpointSelected() {
        return selectedCheckpoint != null;
    }

    /**
     * Gets the list of pieces in the bag.
     *
     * @return an ArrayList containing all the pieces in the bag.
     */
    public List<Piece> getPieces() {
        return pieces;
    }

    /**
     * Gets the currently selected piece.
     *
     * @return the selected piece, or null if no piece is selected.
     */
    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public Tile.TileType getSelectedCheckpoint() {
        return selectedCheckpoint;
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public int getLimits(){
        return limits;
    }

    public int getNbPose() {
        return nbPose;
    }

    public void increasePose() {
        this.nbPose++;
    }

    public void decreasePose() {
        if (nbPose <= 0)
            return ;
        this.nbPose--;
    }

    public boolean checkNbPiece() {
        return (nbPose < limits);
    }

    /**
     * Sets the currently selected piece to the specified piece.
     *
     * @param selectedPiece the piece to set as selected.
     */
    public void setSelectedPiece(Piece selectedPiece) {
        // A copy of the piece with a different ID
        if (nbPose == limits) {
            this.selectedPiece = null;
            return ;
        }
        this.selectedPiece = selectedPiece;
    }

    public void setSelectedCheckpoint(Tile.TileType checkpoint) {
        this.selectedCheckpoint = checkpoint;
        this.selectedPiece = null;
    }   
    public void setPlayerPiece(int player) {
        for (Piece p : pieces) {
            p.setPlayer(player);
        }
    }

    public boolean isPieceInPieceBag(Piece piece) {
        for (Piece p : pieces) {
            if (p.getIndex() == piece.getIndex()) {
                return true;
            }
        }
        return false;
    }

    // public void printPiece() {
    //     for (int i = 0; i < pieces.size();i++) {
    //         System.out.println("p : " + PieceBlueprint.getIdBlueprint(pieces.get(i).getBlueprint()));
    //     }
    // }
}
