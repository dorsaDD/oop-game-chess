package piece;

import main.GamePanel;
import main.Position;

public final class Queen extends Piece {
    public Queen(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-queen")) ;
        } else {
            setImage(getImage("/piece/b-queen"));
        }
    }
    /**
     * Overloaded constructor that takes a Position object instead of separate coordinates.
     * Demonstrates method overloading for constructor polymorphism.
     */
    public Queen(int color, Position position) {

        super(color, position);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-queen")) ;
        }
        else {
            setImage(getImage("/piece/b-queen"));
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Check if the move is valid for a queen (horizontal(row), vertical(column), or diagonal(absolute))
        boolean isValidQueenMove = (getPreRow() == targetRow || getPreCol() == targetCol) ||
                (Math.abs(targetRow - getPreRow()) == Math.abs(targetCol - getPreCol()));

        if (!isValidQueenMove) {
            return false;
        }

        // Check if path is not blocked
        return !checkPath(targetCol, targetRow);
    }
}