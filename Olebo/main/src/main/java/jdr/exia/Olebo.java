package jdr.exia;

import jdr.exia.controller.IController;
import jdr.exia.model.IModel;
import jdr.exia.view.IView;

public class Olebo {
	public static void main(String[] args) {
		// Instantiation of the MVC and start of the program
		IModel model = new Model();
		IView viewManager = new ViewManager();
		IController controller = new Controller(viewManager, model);
		controller.start();

		// Hello world !
		System.out.println("Hello world !");
	}
}
