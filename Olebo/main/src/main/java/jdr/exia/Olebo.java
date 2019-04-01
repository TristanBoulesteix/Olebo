package jdr.exia;

import jdr.exia.controler.IControler;
import jdr.exia.model.IModel;
import jdr.exia.model.Model;
import jdr.exia.view.IView;

public class Olebo {
	public static void main(String[] args) {
		// Instantiation of the MVC and start of the program
		IModel model = new Model();
		IView viewManager = new ViewManager();
		IControler controler = new Controler(viewManager, model);
		controler.initDatas();
		controler.start();

		// Hello world !
		System.out.println("Hello world !");
	}
}
