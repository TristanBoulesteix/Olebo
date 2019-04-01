package jdr.exia;

import jdr.exia.controler.IControler;
import jdr.exia.model.IModel;
import jdr.exia.view.IView;

public class Controler implements IControler {
	private final IView viewManager;
	private final IModel model;

	public Controler(IView viewManager, IModel model) {
		this.viewManager = viewManager;
		this.model = model;
	}

	@Override
	public void initDatas() {

	}

	@Override
	public void start() {

	}
}
