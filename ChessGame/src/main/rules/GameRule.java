package main.rules;

import java.util.ArrayList;

import main.GamePanel;
import piece.Piece;

/**
 * Strategy interface for a pluggable chess rule variant.
 *
 * GamePanel (the core game engine) depends only on this interface, never on
 * any concrete rule class. This is a direct, practical demonstration of:
 *   - subtyping / multityping through an interface (rather than through the
 *     abstract-class hierarchy used for Piece);
 *   - inclusion polymorphism (GamePanel calls activeRule.setupBoard(...) and
 *     the correct variant-specific behaviour runs, decided at runtime by the
 *     concrete type actually stored in the "activeRule" field);
 *   - extensibility / the Open-Closed Principle: a brand-new rule variant
 *     can be added by writing one new class that implements GameRule,
 *     without touching a single line of GamePanel, Board or the Piece
 *     hierarchy.
 */
public interface GameRule {

    /** Short, human-readable name shown in the rule-selection dialog. */
    String getName();

    /**
     * One-line description shown under the name in the rule-selection
     * dialog, so the player knows what this variant changes.
     */
    String getDescription();

    /**
     * Sets up the initial position for a new game under this rule variant.
     * Implementations place pieces via {@link GamePanel#addPiece(Piece)}
     * and, optionally, register board hazards/power-ups via
     * {@link main.Board#placeSpecialSquare}.
     */
    void setupBoard(GamePanel panel);

    /**
     * Checks whether this rule variant declares a winner through a
     * condition that is *additional* to the standard checkmate/stalemate
     * rules already enforced by GameState/GamePanel.
     *
     * @param pieces        the live pieces on the board
     * @param colorJustMoved the color of the player who just completed a move
     * @return a human-readable end-of-game message if this rule declares a
     *         winner right now, or {@code null} if it has nothing to add for
     *         the current position (the default/standard outcome applies).
     */
    String checkSpecialWinCondition(ArrayList<Piece> pieces, int colorJustMoved);
}
