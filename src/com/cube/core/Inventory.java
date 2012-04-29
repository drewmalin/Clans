package com.cube.core;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

public class Inventory {
	
	private int capacity;
	private int itemCount;
	private int renderDimension;
	public ArrayList<Item> items;

	private double startX;
	private double startY;
	private double startZ;
	
	public Inventory() {
		items = new ArrayList<Item>();
		capacity = renderDimension = 0;
		startX = startY = startZ = 0;
	}
	
	public boolean isFull() {
		return (itemCount == capacity);
	}
	
	public boolean isEmpty() {
		return (itemCount == 0);
	}
	
	public void addItem() {
		if (itemCount < capacity)
			itemCount++;
	}
	
	public void removeItem() {
		if (itemCount > 0)
			itemCount--;
	}
	
	public void setCap(int c) {
		capacity = c;
		renderDimension = (int) Math.ceil(Math.pow(capacity, 1.0/3.0));
	}
	
	public void fill() {
		itemCount = capacity;
	}
	
	public void empty() {
		itemCount = 0;
	}
	
	public int count() {
		return itemCount;
	}
	
	public void draw(Entity e) {
		
		float spacing = .2f;
		
		startX = e.position.x + e.direction.x;
		startY = e.position.y + 1 + Resources.map.getHeight((float)e.position.x, (float)e.position.z);
		startZ = e.position.z + e.direction.z;
			
		GL11.glPushMatrix();

			for (int i = 0; i < itemCount; i++) {
				
				GL11.glLoadIdentity();
				GL11.glColor3f(1.0f, 1.0f, 1.0f);
								
				GL11.glTranslated(startX, startY, startZ);
								
				GL11.glRotatef(e.rotation[0], 1, 0, 0);
				GL11.glRotatef(135 + e.rotation[1], 0, 1, 0);
				GL11.glRotatef(e.rotation[2], 0, 0, 1);
				
				GL11.glTranslatef(0 + (spacing * (i % renderDimension)), 
						          0 + (spacing * (int) (i / (renderDimension * renderDimension))),
						          0 + (spacing * (((int)(i/renderDimension)) % renderDimension)));

				GL11.glScalef(.15f, .15f, .15f);

				Graphics.drawCube();

			}

		GL11.glPopMatrix();
	}
}