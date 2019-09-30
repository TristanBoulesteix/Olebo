package jdr.exia;

import javax.swing.JFrame;

// This is the menu that appears upon launch, it allows the user to chose what to do.
//this is a singleton
public class PopupFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1288811587686252103L;
	private static PopupFrame instance;
	
	private PopupFrame() {
		
	}

	public static PopupFrame getInstance() {
		
		if(instance == null) {
			instance = new PopupFrame();
		}
		
		
		return instance;
		
	}
}
