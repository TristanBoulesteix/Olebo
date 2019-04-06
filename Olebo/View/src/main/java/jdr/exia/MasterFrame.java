package jdr.exia;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

	private ItemPanel itemPanel; // Will contain list of available items

	private JPanel masterFramePanel; /*
										 * Simply the frame's BackGround, it's use is to serve as a general layout for
										 * smaller panels (multiple panels can't be put straight into a new frame, there
										 * needs to be a global panel and layout first)
										 */

	public static MasterFrame getInstance() {
		if (instance == null) {
			instance = new MasterFrame();
		}

		return instance;
	}

	private MasterFrame() {

		this.initialize();

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

	// getters and setters

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

	private void initialize() {

		this.setTitle("Master");
		this.setSize(1936, 1056);
		addKeyListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mapPanel = new MapPanel();
		mapPanel.setSize(1280, 720);

		selectPanel = SelectPanel.getInstance();
		selectPanel.setSize(100, 100);
		selectPanel.setBackground(Color.green);

		itemPanel = ItemPanel.getInstance();
		itemPanel.setSize(100, 100);
		itemPanel.setBackground(Color.yellow);

		/*
		 
		 */
		masterFramePanel = new JPanel(new GridBagLayout());
		masterFramePanel.setSize(this.getSize());
		masterFramePanel.setBackground(Color.GRAY);
		this.setContentPane(masterFramePanel);

		GridBagConstraints mapConstraints = new GridBagConstraints();
		GridBagConstraints itemConstraints = new GridBagConstraints();
		GridBagConstraints selectConstraints = new GridBagConstraints();

		itemConstraints.gridx = 0;
		itemConstraints.gridy = 0;
		itemConstraints.gridheight = 2;
		itemConstraints.weightx = 1;
		itemConstraints.weighty = 2;
		itemConstraints.fill = GridBagConstraints.BOTH;

		selectConstraints.gridx = 1;
		selectConstraints.gridy = 1;
		selectConstraints.weightx = 0.5;
		selectConstraints.weighty = 1;
		selectConstraints.gridwidth = GridBagConstraints.REMAINDER;
		selectConstraints.fill = GridBagConstraints.BOTH;

		mapConstraints.gridx = 3;
		mapConstraints.gridy = 0;
		mapConstraints.weightx = 3;
		mapConstraints.weighty = 5;
		mapConstraints.fill = GridBagConstraints.BOTH;

		masterFramePanel.add(mapPanel, mapConstraints);
		masterFramePanel.add(itemPanel, itemConstraints);
		masterFramePanel.add(selectPanel, selectConstraints);
	}
}
