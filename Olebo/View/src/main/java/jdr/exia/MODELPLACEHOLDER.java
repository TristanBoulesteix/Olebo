package jdr.exia;

import java.awt.Image;

public class MODELPLACEHOLDER {
	
	private static MODELPLACEHOLDER instance;
	
	private ElementPlaceHolder[] elements; // the array containing the different sprites to display 
	
	public MODELPLACEHOLDER() {
		
		elements = new ElementPlaceHolder[1];
		
		elements[0] = new ElementPlaceHolder();
	}
	
	public static MODELPLACEHOLDER getInstance() {
		if(instance == null)
		{
			instance = new MODELPLACEHOLDER();
		}else{}
		return instance;
		}
	
	
	public Image[] toSprites() { // Turns an array of elements to a corresponding
		
		int length = elements.length;
		
		
		
		
		return null;
	}
	
	
}
