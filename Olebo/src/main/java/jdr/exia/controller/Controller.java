package jdr.exia.controller;

import jdr.exia.model.Model;
import jdr.exia.view.ViewManager;

import javax.swing.text.View;

public class Controller {
    private final ViewManager viewManager;
    private final Model model;

    public Controller(ViewManager viewManager, Model model) {
        this.viewManager = viewManager;
        this.model = model;
    }

    public void initDatas() {
        model.loadDatabase();
    }


    public void start() {

    }
}
