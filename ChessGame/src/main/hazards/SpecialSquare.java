package main.hazards;

import main.GamePanel;
import piece.Piece;

/**
 * Represents a non-standard board tile: something other than a plain,
 * empty square. GamePanel and Piece interact with every hazard/power-up
 * only through this interface (never through a concrete class), which is
 * a second, independent demonstration of subtyping via interface and
 * inclusion polymorphism, separate from the {@link main.rules.GameRule}
 * hierarchy.
 *
 * Adding a brand-new kind of tile (a teleporter, a fire square that
 * destroys a piece, ...) only requires a new class implementing this
 * interface - Board, Piece and GamePanel do not need to change
 * (Open-Closed Principle / extensibility).
 */
public interface SpecialSquare {

    /**
     * True if a piece is not allowed to move onto (or through, for
     * sliding pieces) this square at all - e.g. a solid obstacle.
     */
    boolean blocksMovement();

    /**
     * Called exactly once, by GamePanel, the moment a piece finishes a
     * move that lands it on this square. Squares that block movement
     * never receive this call, since a piece can never legally land on
     * them.
     */
    void onEnter(Piece piece, GamePanel panel);

    /** Single-character label used to render this square on the board. */
    String getLabel();
}
