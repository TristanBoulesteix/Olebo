package jdr.exia;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*MasterFrame is the Game Master's Interface, it contains a Map panel (the same as PlayerFrame), an ItemPanel and a SelectPanel.
 * MasterFrame will be focused most of the time, so it contains all KeyListeners for the program
this is a singleton*/
public class MasterFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 3204299780560212927L;

	private static MasterFrame instance;
	private MapPanel mapPanel; // similar to PlayerFrame's MapPanel, must stay synced
	private SelectPanel selectPanel; // Will contain all info on selected Item
	private ItemPanel itemPanel; // Will contain list of availaible items
	private JPanel masterFramePanel; /* Simply the frame's BackGround, it's use is to serve as a general layout for
										 smaller panels (multiple panels can't be put straight into a new frame, there
										 needs to be a global panel and layout first)*/

	public static MasterFrame getInstance() {
		if (instance == null) {
			instance = new MasterFrame();
		}

		return instance;
	}

	private MasterFrame() {
		
		this.masterFramePanel = new JPanel();
		this.mapPanel = new MapPanel();
		this.setTitle("Master");
		this.setSize(1936, 1056);
		addKeyListener(this);
		this.mapPanel.setSize(1280, 720);
		this.setContentPane(masterFramePanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	

	// KeyListener section, to add Key bindings
	@Override
	public void keyTyped(KeyEvent keyEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		switch (keyEvent.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			this.dispose();
			System.exit(0);
			break;

		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		// TODO Auto-generated method stub

	}
	
	//getters and setters 
	
	public ItemPanel getItemPanel() {
		return itemPanel;
	}

	public void setItemPanel(ItemPanel itemPanel) {
		this.itemPanel = itemPanel;
	}

	public SelectPanel getSelectPanel() {
		return selectPanel;
	}

	public void setSelectPanel(SelectPanel selectPanel) {
		this.selectPanel = selectPanel;
	}

	public MapPanel getMapPanel() {
		return mapPanel;
	}

	public void setMapPanel(MapPanel mapPanel) {
		this.mapPanel = mapPanel;
	}
}
