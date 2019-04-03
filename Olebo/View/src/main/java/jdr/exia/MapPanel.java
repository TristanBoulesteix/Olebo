package jdr.exia;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

// This panel contains the map and all the objects placed within it

public class MapPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1127620292584522731L;

	public MapPanel() {
		this.setBackground(Color.blue);
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		// graphics.drawImage(,X,Y,null);

	}

}
