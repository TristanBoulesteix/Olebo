package jdr.exia.view;

import javax.swing.JFrame;


/*PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
this is a singleton*/
public class PlayerFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4392066710053478753L;
	private MapPanel mapPanel;
	private static PlayerFrame instance;
	
	
	private PlayerFrame() {
		this.mapPanel = new MapPanel();
		this.setTitle("Player");
		this.setResizable(false);
		this.setSize(1936,1056);
		this.setContentPane(mapPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		
	}
	
	
	static PlayerFrame getInstance() {
		
		if(instance == null)
		{
			instance = new PlayerFrame();
		}
		
		
		return instance;
		
	}


	public MapPanel getMapPanel() {
		return mapPanel;
	}


	public void setMapPanel(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
	}

}
