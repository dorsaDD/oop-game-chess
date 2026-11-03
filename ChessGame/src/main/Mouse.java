package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class Mouse extends MouseAdapter {
	private int x, y;
	private boolean clicked;
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public boolean isClicked() {
		return clicked;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// When mouse is pressed, record position but set clicked
		x = e.getX();
		y = e.getY();
		clicked = true;
	}

	// Method to reset the click state after it's been processed
	public void resetClick() {

		clicked = false;
	}
}