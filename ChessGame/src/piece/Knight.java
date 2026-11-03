package piece;

import main.GamePanel;
import main.Position;

public final class Knight extends Piece {
    public Knight(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-knight"));
        }
        else {
            setImage(getImage("/piece/b-knight"));
        }
    }
    /**
     * Overloaded constructor that takes a Position object instead of separate coordinates.
     * Demonstrates method overloading for constructor polymorphism.
     */
    public Knight(int color, Position position) {
        super(color, position);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-knight"));
        }
        else {
            setImage(getImage("/piece/b-knight"));
        }
    }
    @Override
    public boolean needsClearPath() {
        return false;  // Knight can jump over pieces
    }
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }


        // Knight moves in an L-shape: 2 squares in one direction and 1 square perpendicular
        int rowDiff = Math.abs(targetRow - getPreRow());
        int colDiff = Math.abs(targetCol - getPreCol());

        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
}