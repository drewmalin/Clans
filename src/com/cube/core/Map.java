package com.cube.core;

import org.lwjgl.opengl.GL11;

public class Map extends Drawable {

	private int size;
	public int scale;
	public String file;
	public int[] colorID;
	
	public Map(int _size) {
		size = _size;
		colorID = new int[3];
	}
	
	@Override
	public void draw() {
		drawGrid();
	}
	
	/*
	 * Method to draw a red grid along the y=0 plane. Setting the size parm (passed to the
	 * Map's constructor) determines how large the grid will be.
	 */
	private void drawGrid() {
		
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		
		GL11.glColor3f(.9f, 0f, 0f);

		GL11.glBegin(GL11.GL_LINES);
		
		for (int x = -size; x <= size; x++) {

			GL11.glVertex3d((double)x, 0d, -size);
			GL11.glVertex3d((double)x, 0d, size);

		}
		
		for (int z = -size; z <= size; z++) {
			
			GL11.glVertex3d(size, 0d, (double)z);
			GL11.glVertex3d(-size, 0d, (double)z);

		}
		
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}

}