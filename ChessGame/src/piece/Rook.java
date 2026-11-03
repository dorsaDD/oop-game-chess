package piece;

import main.GamePanel;
import main.Position;

public final class Rook extends Piece {

    private boolean hasMoved = false;

    public Rook(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-rook")) ;
        }
        else {
            setImage(getImage("/piece/b-rook")) ;
        }
    }

    /**
     * Overloaded constructor that takes a Position object instead of separate coordinates.
     * Demonstrates method overloading for constructor polymorphism.
     */
    public Rook(int color, Position position) {
        super(color, position);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-rook")) ;
        }
        else {
            setImage(getImage("/piece/b-rook")) ;
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Check if the move is vertical(col) or horizontal(row)
        boolean isValidMove = (targetCol == getPreCol() || targetRow == getPreRow());
        if (!isValidMove) {
            return false;
        }

        // Check if path is clear
        return !checkPath(targetCol, targetRow);
    }

    @Override
    public void updatePosition() {
        super.updatePosition();

        // Mark the rook as moved after updating position(for Castling)
        if (!hasMoved) {
            hasMoved = true;
        }
    }

    public boolean hasMoved() {
        return hasMoved;
    }
}