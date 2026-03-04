package model;

/**
 * This class represents the preview of a piece in the board before placing it.
 * It acts out like a ghost piece to visualize where it would be placed.
 */
public class PlacementPreview {
    private int row;
    private int col;
    private boolean valid;
    private Piece selectedPiece;    // Selected piece in the piece bag.

    public PlacementPreview(int row, int col, boolean isValid, Piece piece) {
        this.row = row;
        this.col = col;
        this.valid = isValid;
        this.selectedPiece = piece;
    }

    public void update(int newRow, int newCol, boolean newValidity) {
        this.row = newRow;
        this.col = newCol;
        this.valid = newValidity;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean isValid) {
        this.valid = isValid;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void setSelectedPiece(Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
    }
}