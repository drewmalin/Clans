package com.cube.core;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Light {

	private String name;
	private static FloatBuffer position;
	private static FloatBuffer color; 
	private int oglID;
	
	public Light(String str) {
		name = str;
		color 			= BufferUtils.createFloatBuffer(4);
		position 		= BufferUtils.createFloatBuffer(4);
	}

	public void draw() {
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, position);	// sets light position
	}
	
	public void setPosition(float x, float y, float z) {
		position.put(x).put(y).put(z).put(0f).flip();
	}

	public void setColor(float f, float g, float h, float i) {
		color.put(f).put(g).put(h).put(i).flip();
	}

	public void create(int glLight) {	
		
		oglID = glLight;
		
		GL11.glLight(glLight, GL11.GL_SPECULAR, color);		// sets specular light to white
		GL11.glLight(glLight, GL11.GL_DIFFUSE, color);		// sets diffuse light to white
	
		enable();											// enables light0		

	}
	
	public void enable() {
		GL11.glEnable(oglID);
	}
	
	public void disable() {
		GL11.glDisable(oglID);
	}
	
	public String getName() {
		return name;
	}
}
