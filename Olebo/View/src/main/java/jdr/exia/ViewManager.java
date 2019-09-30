package jdr.exia;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import jdr.exia.view.IView;

/*ViewManager is View's facade
this is a singleton*/
public class ViewManager implements IView {

	private static ViewManager instance;
	
	private PlayerFrame playerFrame;
	
	private MasterFrame masterFrame;
	
	
	
	private ViewManager() {

		this.initializeActFrames();
		this.test();
	}
	
	void test() {
		
		
	}
	
	
	void refreshFrames() {  // Refreshes Player and MasterFrames at once
		masterFrame.getMapPanel().refresh();
	}

	
	private void initializeActFrames() { /*this method activates the Player and GM frames to initiate/start back an act	*/
		
		this.playerFrame = PlayerFrame.getInstance();
		this.masterFrame = MasterFrame.getInstance();

		
		
		GraphicsDevice[] screens = this.getScreens();
		
		/* if there are 2 screens or more, the the 1st one is GM's screen, and the 2nd
		is the players' screen.*/
		if (screens.length >= 2) {
			
			playerFrame.setUndecorated(true);
			masterFrame.setUndecorated(true);
			screens[1].setFullScreenWindow(playerFrame);
			screens[0].setFullScreenWindow(masterFrame);
			masterFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			playerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			
			/* if there's only 1 screen, we assume the act is played for testing purposes,
			and we make both frames decorated*/
		} else if (screens.length == 1) {
			
			playerFrame.setUndecorated(false);
			masterFrame.setUndecorated(false);
			screens[0].setFullScreenWindow(playerFrame);
			screens[0].setFullScreenWindow(masterFrame);
			masterFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			playerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		} else {
			throw new RuntimeException("No Screens Found");
		}

	}
	
	
	
	/* these 2 lines generate a GraphicsDevice array, GraphicsDevice are screens*/
	private GraphicsDevice[] getScreens()
	{
		
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return  graphicsEnvironment.getScreenDevices();
	}
	
	

	public static ViewManager getInstance() {

		if (instance == null) {
			instance = new ViewManager();
		}
		return instance;
	}
	
	
	
	
	
	/* Code supposed to be in Controller, but due to the non-existence of the Controller, the view is kindly sheltering these lines of code */
	private Object clickedElement; //this is the last Element that was clicked on, if it is equal to NULL, the program tries to fill this slot with an object	
}


