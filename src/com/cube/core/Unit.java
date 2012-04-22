package com.cube.core;

import org.lwjgl.opengl.GL11;
import com.cube.util.Texture;

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
			if(tex == null) {
				Resources.objectLibrary[objectID].draw();
			}else{
				Resources.objectLibrary[objectID].draw(tex);
			}
			inventory.draw(this);
			
			GL11.glColor3f(1.0f, 0f, 0f);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2d(2 * velocity.x + position[0], 2 * velocity.y + position[2]);
			GL11.glVertex2d(position[0], position[2]);
			GL11.glEnd();
		GL11.glPopMatrix();
	}
}
