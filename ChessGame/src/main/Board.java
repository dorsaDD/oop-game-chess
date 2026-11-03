package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import main.hazards.SpecialSquare;

public final class Board {
    // Constants for Board Size
    private final static int MAX_COL = 8;
    private final static int MAX_ROW = 8;
    // Constants for Square Size(each is 100pixels)
    private static final int SQUARE_SIZE = 100;
    public static int getSquareSize(){

        return SQUARE_SIZE;
    }

    // Registry of board hazards/power-ups (Section: customization layer).
    // Static because there is exactly one board for the whole application,
    // mirroring how GamePanel already keeps "pieces"/"simPieces" as static
    // fields - kept private with controlled access through the static
    // methods below (information hiding).
    private static final Map<String, SpecialSquare> specialSquares = new HashMap<>();

    private static String key(int col, int row) {
        return col + "," + row;
    }

    /** Registers a hazard/power-up at (col, row). Overwrites any previous one there. */
    public static void placeSpecialSquare(int col, int row, SpecialSquare square) {
        specialSquares.put(key(col, row), square);
    }

    /** Returns the hazard/power-up at (col, row), or null if the square is plain. */
    public static SpecialSquare getSpecialSquare(int col, int row) {
        return specialSquares.get(key(col, row));
    }

    /** Removes every registered hazard/power-up - called when a new game starts. */
    public static void clearSpecialSquares() {
        specialSquares.clear();
    }


    //Draws the chessboard
    public void draw(Graphics2D g2) {
        //Initialize Color Counter
        int c = 0;
        //Double Loop to Draw the Board
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                //Set Square Color
                if (c == 0) {
                    g2.setColor(new Color(210, 165, 125));  // Light square
                    c = 1;
                }
                else {
                    g2.setColor(new Color(115, 70, 50));  // Dark square
                    c = 0;
                }
                //Draw the Square
                // HorizentalX-coordinate (left to right)  VerticalY-coordinate (top to bottom)    Width(100)   Height(100)
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
            //Alternate the Color for the Next Row
            if (c == 0){
                c = 1;
            }
            else {
                c = 0;
            }
        }

        // Draw any registered hazards/power-ups on top of the grid.
        // Board only ever talks to SpecialSquare through the interface
        // (getLabel()) - it has no idea whether a given tile is an
        // ObstacleSquare, a ShieldPowerUp, or some future hazard type
        // that does not exist yet (inclusion polymorphism again).
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                SpecialSquare square = getSpecialSquare(col, row);
                if (square == null) {
                    continue;
                }
                if (square.blocksMovement()) {
                    g2.setColor(new Color(40, 40, 40, 160));
                } else {
                    g2.setColor(new Color(60, 200, 90, 140));
                }
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                g2.setColor(Color.WHITE);
                g2.drawString(square.getLabel(),
                        col * SQUARE_SIZE + SQUARE_SIZE / 2 - 8,
                        row * SQUARE_SIZE + SQUARE_SIZE / 2 + 10);
            }
        }
    }

    //Method to Check Valid Positions
    public boolean isValidPosition(int col, int row) {

        return col >= 0 && col < MAX_COL && row >= 0 && row < MAX_ROW;
    }
}
