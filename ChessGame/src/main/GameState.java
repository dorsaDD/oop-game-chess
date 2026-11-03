package main;

import java.util.ArrayList;
import piece.King;
import piece.Knight;
import piece.Piece;

public final class GameState {
    private static final int ONGOING = 0;
    public static int getOngiong(){
        return ONGOING;
    }
    private static final int CHECK = 1;
    public static int getCheck(){
        return CHECK;
    }
    private static final int CHECKMATE = 2;
    public static int getCheckMate(){
        return CHECKMATE;
    }
    private static final int STALEMATE = 3;
    public static int getStalemate(){
        return STALEMATE;
    }



    /**
     * Method to check if the king of the specified color is in check
     */
    public static boolean isKingInCheck(ArrayList<Piece> pieces, int kingColor) {
        // Find the king of the specified color
        King king = null;
        for (Piece p : pieces) {
            if (p instanceof King && p.getColor() == kingColor) {
                king = (King) p;
                break;
            }
        }
        //no king was found
        if (king == null) {
            System.err.println("ERROR: King not found for color " + kingColor);
            return true;
        }

        //Getting King's Position
        int kingCol = king.getCol();
        int kingRow = king.getRow();


        // Check if any opponent's piece can capture the king
        // Create a copy of the list to safely iterate
        ArrayList<Piece> piecesCopy = new ArrayList<>(pieces);

        for (Piece attacker : piecesCopy) {
            // Same color as the king, can't check!
            if (attacker.getColor() == kingColor) {
                continue;//ignoring friendly pieces
            }

            // Check if this piece can move to the king's position
            if (attacker.canMove(kingCol, kingRow)) {
                // Knights don't need path checking
                if (attacker instanceof Knight) {
                    return true;
                }

                // For all other pieces, check if the path to the king is clear
                boolean pathBlocked = false;

                // Calculate direction from attacker to king the direction from attacker to king)
                //(Integer.compare(a, b) returns -1, 0, or 1 depending on whether a < b, a == b, or a > b)
                int colDirection = Integer.compare(kingCol, attacker.getCol());
                int rowDirection = Integer.compare(kingRow, attacker.getRow());

                // Start checking from the square after the attacker
                int currentCol = attacker.getCol() + colDirection;
                int currentRow = attacker.getRow() + rowDirection;

                // Check each square until we reach the king (exclusive)
                while (currentCol != kingCol || currentRow != kingRow) {
                    // Check if any piece is on this square
                    for (Piece blockingPiece : piecesCopy) {
                        if (blockingPiece.getCol() == currentCol && blockingPiece.getRow() == currentRow) {
                            pathBlocked = true;
                            break;
                        }
                    }

                    if (pathBlocked) {
                        break;
                    }

                    //Moving to Next Square
                    currentCol += colDirection;
                    currentRow += rowDirection;
                }

                // If path is not blocked, the king is in check
                if (!pathBlocked) {
                    return true;//king is in check
                }
            }
        }

        return false;//If we've checked all opponent pieces and none can attack the king is not in check
    }

}