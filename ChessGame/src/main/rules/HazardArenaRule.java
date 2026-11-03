package main.rules;

import java.util.ArrayList;

import main.Board;
import main.GamePanel;
import main.hazards.ObstacleSquare;
import main.hazards.ShieldPowerUp;
import piece.Piece;

/**
 * "Hazard Arena" scenario: identical starting position to standard chess,
 * but the board also has a few obstacle squares and one shield power-up
 * placed on it before the game begins.
 *
 * This class shows composition of two independent extension mechanisms:
 * it reuses {@link StandardRule} for the piece layout, and additionally
 * populates {@link Board}'s special-square registry - the same
 * SpecialSquare hazards/power-ups a future rule variant could reuse in a
 * completely different arrangement.
 */
public final class HazardArenaRule implements GameRule {

    private final StandardRule baseSetup = new StandardRule();

    @Override
    public String getName() {
        return "Hazard Arena";
    }

    @Override
    public String getDescription() {
        return "Standard setup, plus obstacle squares and a shield power-up on the board.";
    }

    @Override
    public void setupBoard(GamePanel panel) {
        Board.clearSpecialSquares();
        baseSetup.setupBoard(panel);

        // A small wall of obstacles across the middle of the board.
        Board.placeSpecialSquare(2, 3, new ObstacleSquare());
        Board.placeSpecialSquare(2, 4, new ObstacleSquare());
        Board.placeSpecialSquare(5, 3, new ObstacleSquare());
        Board.placeSpecialSquare(5, 4, new ObstacleSquare());

        // A single shield power-up sitting in the middle of the board.
        Board.placeSpecialSquare(4, 4, new ShieldPowerUp());
    }

    @Override
    public String checkSpecialWinCondition(ArrayList<Piece> pieces, int colorJustMoved) {
        return null; // Hazard Arena uses the standard checkmate/stalemate rules.
    }
}
