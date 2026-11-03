package piece;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import main.GamePanel;
import main.Board;
import main.Position;

import javax.imageio.ImageIO;

import main.hazards.SpecialSquare;

public abstract class Piece {

    // INFORMATION HIDING - Step 1: All attributes are private
    private BufferedImage image;
    private int x, y;              // pixel Coordinate
    private int col, row, preCol, preRow;
    // Step 2: Determine constant vs variable
    private final int color;       // CONSTANT - never changes
    // Step 3: All are instance members (not static)

    // Customization layer: temporary power-up state (Section: Hazards &
    // Power-ups). 0 = not shielded. A positive value counts down by one
    // ply (one move, by either side): GamePanel.update() calls
    // tickShield() on every piece right after each real move is executed.
    private int shieldTurns = 0;

    public boolean isShielded() { return shieldTurns > 0; }
    public void setShieldTurns(int turns) { this.shieldTurns = turns; }
    public void tickShield() {
        if (shieldTurns > 0) {
            shieldTurns--;
        }
    }

    public void setX(int x){ this.x = x;}
    public void setY(int y){ this.y = y;}
    public void setCol(int col){ this.col = col;}
    public void setRow(int row){ this.row = row;}
    public void setPreCol(int precol){ this.preCol = precol; }
    public void setPreRow(int prerow){ this.preRow = prerow; }
    public int getCol() { return col; }
    public int getRow() { return row; }
    public int getPreCol() { return preCol; }
    public int getPreRow() { return preRow; }
    public int getColor() { return color; }
    public void setImage(BufferedImage image) { this.image = image; }
    // conversion-> center point of the piece
    public int getX(int col){ return col * Board.getSquareSize(); }
    public int getY(int row){ return row * Board.getSquareSize(); }



    public Piece(int color, int col, int row){
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);//Coordinates x,y on the screen
        y = getY(row);
        preCol = col;
        preRow = row;
    }


    /**
     * Overloaded constructor!.
     */
    public Piece(int color, Position position) {
        this(color, position.getCol(), position.getRow());
    }


    // Exception handling in image loading
    public BufferedImage getImage(String imagePath) {
        try (InputStream stream = getClass().getResourceAsStream(imagePath + ".png")) {
            if (stream != null) {
                BufferedImage image = ImageIO.read(stream);
                if (image != null) {
                    return image;
                }
            }
            throw new IOException("Image could not be loaded: " + imagePath);
        } catch (IOException e) {
            return createPieceImage(imagePath);
        }
    }

    /**
     * Draws a clear chess piece symbol when PNG assets are unavailable.
     * Path format: /piece/w-king, /piece/b-pawn, etc.
     */
    private BufferedImage createPieceImage(String imagePath) {
        boolean isWhite = imagePath.contains("/w-");
        String pieceType = imagePath.substring(imagePath.lastIndexOf('-') + 1);
        String symbol = getChessSymbol(pieceType, isWhite);

        int size = Board.getSquareSize();
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int padding = 10;
        if (isWhite) {
            g.setColor(new Color(248, 248, 242));
            g.fillOval(padding, padding, size - padding * 2, size - padding * 2);
            g.setColor(new Color(35, 35, 40));
            g.drawOval(padding, padding, size - padding * 2, size - padding * 2);
            g.setColor(new Color(20, 20, 24));
        } else {
            g.setColor(new Color(45, 45, 52));
            g.fillOval(padding, padding, size - padding * 2, size - padding * 2);
            g.setColor(new Color(210, 210, 205));
            g.drawOval(padding, padding, size - padding * 2, size - padding * 2);
            g.setColor(new Color(235, 235, 230));
        }

        Font pieceFont = resolvePieceFont(size);
        g.setFont(pieceFont);
        FontMetrics metrics = g.getFontMetrics();
        int textX = (size - metrics.stringWidth(symbol)) / 2;
        int textY = (size - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(symbol, textX, textY);
        g.dispose();
        return image;
    }

    private static String getChessSymbol(String pieceType, boolean isWhite) {
        return switch (pieceType) {
            case "king" -> isWhite ? "\u2654" : "\u265A";
            case "queen" -> isWhite ? "\u2655" : "\u265B";
            case "rook" -> isWhite ? "\u2656" : "\u265C";
            case "bishop" -> isWhite ? "\u2657" : "\u265D";
            case "knight" -> isWhite ? "\u2658" : "\u265E";
            case "pawn" -> isWhite ? "\u2659" : "\u265F";
            default -> isWhite ? "?" : "?";
        };
    }

    private static Font resolvePieceFont(int size) {
        String[] fontNames = {"Segoe UI Symbol", "DejaVu Sans", "Arial Unicode MS", "Dialog"};
        int fontSize = (int) (size * 0.72);
        for (String fontName : fontNames) {
            Font font = new Font(fontName, Font.PLAIN, fontSize);
            if (font.canDisplay('\u2654')) {
                return font;
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
    }
    public boolean isKing() {
        return false;  // Default: most pieces are not kings
    }

    public boolean needsClearPath() {
        return true;  // Default: most pieces need clear path
    }


    public void updatePosition() {
        //saves old position
        preCol = col;
        preRow = row;
    }

    // Abstract method
    public abstract boolean canMove(int targetCol, int targetRow);

    public void checkPromotion(Runnable promotionHandler) {
        // Default: most pieces don't promote
        // Only Pawn overrides this
    }



    public boolean isWithinBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

    //Checks if a move is blocked
    public boolean checkPath(int targetCol, int targetRow) {
        int startCol = this.preCol;
        int startRow = this.preRow;

        int colDirection = Integer.compare(targetCol, startCol);
        int rowDirection = Integer.compare(targetRow, startRow);

        int currentCol = startCol + colDirection;
        int currentRow = startRow + rowDirection;

        while (currentCol != targetCol || currentRow != targetRow) {
            if (checkSpot(currentCol, currentRow)) {
                return true;
            }
            currentCol += colDirection;
            currentRow += rowDirection;
        }

        return false;
    }

    //check other piece is at the target (col/row)
    public boolean checkSpot(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.getSimPieces()) {
            if (piece != this && piece.getCol() == targetCol && piece.getRow() == targetRow) {
                return true;
            }
        }
        // Customization layer: an obstacle square (Section: Hazards &
        // Power-ups) blocks a sliding piece's path exactly like another
        // piece would - checkPath() calls checkSpot() for every square
        // along the way, so this one change is enough to make Rooks,
        // Bishops and Queens respect obstacles without touching their
        // individual canMove() implementations.
        SpecialSquare square = main.Board.getSpecialSquare(targetCol, targetRow);
        return square != null && square.blocksMovement();
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.getSquareSize(), Board.getSquareSize(), null);
    }
}
