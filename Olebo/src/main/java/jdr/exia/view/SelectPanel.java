package jdr.exia.view;

import java.awt.Graphics;

import javax.swing.JPanel;

// contains all this info regarding the item selected by the Game Master
//this is a singleton

public class SelectPanel extends JPanel{
/**
	 * 
	 */
	private static final long serialVersionUID = 4258928035901251486L;
private static SelectPanel instance;

private SelectPanel() {

	
}

public static SelectPanel getInstance() {
	if (instance == null) {
		instance = new SelectPanel();
	
	}
	
	return instance;
}



void refresh() { // refreshes the panel's content
	this.repaint();
}

public void paintComponent(Graphics graphics) {
	super.paintComponent(graphics);

	// graphics.drawImage(,X,Y,null);

}

}
