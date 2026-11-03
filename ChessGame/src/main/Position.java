package main;

/**
 * Represents a position on the chess board.
 * Helps method overloading.
 */
public final class Position {
    private final int col;
    private final int row;
    public int getCol() { return col; }
    public int getRow() { return row; }

    public Position(int col, int row) {
        this.col = col;
        this.row = row;
    }


    public Position offset(double colOffset, double rowOffset) {
        // This demonstrates coercion polymorphism:
        // The double parameters will be implicitly converted (coerced) to int
        // when added to the int fields, causing the decimal part to be truncated
        return new Position(this.col + (int)colOffset, this.row + (int)rowOffset);
    }


    @Override
    public String toString() {
        return "(" + col + ", " + row + ")";
    }

}

