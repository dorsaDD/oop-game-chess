package main.rules;

import java.util.ArrayList;

import main.GamePanel;
import piece.King;
import piece.Piece;

/**
 * "King of the Hill" variant: same starting position as standard chess,
 * but a player also wins immediately if their own king reaches and stands
 * on one of the four central squares (d4, d5, e4, e5) at the end of their
 * move - even if the opponent's king is not in checkmate.
 *
 * This class reuses {@link StandardRule} for board setup through
 * composition ("has-a StandardRule", not "is-a StandardRule") rather than
 * inheritance, since the two classes share behaviour but are not in a
 * genuine is-a relationship - a King-of-the-Hill match is not a kind of
 * StandardRule, it simply borrows its setup logic.
 */
public final class KingOfTheHillRule implements GameRule {

    // Composition: this rule delegates the "boring" part (standard setup)
    // to an existing GameRule implementation instead of duplicating it.
    private final StandardRule baseSetup = new StandardRule();

    // Board columns/rows are 0-7; the four central squares are (3,3),
    // (4,3), (3,4), (4,4) - i.e. d5,e5,d4,e4 in chess notation.
    private static final int[] HILL_COLS = {3, 4, 3, 4};
    private static final int[] HILL_ROWS = {3, 3, 4, 4};

    @Override
    public String getName() {
        return "King of the Hill";
    }

    @Override
    public String getDescription() {
        return "Standard setup, but marching your king to a central square also wins the game.";
    }

    @Override
    public void setupBoard(GamePanel panel) {
        baseSetup.setupBoard(panel);
    }

    @Override
    public String checkSpecialWinCondition(ArrayList<Piece> pieces, int colorJustMoved) {
        for (Piece p : pieces) {
            if (p.isKing() && p.getColor() == colorJustMoved) {
                King king = (King) p;
                for (int i = 0; i < HILL_COLS.length; i++) {
                    if (king.getCol() == HILL_COLS[i] && king.getRow() == HILL_ROWS[i]) {
                        String colorName = (colorJustMoved == GamePanel.getWhite()) ? "White" : "Black";
                        return colorName + " wins - King of the Hill!";
                    }
                }
            }
        }
        return null;
    }
}
