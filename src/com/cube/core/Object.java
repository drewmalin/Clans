package com.cube.core;

import com.cube.util.ShaderManager.ShaderType;
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
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Graphics.shaderManager.bindShader(ShaderType.HEMISPHERE);
		Resources.textures.get(1).bind();
		drawOBJ();
		Graphics.shaderManager.unbindShader(ShaderType.HEMISPHERE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void draw(Texture tex)
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Graphics.shaderManager.bindShader(ShaderType.HEMISPHERE);
		tex.bind();
		drawOBJ();
		Graphics.shaderManager.unbindShader(ShaderType.HEMISPHERE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
}
