package com.cube.core;

public class Object extends Drawable {
	
	public int id;
	public String file;
	
	public Object() {
		id = -1;
		file = null;
	}

	@Override
	public void draw() {
		drawOBJ();
	}
}
