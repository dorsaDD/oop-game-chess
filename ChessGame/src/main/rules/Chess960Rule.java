package main.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
 * Chess960 (Fischer Random Chess): pawns are placed as usual, but the
 * back-rank piece order is randomized for every new game, subject to two
 * classic constraints:
 *   1. the two bishops must stand on opposite-colored squares;
 *   2. the king must end up strictly between the two rooks.
 * Both sides use the same randomized order (mirrored), exactly like
 * standard chess mirrors White and Black.
 *
 * This class demonstrates that a GameRule implementation can override
 * setup completely (rather than delegating to StandardRule like
 * KingOfTheHillRule does), which is exactly the flexibility the GameRule
 * interface was designed to allow.
 */
public final class Chess960Rule implements GameRule {

    private final Random random = new Random();

    @Override
    public String getName() {
        return "Chess960 (Fischer Random)";
    }

    @Override
    public String getDescription() {
        return "Randomized back-rank starting position, generated fresh each game.";
    }

    @Override
    public void setupBoard(GamePanel panel) {
        int[] backRank = generateBackRank();

        int WHITE = GamePanel.getWhite();
        int BLACK = GamePanel.getBlack();

        for (int col = 0; col < 8; col++) {
            panel.addPiece(new Pawn(WHITE, new Position(col, 6)));
            panel.addPiece(new Pawn(BLACK, new Position(col, 1)));
            panel.addPiece(makePiece(backRank[col], WHITE, col, 7));
            panel.addPiece(makePiece(backRank[col], BLACK, col, 0));
        }
    }

    /**
     * Returns an array of 8 piece-type codes (see {@link #makePiece}),
     * one per column, satisfying the Chess960 placement rules.
     */
    private int[] generateBackRank() {
        List<Integer> freeCols = new ArrayList<>();
        for (int c = 0; c < 8; c++) {
            freeCols.add(c);
        }
        int[] rank = new int[8]; // 0=empty (filled below), see makePiece() codes

        // 1. Bishops on opposite-colored squares (even column vs odd column).
        int bishop1 = pickAndRemove(freeCols, true);
        rank[bishop1] = BISHOP;
        int bishop2 = pickAndRemove(freeCols, false);
        rank[bishop2] = BISHOP;

        // 2. Queen on any remaining square.
        int queenCol = freeCols.remove(random.nextInt(freeCols.size()));
        rank[queenCol] = QUEEN;

        // 3. Two knights on any remaining squares.
        int knight1 = freeCols.remove(random.nextInt(freeCols.size()));
        rank[knight1] = KNIGHT;
        int knight2 = freeCols.remove(random.nextInt(freeCols.size()));
        rank[knight2] = KNIGHT;

        // 4. The 3 columns left over take Rook, King, Rook, in ascending
        //    column order - this guarantees the king ends up strictly
        //    between the two rooks.
        Collections.sort(freeCols);
        rank[freeCols.get(0)] = ROOK;
        rank[freeCols.get(1)] = KING;
        rank[freeCols.get(2)] = ROOK;

        return rank;
    }

    /** Removes and returns a column of the requested parity from the list. */
    private int pickAndRemove(List<Integer> cols, boolean even) {
        List<Integer> candidates = new ArrayList<>();
        for (int c : cols) {
            if ((c % 2 == 0) == even) {
                candidates.add(c);
            }
        }
        int chosen = candidates.get(random.nextInt(candidates.size()));
        cols.remove(Integer.valueOf(chosen));
        return chosen;
    }

    // Piece-type codes used only inside this class's back-rank array.
    private static final int ROOK = 1;
    private static final int KNIGHT = 2;
    private static final int BISHOP = 3;
    private static final int QUEEN = 4;
    private static final int KING = 5;

    private Piece makePiece(int code, int color, int col, int row) {
        switch (code) {
            case ROOK:   return new Rook(color, col, row);
            case KNIGHT: return new Knight(color, col, row);
            case BISHOP: return new Bishop(color, col, row);
            case QUEEN:  return new Queen(color, col, row);
            case KING:   return new King(color, col, row);
            default:
                throw new IllegalStateException("Unassigned Chess960 column: " + col);
        }
    }

    @Override
    public String checkSpecialWinCondition(ArrayList<Piece> pieces, int colorJustMoved) {
        return null; // Chess960 uses the standard checkmate/stalemate rules.
    }
}
