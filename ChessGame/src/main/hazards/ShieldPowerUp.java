package main.hazards;

import main.GamePanel;
import piece.Piece;

/**
 * A power-up tile: does not block movement, and grants the piece that
 * lands on it one full ply (a single move, by either side) of immunity
 * to capture. See {@link piece.Piece#setShieldTurns(int)}.
 */
public final class ShieldPowerUp implements SpecialSquare {

    @Override
    public boolean blocksMovement() {
        return false;
    }

    @Override
    public void onEnter(Piece piece, GamePanel panel) {
        piece.setShieldTurns(1);
    }

    @Override
    public String getLabel() {
        return "S";
    }
}
