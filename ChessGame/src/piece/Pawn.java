package piece;

import main.GamePanel;
import main.Position;

public final class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-pawn"));
        }
        else {
            setImage(getImage("/piece/b-pawn"));
        }
    }

    /**
     * Overloaded constructor that takes a Position object instead of separate coordinates.
     * Demonstrates method overloading for constructor polymorphism.
     */
    public Pawn(int color, Position position) {

        super(color, position);
        if (color == GamePanel.getWhite()) {
            setImage(getImage("/piece/w-pawn"));
        }
        else {
            setImage(getImage("/piece/b-pawn"));
        }

    }


    // Corrected method in Pawn.java
    public boolean canBePromoted() {
        // White pawns promote when they reach row 0 (top of board)
        // Black pawns promote when they reach row 7 (bottom of board)
        return (getColor() == GamePanel.getWhite() && getRow() == 0) ||
                (getColor() == GamePanel.getBlack() && getRow() == 7);
    }

    @Override
    public void checkPromotion(Runnable handler) {
        if (canBePromoted()) {
            handler.run();
        }
    }


    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }


        // Check for piece at target position
        boolean isTargetEnemy = false;

        for (Piece piece : GamePanel.getSimPieces()) {
            if (piece != this && piece.getCol() == targetCol && piece.getRow() == targetRow) {
                isTargetEnemy = true;
                break;
            }
        }

        // White pawns move up (decreasing row), black pawns move down (increasing row)
        if (getColor() == GamePanel.getWhite()) {
            // Diagonal capture for white
            if (Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow() - 1) {
                return isTargetEnemy ;
            }

            // Forward movement for white
            if (targetCol == getPreCol()) {
                // Can't move forward if blocked
                if (isTargetEnemy) {
                    return false;
                }

                // One square forward
                if (targetRow == getPreRow() - 1) {
                    return true;
                }

                // Two squares forward from starting position
                if (!hasMoved && targetRow == getPreRow() - 2) {
                    // Check if path is clear
                    for (Piece piece : GamePanel.getSimPieces()) {
                        if (piece != this && piece.getCol() == targetCol && piece.getRow() == getPreRow() - 1) {
                            return false;
                        }
                    }
                    //can move 2 at first
                    return true;
                }
            }
        }

        else {  // Black pawn movement
            // Diagonal capture for black
            if (Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow() + 1) {
                return isTargetEnemy;
            }

            // Forward movement for black
            if (targetCol == getPreCol()) {
                // Can't move forward if blocked
                if (isTargetEnemy) {
                    return false;
                }

                // One square forward
                if (targetRow == getPreRow() + 1) {
                    return true;
                }

                // Two squares forward from starting position
                if (!hasMoved && targetRow == getPreRow() + 2) {

                    // Check if path is clear(Check for piece of same color at target)
                    for (Piece piece : GamePanel.getSimPieces()) {
                        if (piece != this && piece.getCol() == targetCol && piece.getRow() == getPreRow() + 1) {
                            return false;
                        }
                    }
                    //can move 2 at first
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updatePosition() {
        super.updatePosition();
        if (!hasMoved) {
            hasMoved = true;
        }
    }
}