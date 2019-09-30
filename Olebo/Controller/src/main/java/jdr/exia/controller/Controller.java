package jdr.exia.controller;

import jdr.exia.controller.IController;
import jdr.exia.model.IModel;
import jdr.exia.view.IView;

public class Controller implements IController {
	private final IView viewManager;
	private final IModel model;

	public Controller(IView viewManager, IModel model) {
		this.viewManager = viewManager;
		this.model = model;
	}

	@Override
	public void initDatas() {
		model.loadDatabase();
	}

	@Override
	public void start() {

	}

	@Override
	public void initDatas() {

	}
}
