package com.cube.core;

import java.nio.FloatBuffer;
import javax.vecmath.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Light {

	private String name;
	private static FloatBuffer position;
	public static Vector3d worldPosition;
	private static FloatBuffer color; 
	private int oglID;
	private Object obj;
	
	private long lastCheck;
	private float maxY;
	
	public Light(String str) {
		name = str;
		color 			= BufferUtils.createFloatBuffer(4);
		position 		= BufferUtils.createFloatBuffer(4);
		worldPosition 	= new Vector3d(0, 0, 0);
		
		lastCheck = System.nanoTime();
	}

	public void loadOBJ(String filename) {
		obj = new Object();
		obj.file = filename;
		Resources.loadLocalFile(filename, obj);
	}
	
	public void draw() {
		
		progressDayNight();
		
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glColor4f(color.get(0), color.get(1), color.get(2), color.get(3));
			GL11.glTranslated(worldPosition.x, worldPosition.y, worldPosition.z);
			obj.drawOBJ();
		GL11.glPopMatrix();
		
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, position);	// sets light position
	}
	
	public void moveSun(float a) {
		Physics.rotateVector3d(worldPosition, 0, 0, a);
		position.put((float) worldPosition.x).put((float) worldPosition.y).put((float) worldPosition.z).put(0f).flip();
	}

	public void progressDayNight() {
		
		if ((System.nanoTime() - lastCheck) >= 62500000) { //Every 1/8 second, move sun .1 degree
			lastCheck = System.nanoTime();
			Physics.rotateVector3d(worldPosition, 0, 0, .05f);
			position.put((float) worldPosition.x).put((float) worldPosition.y).put((float) worldPosition.z).put(0f).flip();
			
			if (worldPosition.y >= 0) {
				color.put(1.0f).put(1.0f).put((float)worldPosition.y/maxY).put(1.0f).flip();
			}
		}
	}
	
	public void setPosition(float x, float y, float z) {
		maxY = y;
		worldPosition.set(x, y, z);
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
