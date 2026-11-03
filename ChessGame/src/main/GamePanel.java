package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

import main.hazards.SpecialSquare;
import main.rules.GameRule;
import main.rules.StandardRule;

public final class GamePanel extends JPanel implements Runnable {


	//game window size
	private static final int WIDTH = 1100;
	private static final int HEIGHT = 800;
	//Frame Per Second
	private final int FPS = 60;
	//Manages the game loop
	private Thread gameThread;

	//has a board
	Board board = new Board(); //chessboard
	//has a mouse
	Mouse mouse = new Mouse(); //Mouse Interaction

	// Pieces Storage( has pieces [ Aggregation  ] )
	private static ArrayList<Piece> pieces = new ArrayList<>();//stores all active pieces
	private static ArrayList<Piece> simPieces = new ArrayList<>();//copy of pieces
	public static ArrayList<Piece> getSimPieces() {
		return simPieces;
	}
	private Piece activeP;//currently selected (active) piece

	// Color Constants
	private static final int WHITE = 0;
	public static int getWhite(){
		return WHITE;
	}
	private static final int BLACK = 1;
	public static int getBlack(){
		return BLACK;
	}

	int currentColor = WHITE;// keeps track of turns

	// Variables for Move Validation
	private int startCol;
	private int startRow;


	// Game state variables
	private int gameState = GameState.getOngiong(); //stores the current state
	private boolean gameOver = false; // true = game ends
	private String statusMessage = ""; //store messages

	// Active rule variant (customization layer). GamePanel depends only on
	// the GameRule INTERFACE here, never on a concrete rule class - this
	// field is a direct example of subtyping via interface + inclusion
	// polymorphism: whichever concrete rule is stored, activeRule.xxx()
	// calls always run the right variant-specific behaviour.
	private GameRule activeRule;

	//Constructor & Initialization(create an object in main class)
	public GamePanel() {
		this(new StandardRule());
	}

	/**
	 * Overloaded constructor (Section: Overloading Polymorphism) that lets
	 * the caller choose a GameRule variant instead of always defaulting to
	 * standard chess. main.main() uses this to let the player pick a
	 * scenario/level at startup.
	 */
	public GamePanel(GameRule rule) {
		this.activeRule = rule;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.darkGray);
		// Mouse move pieces
		addMouseListener(mouse);

