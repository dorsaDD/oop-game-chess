package main;

import piece.*;

public final class MultitypingDemo {
    private static Queen queenObject = new Queen(GamePanel.getWhite(), 3, 3);

    public static void runDemo() {
        System.out.println("\n===== SUBTYPING/MULTITYPING DEMONSTRATION =====");

        // Create a Queen object

        // Access the SAME object through DIFFERENT type variables
        Piece pieceVariable = queenObject;      // Queen viewed as Piece
        Object objectVariable = queenObject;     // Queen viewed as Object

        // Prove it's the same object
        System.out.println("All variables point to same object: " +
                (queenObject == pieceVariable && pieceVariable == objectVariable));

        // Show the actual type remains Queen regardless of variable type
        System.out.println("Queen accessed as Queen type: " + queenObject.getClass().getSimpleName());
        System.out.println("Queen accessed as Piece type: " + pieceVariable.getClass().getSimpleName());
        System.out.println("Queen accessed as Object type: " + objectVariable.getClass().getSimpleName());
    }

}