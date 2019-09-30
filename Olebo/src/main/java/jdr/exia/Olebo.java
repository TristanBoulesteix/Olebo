package jdr.exia;

import jdr.exia.controller.Controller;
import jdr.exia.model.Model;
import jdr.exia.view.ViewManager;

public class Olebo {
    public static void main(String[] args) {
        Model model = new Model();
        ViewManager viewManager = ViewManager.getInstance();
        Controller controller = new Controller(viewManager, model);
        controller.initDatas();
        controller.start();
    }
}
