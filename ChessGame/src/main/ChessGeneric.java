package main;

import piece.Piece;
import java.util.ArrayList;
import java.util.function.Predicate;//functional interface(returns a boolean for a given T)

/**
 * ChessGeneric class demonstrating parametric polymorphism through generics.
 * This allows type-safe operations on chess pieces.
 */
public final class ChessGeneric {
    /**
     * Generic method that filters pieces based on a predicate.
     * Demonstrates parametric polymorphism by working with any subtype of Piece.
     */
    static  <T extends Piece> ArrayList<T> filterPieces(ArrayList<T> pieces, Predicate<T> predicate) {
        ArrayList<T> result = new ArrayList<>();//<> no need to write <T>->diamond operator
        //If the predicate returns true for a piece, it’s added to the result
        for (T piece : pieces) {
            if (predicate.test(piece)) {
                result.add(piece);
            }
        }
        return result;
    }
}