package com.cube.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

public class Inventory {
	
	private int capacity;
	private int itemCount;
	private int renderDimension;
	//public ArrayList<Item> items;
	public HashMap<Item, Integer> items;
	
	private double startX;
	private double startY;
	private double startZ;
	
	public float maxWeight;
	public float currentWeight;
	
	public Inventory() {
		items = new HashMap<Item, Integer>();
		
		capacity = renderDimension = 0;
		startX = startY = startZ = 0;
	}

	public String printInventory() {
		String ret = "";
		
		for (Item key : items.keySet())
			if (items.get(key) > 0)
				ret += key.name + " " + items.get(key) + "\n";
		
		return ret;
	}
	public boolean isFull() {
		return (currentWeight >= maxWeight);
	}

	public boolean isEmpty() {
		for (Item key : items.keySet()) {
			if (items.get(key) > 0) {
				return false;
			}
		}
		return true;
	}
	
	public void addItem(Item i) {
		if (!isFull()) {
			if (items.containsKey(i)) {
				items.put(i, items.get(i) + 1);
			}
			else {
				items.put(i, 1);
			}
			currentWeight += i.weight;
		}
	}

	public void addItems(Item i, int count) {
		if ((i.weight * count) + currentWeight <= maxWeight) {
			items.put(i, count);
			currentWeight += (i.weight * count);
		}
	}
	
	public void removeItem(Item i) {
		if (items.get(i) != null && items.get(i) > 0) {
			items.put(i, items.get(i) - 1);
			currentWeight -= i.weight;
		}
	}
	
	/*
	 * Removes the first item it finds from the inventory. If no items are found,
	 * returns null.
	 */
	public Item removeItem() {
		Item ret = null;
		
		for (Item key : items.keySet()) {
			if (items.get(key) > 0) {
				ret = key;
				items.put(key, items.get(key) - 1);
				currentWeight -= key.weight;
				return ret;
			}
		}
		return null;
	}
	
	public void removeItems(Item i, int count) {
		int newCount = items.get(i) - count;
		if (newCount < 0) newCount = 0;
		
		items.put(i, newCount);
		
	}
	/*
	 * Returns true if there are items of the given item type in this inventory,
	 * false otherwise.
	 */
	public boolean contains(String key) {
		
		Item i = Resources.itemLibrary.get(key);
		
		if (items.get(i) != null && items.get(i) > 0)
			return true;
		else
			return false;
	}
	
	public void setCap(int c) {
		capacity = c;
		renderDimension = (int) Math.ceil(Math.pow(capacity, 1.0/3.0));
	}
	
	public void empty() {
		itemCount = 0;
		currentWeight = 0;
		items.clear();
	}
	
	public int count() {
		return items.size();
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