		setPieces(); //setpieces in further (initializes all pieces) we call it in this constructor
		copyPieces(pieces, simPieces);// copy pieces for simulate we call it in this constructor too
	}

	public String getActiveRuleName() {
		return activeRule.getName();
	}

	//Launching the Game
	public void launchGame() {
		gameThread = new Thread(this);// this = current GamePanel object
		gameThread.start();// start the run method
	}


	//Initializing chess pieces
	public void setPieces() {
		pieces.clear();
		// Customization layer: the actual layout (and, for some variants,
		// board hazards) is entirely delegated to the active GameRule -
		// GamePanel no longer hard-codes a single starting position.
		activeRule.setupBoard(this);
	}

	/**
	 * Controlled way for a GameRule implementation to add a piece to the
	 * starting position. "pieces" itself stays private (information
	 * hiding); this is the one operation GamePanel exports for it.
	 */
	public void addPiece(Piece p) {
		pieces.add(p);
	}

	//this method handle pieces by 2 list source(pieces), target(simPieces)
	//adding from source(pieces), to target(simPieces)
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}

	//Game Loop (Thread Execution)
	@Override // from the interface runnable
	public void run() {
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		while (gameThread != null) {
			currentTime = System.nanoTime(); //Records the current time
			delta += (currentTime - lastTime)/drawInterval; //Calculates the time difference
			lastTime = currentTime;//Delta gather time in frame

			if (delta >= 1) {
				update(); // update game state
				repaint(); // redraw the screen
				delta--; // prepare for the next frame
			}
		}
	}


	private void handlePawnPromotion(Pawn pawn) {
		final int[] choiceHolder = {0};
		try {
			SwingUtilities.invokeAndWait(() -> {
				String[] options = {"Queen", "Rook", "Bishop", "Knight"};
				choiceHolder[0] = JOptionPane.showOptionDialog(
						this,
						"Choose promotion piece:",
						"Pawn Promotion",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null, options, options[0]
				);
			});
		} catch (Exception e) {
			choiceHolder[0] = 0;
		}

		String[] options = {"Queen", "Rook", "Bishop", "Knight"};
		int choice = choiceHolder[0];

		int col = pawn.getCol();
		int row = pawn.getRow();
		int color = pawn.getColor();

		Map<String, Class<? extends Piece>> pieceClassMap = new HashMap<>();
		pieceClassMap.put("Queen", Queen.class);
		pieceClassMap.put("Rook", Rook.class);
		pieceClassMap.put("Bishop", Bishop.class);
		pieceClassMap.put("Knight", Knight.class);

		String selected = (choice >= 0 && choice < options.length) ? options[choice] : "Queen";
		Class<? extends Piece> clazz = pieceClassMap.get(selected);

		Piece newPiece = null;
		try {
			newPiece = clazz.getConstructor(int.class, int.class, int.class).newInstance(color, col, row);
		} catch (Exception e) {
			e.printStackTrace();
			newPiece = new Queen(color, col, row); // fallback
		}

		simPieces.remove(pawn);
		simPieces.add(newPiece);
	}



	//method continuously updates the game state
	private void update() {
		if (gameOver) {
			// Game is over, no more moves allowed
			if (mouse.isClicked()) {
				mouse.resetClick();
			}
			return;
		}

		// If we have a new game state (check, checkmate), update the message
		if (gameState != GameState.getOngiong()) {
			updateStatusMessage();
		}

		// Process Mouse Clicks(Only run the move logic when the player clicks)
		if (mouse.isClicked()) {
			int col = mouse.getX() / Board.getSquareSize();
			int row = mouse.getY() / Board.getSquareSize();

			// Reset the click state after processing
			mouse.resetClick();

			if (activeP == null) {
				// First click: Select a piece of a correct color and col & row
				for (Piece piece : simPieces) {
					if (piece.getColor() == currentColor &&
							piece.getCol() == col &&
							piece.getRow() == row) {

						activeP = piece;
						//save original poistion
						startCol = piece.getCol();
						startRow = piece.getRow();
						activeP.setPreCol(startCol);
						activeP.setPreRow(startRow);

						break;
					}
				}
			}
			else {
				// Second click: Move the selected piece
				if (activeP.getCol() == col && activeP.getRow() == row) {
					// Clicked on the same piece again, deselect it
					activeP = null;
				}
				//Check if move is valid
				else if (board.isValidPosition(col, row) && isLegalMove(activeP, col, row)) {
					// Check if the target square is occupied
					Piece pieceAtTarget = null;
					for (Piece p : simPieces) {
						if (p != activeP && p.getCol() == col && p.getRow() == row) {
							if (p.getColor() == activeP.getColor()) {
								// Clicked on another piece of same color - change selection
								activeP = p;
								startCol = p.getCol();
								startRow = p.getRow();
								activeP.setPreCol(startCol);
								activeP.setPreRow(startRow);
								return;
							}
							pieceAtTarget = p;
							break;
						}
					}


					// Special case: Check if a king is capturing a protected piece
					boolean capturedProtectedPiece = false;
					if (activeP.isKing() && pieceAtTarget != null) {
						// Check if the piece is protected
						for (Piece defender : simPieces) {
							// Skip pieces of same color as king and the piece being captured
							if (defender.getColor() == activeP.getColor() || defender == pieceAtTarget) {
								continue;
							}

							// Can this piece protect the target?
							if (defender.canMove(col, row)) {
								// For pieces that need a clear path, check if the path is clear
								if (defender.needsClearPath()) {
									if (defender.checkPath(col, row)) {
										continue;
									}
								}

								// The piece is protected! King shouldn't capture it
								// Set capturedProtectedPiece to true but don't proceed with the move
								capturedProtectedPiece = true;
								activeP = null; // Deselect the piece
								return; // Exit the method without making the move
							}
						}
					}

					// Remove the captured piece safely
					if (pieceAtTarget != null) {
						simPieces.remove(pieceAtTarget);// remove from the arraylist
					}

					// Move the selected piece
					activeP.setCol(col);
					activeP.setRow(row);
					activeP.setX(col * Board.getSquareSize());
					activeP.setY(row * Board.getSquareSize());
					activeP.updatePosition();

					//  OCP friendly!
					activeP.checkPromotion(() -> handlePawnPromotion((Pawn) activeP));

					// Switching the colors btw White & Black
					if (currentColor == WHITE) {
						currentColor = BLACK;
					} else {
						currentColor = WHITE;
					}

					// Every completed move counts down one ply of shield
					// protection for every piece still holding one. This
					// MUST run before the power-up trigger below, so that a
					// shield picked up THIS move is not immediately ticked
					// back down to zero before the opponent even gets a
					// turn - it needs to survive exactly one full ply.
					for (Piece p : simPieces) {
						p.tickShield();
					}

					// Customization layer: trigger a power-up if the piece
					// just landed on one (Section: Hazards & Power-ups).
					// GamePanel only calls the SpecialSquare interface here
					// - it never checks "is this an ObstacleSquare?" or
					// "is this a ShieldPowerUp?", so a brand-new hazard
					// type would need zero changes in this method.
					SpecialSquare landedOn = Board.getSpecialSquare(col, row);
					if (landedOn != null) {
						landedOn.onEnter(activeP, this);
					}

					activeP = null; //Deselect piece after move.



					// Only check for check/checkmate if we didn't just capture a protected piece
					if (!capturedProtectedPiece) {
						// to check if king is under attack [ Association ]
						boolean kingInCheck = GameState.isKingInCheck(simPieces, currentColor);

						if (kingInCheck) {

							// Use the improved checkmate detection
							if (isCheckmate()) {
								gameState = GameState.getCheckMate();
								gameOver = true;
								System.out.println("CHECKMATE detected!");
							}
							else {
								gameState = GameState.getCheck();
								System.out.println("CHECK - King can still escape");
							}
						}
						else {
							// Not in check, but check
							boolean hasLegalMoves = false;

							// Check if any piece has a legal move
							for (Piece p : simPieces) {
								if (p.getColor() == currentColor) {
									//row loop
									for (int r = 0; r < 8; r++) {
										//col loop
										for (int c = 0; c < 8; c++) {
											if (isLegalMove(p, c, r)) {
												hasLegalMoves = true;
												break;
											}
										}
									}
								}
							}

							if (hasLegalMoves) {
								gameState = GameState.getOngiong();
							}
							else {
								gameState = GameState.getStalemate();
								gameOver = true;
								System.out.println("STALEMATE - no legal moves but not in check");
							}
						}
					}

					// Update status message based on new game state
					updateStatusMessage();

					// Customization layer (Section: Game Logic Extensions):
					// give the active rule variant a chance to declare a
					// winner through a condition beyond checkmate/stalemate
					// (e.g. King of the Hill). currentColor has already
					// been flipped above, so the player who just moved is
					// whichever color is NOT currentColor right now.
					if (!gameOver) {
						int moverColor = (currentColor == WHITE) ? BLACK : WHITE;
						String specialResult = activeRule.checkSpecialWinCondition(simPieces, moverColor);
						if (specialResult != null) {
							gameOver = true;
							statusMessage = specialResult;
						}
					}

					// If game is over, print message
					if (gameOver) {
						// Switch turn(ternary operator)condition ? valueIfTrue : valueIfFalse;
						if (gameState == GameState.getCheckMate()) {
							System.out.println("GAME OVER: Checkmate");
						} else if (gameState == GameState.getStalemate()) {
							System.out.println("GAME OVER: Stalemate");
						} else {
							// A GameRule variant (e.g. King of the Hill)
							// ended the game through its own win condition.
							System.out.println("GAME OVER: " + statusMessage);
						}
					}
				} else {
					// Invalid move - either clicked elsewhere on board or clicked invalid destination
					// Check if clicked on another piece of same color
					for (Piece piece : simPieces) {
						if (piece.getColor() == currentColor &&
								piece.getCol() == col &&
								piece.getRow() == row) {

							activeP = piece;
							startCol = piece.getCol();
							startRow = piece.getRow();
							activeP.setPreCol(startCol);
							activeP.setPreRow(startRow);
							return;
						}
					}

					// Clicked on empty square or opponent's piece that can't be reached - deselect
					activeP = null;
				}
			}
		}
	}

	//helper method
	private final Map<Integer, Function<Integer, String>> statusMap = Map.of(
			0, c -> "",
			1, c -> (c == WHITE ? "White" : "Black") + " is in check!",
			2, c -> "Checkmate! " + (c == WHITE ? "Black" : "White") + " wins!",
			3, c -> "Stalemate!"
	);

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // parent class repaint the component
		Graphics2D g2 = (Graphics2D)g; // Converts Graphics to Graphics2D

		// Draw board
		board.draw(g2);

		// Customization layer: show which rule variant is active.
		g2.setFont(new Font("Arial", Font.PLAIN, 14));
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawString("Rule: " + activeRule.getName(), 8 * Board.getSquareSize() + 20, 30);

		// If a piece is selected, highlight its position and valid moves
		if (activeP != null) {
			// Highlight selected piece position with yellow
			g2.setColor(new Color(255, 255, 0, 100));  // Semi-transparent yellow
			g2.fillRect(activeP.getCol() * Board.getSquareSize(), activeP.getRow() * Board.getSquareSize(),
					Board.getSquareSize(), Board.getSquareSize());

			// Highlight valid moves with light blue
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if (isLegalMove(activeP, col, row)) {
						g2.setColor(new Color(100, 180, 255, 80));  // Semi-transparent light blue
						g2.fillRect(col * Board.getSquareSize(), row * Board.getSquareSize(),
								Board.getSquareSize(), Board.getSquareSize());
					}
				}
			}
		}

		// Highlight king in red if it's in check or checkmate
		if (gameState == GameState.getCheck() || gameState == GameState.getCheckMate()) {
			for (Piece p : simPieces) {
				if (p.isKing() && p.getColor() == currentColor) {
					g2.setColor(new Color(255, 0, 0, 100));  // Semi-transparent red
					g2.fillRect(p.getCol() * Board.getSquareSize(), p.getRow() * Board.getSquareSize(),
							Board.getSquareSize(), Board.getSquareSize());
					break;
				}
			}
		}

		// Draw all pieces
		for (Piece p : simPieces) {
			p.draw(g2);// calls for each piece
		}

		// Draw game status message
		if (!statusMessage.isEmpty()) {
			g2.setFont(new Font("Arial", Font.BOLD, 24));
			g2.setColor(Color.WHITE);

			// Draw message on the right side of the board
			int messageX = 8 * Board.getSquareSize() + 20;
			int messageY = 100;

			g2.drawString(statusMessage, messageX, messageY);

			// Draw additional message for game over
			if (gameOver) {
				g2.setFont(new Font("Arial", Font.BOLD, 18));
				g2.drawString("Game Over", messageX, messageY + 40);
				g2.drawString("Restart the application", messageX, messageY + 70);
				g2.drawString("to play again", messageX, messageY + 100);
			}
		}

		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Segoe UI", Font.PLAIN, 40));
		if (!gameOver) {
			if (currentColor == WHITE) {
				g2.drawString("White's turn", 840, 550);
			} else {
				g2.drawString("Black's turn", 840, 250);
			}
		}
	}

	// in king class
	public static boolean isKingInCheck(int kingColor) {

		return GameState.isKingInCheck(simPieces, kingColor);
	}

	private void updateStatusMessage() {
		statusMessage = statusMap.getOrDefault(gameState, c -> "").apply(currentColor);
	}



	/**
	 * isLegalMove method will allow kings to capture protected pieces,
	 */
	private boolean isLegalMove(Piece piece, int targetCol, int targetRow) {
		// First, check if the move is valid according to the piece's movement rules
		if (!piece.canMove(targetCol, targetRow)) {
			return false;
		}

		// Customization layer: an obstacle square (Section: Hazards &
		// Power-ups) is never a legal destination, no matter what the
		// piece's own canMove() rule says.
		SpecialSquare destinationSquare = Board.getSpecialSquare(targetCol, targetRow);
		if (destinationSquare != null && destinationSquare.blocksMovement()) {
			return false;
		}

		// Check for same-color piece at target
		for (Piece p : simPieces) {
			if (p != piece && p.getCol() == targetCol && p.getRow() == targetRow) {
				if (p.getColor() == piece.getColor()) {
					return false;
				}
				if (p.isKing()) {
					return false;
				}
				// Customization layer: a shielded enemy piece (Section:
				// Hazards & Power-ups) cannot be captured this ply.
				if (p.isShielded()) {
					return false;
				}
			}
		}

		// Special check for kings: Cannot capture protected pieces
		if (piece.isKing()) {
			// Check if there's an enemy piece at the target position
			Piece targetPiece = null;
			for (Piece p : simPieces) {
				if (p != piece && p.getCol() == targetCol && p.getRow() == targetRow) {
					targetPiece = p;
					break;
				}
			}

			// If there's a piece to capture, check if it's protected
			if (targetPiece != null) {
				// Check if any enemy piece can move to this square (i.e., it's protected)
				for (Piece defender : simPieces) {
					// Skip pieces of same color as king and the piece being captured
					if (defender.getColor() == piece.getColor() || defender == targetPiece) {
						continue;
					}

					// Can this piece protect the target?
					if (defender.canMove(targetCol, targetRow)) {
						// For pieces that need a clear path, check if the path is clear
						if (defender.needsClearPath()) {							// Check if the path is blocked
							boolean pathBlocked = defender.checkPath(targetCol, targetRow);
							if (pathBlocked) {
								continue; // Path is blocked, can't protect
							}
						}

						// The piece is protected! King can't capture it
						return false;
					}
				}
			}
		}

		// Standard check for all pieces: make sure move doesn't leave own king in check
		// Save the original position
		int originalCol = piece.getCol();
		int originalRow = piece.getRow();

		// Capturing any piece at the target position
		Piece capturedPiece = null;
		for (Piece p : simPieces) {
			if (p != piece && p.getCol() == targetCol && p.getRow() == targetRow) {
				capturedPiece = p;
				break;
			}
		}

		// Create a copy of the pieces list for simulation
		ArrayList<Piece> simulatedPieces = new ArrayList<>(simPieces);

		// Temporarily remove captured piece if it exists
		if (capturedPiece != null) {
			simulatedPieces.remove(capturedPiece);
		}

		// Temporarily move the piece in our simulation
		piece.setCol(targetCol);
		piece.setRow(targetRow);

		// Check if this move would leave our king in check
		boolean kingInCheck = GameState.isKingInCheck(simulatedPieces, piece.getColor());

		// Restore the original position
		piece.setCol(originalCol);
		piece.setRow(originalRow);

		// The move is legal only if it doesn't leave the king in check
		return !kingInCheck;
	}
	/**
	 * Special method to accurately determine if the game is in checkmate
	 */
	private boolean isCheckmate() {
		// First, verify that the current player's king is in check [ Association ]
		boolean kingInCheck = GameState.isKingInCheck(simPieces, currentColor);
		if (!kingInCheck) {
			return false; // Not checkmate if the king isn't in check
		}

		System.out.println("Checking for checkmate - king is in check");

		// Find the king
		King king = null;
		for (Piece p : simPieces) {
			if (p.isKing() && p.getColor() == currentColor) {
				king = (King) p;
				break;
			}
		}

		if (king == null) {
			System.err.println("ERROR: King not found for color " + currentColor);
			return false;
		}

		// Check all possible king moves including captures
		int kingCol = king.getCol();
		int kingRow = king.getRow();

		// Try all 8 possible king moves
		int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
		int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

		for (int i = 0; i < 8; i++) {
			int newCol = kingCol + dx[i];
			int newRow = kingRow + dy[i];

			// Skip moves outside the board
			if (newCol < 0 || newCol > 7 || newRow < 0 || newRow > 7) {
				continue;
			}

			System.out.println("Checking if king can move to " + newCol + "," + newRow);

			// Check if this move is legal
			if (isLegalMove(king, newCol, newRow)) {
				System.out.println("King can escape check by moving to " + newCol + "," + newRow);
				return false; // King can escape, not checkmate
			}
		}

		// Check if any piece can block the check or capture the attacking piece
		for (Piece p : simPieces) {
			// Only consider current player's pieces
			if (p.getColor() != currentColor || p.isKing()) {
				continue;
			}
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if (isLegalMove(p, col, row)) {
						System.out.println("Piece " + p.getClass().getSimpleName() +
								" at " + p.getCol() + "," + p.getRow() +
								" can block/capture by moving to " + col + "," + row);
						return false; // A piece can block or capture, not checkmate
					}
				}
			}
		}

		// If we get here, no escape moves were found - it's checkmate
		System.out.println("CHECKMATE confirmed - no legal moves found");
		return true;
	}

}