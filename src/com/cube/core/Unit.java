package com.cube.core;

import org.lwjgl.opengl.GL11;
import com.cube.util.Texture;
import com.cube.util.ShaderManager.ShaderType;

public class Unit extends Entity {
			
	public Unit(int _type, int _id, Clan c, Texture tex) {
		type = _type;
		objectID = _id;
		clanRef = c;
		
		show = true;

		this.tex = tex;
	}
	
	public void update(int timeElapsed) {
		super.update(timeElapsed);
	}
	
	@Override
	public void draw() {
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			
			GL11.glTranslated(position.x, position.y, position.z);
			GL11.glRotatef(rotation[0], 1, 0, 0);
			GL11.glRotatef(rotation[1], 0, 1, 0);
			GL11.glRotatef(rotation[2], 0, 0, 1);
			GL11.glScalef(scale, scale, scale);

			if (Graphics.colorPicking) {
				GL11.glColor3ub((byte)colorID[0], (byte)colorID[1], (byte)colorID[2]);
				Resources.objectLibrary[objectID].drawOBJ();
			}
			else {
				GL11.glColor3f(color[0], color[1], color[2]);
				
				//Bind shaders
				Graphics.shaderManager.bindShader(ShaderType.HEMISPHERE);
				if(tex == null) {
					Resources.objectLibrary[objectID].draw();
				}else{
					Resources.objectLibrary[objectID].draw(tex);
				}
				//Unbind shaders
				Graphics.shaderManager.unbindShader(ShaderType.HEMISPHERE);
				
				inventory.draw(this);
			}
		GL11.glPopMatrix();
	}
}
