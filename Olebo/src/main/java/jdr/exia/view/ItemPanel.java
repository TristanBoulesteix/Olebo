package jdr.exia.view;

import java.awt.Graphics;

import javax.swing.JPanel;


/*This panel is intended to contain the entire list of items that the Game master can use*/
//this is a singleton
public class ItemPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -4434335768430831055L;
    private static ItemPanel instance;


    private ItemPanel() {

    }


    static ItemPanel getInstance() {
        if (instance == null) {
            instance = new ItemPanel();
        }
        return instance;
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // graphics.drawImage(,X,Y,null);

    }


}
