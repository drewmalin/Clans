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
		/*TODO
		 * Update the y rotation of the unit based on the direction vector
		 */
	}
	
	public void draw() {
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glColor3f(color[0], color[1], color[2]);
			GL11.glTranslatef(position[0], position[1], position[2]);
			GL11.glRotatef(rotation[0], 1, 0, 0);
			GL11.glRotatef(rotation[1], 0, 1, 0);
			GL11.glRotatef(rotation[2], 0, 0, 1);
			GL11.glScalef(scale, scale, scale);
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
		GL11.glPopMatrix();
	}
}
