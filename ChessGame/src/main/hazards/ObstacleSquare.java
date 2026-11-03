package main.hazards;

import main.GamePanel;
import piece.Piece;

/**
 * A solid obstacle: no piece may move onto it or slide through it.
 * GamePanel checks {@link #blocksMovement()} before allowing a move onto
 * this square, and Piece's own path-checking (checkSpot/checkPath) treats
 * it exactly like an occupied square for sliding pieces (Rook, Bishop,
 * Queen), so a Rook cannot "see past" an obstacle any more than it can
 * see past another piece.
 */
public final class ObstacleSquare implements SpecialSquare {

    @Override
    public boolean blocksMovement() {
        return true;
    }

    @Override
    public void onEnter(Piece piece, GamePanel panel) {
        // Never actually invoked: blocksMovement() == true means GamePanel
        // rejects any move that would land a piece here in the first
        // place. Implemented anyway so ObstacleSquare is a complete,
        // well-formed SpecialSquare rather than a partial implementation.
    }

    @Override
    public String getLabel() {
        return "X";
    }
}
