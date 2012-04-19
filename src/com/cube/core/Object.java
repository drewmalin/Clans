package com.cube.core;

import com.cube.util.Texture;
import org.lwjgl.opengl.GL11;

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
	
	public void draw(Texture tex)
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		tex.bind();
		drawOBJ();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
}
