package com.cube.core;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.cube.states.NeutralState;

public class Clan {
	
	public static ArrayList<Unit> units;
	public int id;
	public int farmerCount;
	public int builderCount;
	public int warriorCount;
	public int hunterCount;
	public int berryCount;
	public int meatCount;
	public float[] color;
	public float[] position;

	public static int FARMER = 10;
	public static int BUILDER = 11;
	public static int WARRIOR = 12;
	public static int HUNTER = 13;
	
	public Clan() {
		units = new ArrayList<Unit>();
		position = new float[3];
		farmerCount = 0;
		builderCount = 0;
		warriorCount = 0;
		hunterCount = 0;
		berryCount = 0;
		meatCount = 0;
	}
	
	public void draw() {
		for (Unit u : units) {
			if (u.show) {
				u.draw();
			}
		}

		drawMeatStockpile();
	}
	
	public void update(int timeElapsed) {
		for (Unit u : units) {
			if (u.show) {
				u.update(timeElapsed);
			}
		}
	}
	
	public void process() {
		Unit u;
		
		for (int i = 0; i < farmerCount; i++) {
			u = new Unit(FARMER, 1, this, Resources.textures.get(0));
			u.inventory.setCap(4);
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
		for (int i = 0; i < builderCount; i++) {
			u = new Unit(BUILDER, 1, this, Resources.textures.get(0));
			u.inventory.setCap(4);
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
		for (int i = 0; i < warriorCount; i++) {
			u = new Unit(WARRIOR, 1, this, Resources.textures.get(0));
			u.inventory.setCap(4);
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
		for (int i = 0; i < hunterCount; i++) {
			u = new Unit(HUNTER, 1, this, Resources.textures.get(0));
			u.inventory.setCap(16);
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
	}
	
	public void drawMeatStockpile() {
		
		float spacing = .5f;
		int renderDimension = (int) Math.ceil(Math.pow(meatCount, 1.0/3.0));
		
		
		GL11.glPushMatrix();
		
			for (int i = 0; i < meatCount; i++) {
				
				GL11.glLoadIdentity();
				GL11.glColor3f(.4f, 1.0f, 1.0f);

				GL11.glTranslatef(position[0] + (spacing * (i % renderDimension)), 
						position[1]  + (spacing * (int) (i / (renderDimension * renderDimension))),
						position[2]  + (spacing * (((int)(i/renderDimension)) % renderDimension)));
				GL11.glRotatef(0, 1, 0, 0);
				GL11.glRotatef(0, 0, 1, 0);
				GL11.glRotatef(0, 0, 0, 1);
				GL11.glScalef(.25f, .25f, .25f);
				
				Graphics.drawCube();
			}

		GL11.glPopMatrix();
	}
}
