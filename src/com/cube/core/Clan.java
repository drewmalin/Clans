package com.cube.core;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import com.cube.states.NeutralState;

public class Clan {
	
	public static ArrayList<Unit> units;
	public static ArrayList<Building> buildings;
	
	public int id;
	public int farmerCount;
	public int builderCount;
	public int warriorCount;
	public int hunterCount;

	public float[] color;
	public Vector3d position;
	public Inventory clanStockpile;
	
	public static int FARMER 	= 100;
	public static int BUILDER 	= 101;
	public static int WARRIOR 	= 102;
	public static int HUNTER 	= 103;
	
	public static int influenceRadius = 100;
	
	public Clan() {
		units = new ArrayList<Unit>();
		buildings = new ArrayList<Building>();
		
		position = new Vector3d(0, 0, 0);
		farmerCount 	= 0;
		builderCount	= 0;
		warriorCount 	= 0;
		hunterCount 	= 0;

		clanStockpile = new Inventory();
		clanStockpile.maxWeight = Integer.MAX_VALUE;
	}
	
	public void draw() {
		for (Unit u : units) {
			if (u.show) {
				u.draw();
			}
		}
		
		for (Building b : buildings) {
			b.draw();
		}

		drawStockpile(clanStockpile.items.get("MEAT") == null ? 0 : clanStockpile.items.get("MEAT"));
	}
	
	public void update(int timeElapsed) {
		for (Unit u : units) {
			if (u.show) {
				u.update(timeElapsed);
			}
		}
	}
	
	public void process() {
		
		for (int i = 0; i < farmerCount; i++) {
			createUnit(FARMER, 4);
		}
		for (int i = 0; i < builderCount; i++) {
			createUnit(BUILDER, 4);
		}
		for (int i = 0; i < warriorCount; i++) {
			createUnit(WARRIOR, 4);
		}
		for (int i = 0; i < hunterCount; i++) {
			createUnit(HUNTER, 16);
		}
	}
	
	public void createUnit(int type, int inv) {
		Unit u;
		u = new Unit(type, 12, this, Resources.textures.get(2));
		u.inventory.setCap(inv);
		u.currentState = NeutralState.getState();
		u.inventory.maxWeight = 10;
		u.startState();
		
		if (type == HUNTER) {
			u.targets.add(Entity.AGGRESSIVE);
			u.targets.add(Entity.PASSIVE);
			u.targets.add(Entity.EDIBLE);
		}
		else if (type == BUILDER) {
			u.targets.add(Entity.GATHERABLE);
		}
		
		units.add(u);
	}
	
	public void createBuilding(String name) {
		buildings.add(Resources.buildingLibrary.get(name).copy());
	}
	
	public void drawStockpile(int count) {
		
		float spacing = .5f;
		int renderDimension = (int) Math.ceil(Math.pow(count, 1.0/3.0));
		
		
		GL11.glPushMatrix();
		
			for (int i = 0; i < count; i++) {
				
				GL11.glLoadIdentity();
				GL11.glColor3f(.4f, 1.0f, 1.0f);
								
				GL11.glTranslated(position.x + (spacing * (i % renderDimension)), 
						position.y  + Resources.map.getHeight((float)position.x, (float)position.z) + (spacing * (int) (i / (renderDimension * renderDimension))),
						position.z  + (spacing * (((int)(i/renderDimension)) % renderDimension)));
				
				GL11.glRotatef(0, 1, 0, 0);
				GL11.glRotatef(0, 0, 1, 0);
				GL11.glRotatef(0, 0, 0, 1);
				GL11.glScalef(.25f, .25f, .25f);
				
				Graphics.drawCube();
			}

		GL11.glPopMatrix();
	}

	public void payBuildingReqs(Building buildingToBeBuilt) {
		for (Item i : buildingToBeBuilt.itemPrereqs.keySet()) {
			clanStockpile.removeItems(i, buildingToBeBuilt.itemPrereqs.get(i));
		}
	}
}
