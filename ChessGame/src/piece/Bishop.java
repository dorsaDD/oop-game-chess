
package piece;

import main.GamePanel;
import main.Position;

public final class Bishop extends Piece {
    public Bishop(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-bishop"));
        } else {
            setImage(getImage("/piece/b-bishop"));
        }
    }

    public Bishop(int color, Position position) {
        super(color, position);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-bishop"));
        } else {
            setImage(getImage("/piece/b-bishop"));
        }
    }


    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Your existing implementation
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Check if the move is valid for a Bishop(diagonal) the number col and row must be equal like 1:1
        boolean isValidMove = Math.abs(targetRow - getPreRow()) == Math.abs(targetCol - getPreCol());
        if (!isValidMove) {
            return false;
        }

        // Check if path is not blocked
        return !checkPath(targetCol, targetRow);
    }

}


