package main.rules;

import java.util.ArrayList;

import main.GamePanel;
import main.Position;
import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

/**
 * The classic, unmodified chess rule set: standard starting position,
 * no additional win condition beyond the checkmate/stalemate logic that
 * GamePanel/GameState already enforce.
 *
 * This class is also reused (by composition, not inheritance) by other
 * rule variants that only want to change one aspect of the game - see
 * {@link HazardArenaRule}, which keeps the standard piece layout but adds
 * board hazards on top of it.
 */
public final class StandardRule implements GameRule {

    @Override
    public String getName() {
        return "Standard Chess";
    }

    @Override
    public String getDescription() {
        return "The classic chess rules and starting position - no changes.";
    }

    @Override
    public void setupBoard(GamePanel panel) {
        int WHITE = GamePanel.getWhite();
        int BLACK = GamePanel.getBlack();

        // White team
        panel.addPiece(new Pawn(WHITE, new Position(0, 6)));
        panel.addPiece(new Pawn(WHITE, 1, 6));
        panel.addPiece(new Pawn(WHITE, 2, 6));
        panel.addPiece(new Pawn(WHITE, 3, 6));
        panel.addPiece(new Pawn(WHITE, 4, 6));
        panel.addPiece(new Pawn(WHITE, 5, 6));
        panel.addPiece(new Pawn(WHITE, 6, 6));
        panel.addPiece(new Pawn(WHITE, new Position(7, 6)));
        panel.addPiece(new Rook(WHITE, new Position(0, 7)));
        panel.addPiece(new Rook(WHITE, 7, 7));
        panel.addPiece(new Knight(WHITE, new Position(1, 7)));
        panel.addPiece(new Knight(WHITE, 6, 7));
        panel.addPiece(new Bishop(WHITE, new Position(2, 7)));
        panel.addPiece(new Bishop(WHITE, 5, 7));
        panel.addPiece(new Queen(WHITE, 3, 7));
        panel.addPiece(new King(WHITE, new Position(4, 7)));

        // Black team
        panel.addPiece(new Pawn(BLACK, new Position(0, 1)));
        panel.addPiece(new Pawn(BLACK, 1, 1));
        panel.addPiece(new Pawn(BLACK, 2, 1));
        panel.addPiece(new Pawn(BLACK, 3, 1));
        panel.addPiece(new Pawn(BLACK, 4, 1));
        panel.addPiece(new Pawn(BLACK, 5, 1));
        panel.addPiece(new Pawn(BLACK, 6, 1));
        panel.addPiece(new Pawn(BLACK, new Position(7, 1)));
        panel.addPiece(new Rook(BLACK, new Position(0, 0)));
        panel.addPiece(new Rook(BLACK, 7, 0));
        panel.addPiece(new Knight(BLACK, new Position(1, 0)));
        panel.addPiece(new Knight(BLACK, 6, 0));
        panel.addPiece(new Bishop(BLACK, new Position(2, 0)));
        panel.addPiece(new Bishop(BLACK, 5, 0));
        panel.addPiece(new Queen(BLACK, new Position(3, 0)));
        panel.addPiece(new King(BLACK, 4, 0));
    }

    @Override
    public String checkSpecialWinCondition(ArrayList<Piece> pieces, int colorJustMoved) {
        return null; // Standard chess adds no extra win condition.
    }
}
