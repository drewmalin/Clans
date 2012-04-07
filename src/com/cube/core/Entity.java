package com.cube.core;

import org.lwjgl.opengl.GL11;

public class Entity {
	public int id;
	public float[] position;
	public float[] color;
	public float[] rotation;
	public float scale;
	public boolean show;

	public Entity() {
		position 	= new float[3];
		color 		= new float[3];
		rotation 	= new float[3];
		
		position[0] = position[1] = position[2] = 0f;
		color[0] = color[1] = color[2] = 0f;
		rotation[0] = rotation[1] = rotation[2] = 0f;
		
		scale = .1f;
		show = true;
		id = -1;
	}
	
	public void draw() {
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glTranslatef(position[0], position[1], position[2]);
		GL11.glRotatef(rotation[0], 1, 0, 0);
		GL11.glRotatef(rotation[1], 0, 1, 0);
		GL11.glRotatef(rotation[2], 0, 0, 1);
		GL11.glScalef(scale, scale, scale);
		Resources.objectLibrary[id].draw();
		GL11.glPopMatrix();
	}
}
