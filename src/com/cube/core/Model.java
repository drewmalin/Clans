package com.cube.core;

import com.cube.util.GeometryGroup;
import com.cube.util.OBJParser;
import com.cube.util.ShaderManager;
import com.cube.util.Texture;
import org.lwjgl.opengl.GL11;

public class Model extends Drawable {
	
	public String file;
	public String name;
	
	public Model(String filename) {
		file = filename;
		name = filename.substring(filename.lastIndexOf("/")+1, filename.indexOf("."));
		
		OBJParser parser = new OBJParser(filename);
		vertexArray = parser.v;
		vertexNormalArray = parser.vn;
		textureArray = parser.t;
		for(GeometryGroup gg : parser.ggs) {
			geoGroups.add(gg);
		}
		setupVBO();
	}

	@Override
	public void draw() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Graphics.shaderManager.bindShader(ShaderManager.HEMISPHERE);
		Resources.textureLibrary.get("default").bind();
		drawOBJ();
		Graphics.shaderManager.unbindShader(ShaderManager.HEMISPHERE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void draw(Texture tex)
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Graphics.shaderManager.bindShader(ShaderManager.HEMISPHERE);
		tex.bind();
		drawOBJ();
		Graphics.shaderManager.unbindShader(ShaderManager.HEMISPHERE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
}
