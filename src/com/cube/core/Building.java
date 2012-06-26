package com.cube.core;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import com.cube.util.Utilities;

public class Building extends Entity {
	
	public String name;
	public int ID;
	public boolean complete;
	public boolean paused;
	public int objectID;
	public float averageHeight;
	
	public HashMap<Item, Integer> itemPrereqs;
	public ArrayList<Building> buildingPrereqs;
	
	public int width;
	public int height;
	
	public Building() {
		name = "";
		ID = -1;
		complete = false;
		paused = false;
		
		width = 0;
		height = 0;
		averageHeight = 0;
		
		itemPrereqs = new HashMap<Item, Integer>();
		buildingPrereqs = new ArrayList<Building>();
	}
	
	@SuppressWarnings("unchecked")
	public Building copy() {
		Building b = new Building();
		
		b.name 		= this.name;
		b.ID 		= this.ID;
		b.width 	= this.width;
		b.height	= this.height;
		b.complete 	= this.complete;
		
		b.position.x  = this.position.x;
		b.position.y  = this.position.y;
		b.position.z  = this.position.z;

		b.rotation  = this.rotation.clone();
		
		b.itemPrereqs 	  = (HashMap<Item, Integer>) this.itemPrereqs.clone();
		b.buildingPrereqs = (ArrayList<Building>) this.buildingPrereqs.clone();
		
		b.averageHeight = this.averageHeight;
		
		return b;
	}
	
	@Override
	public void draw() {
		if (complete) {
			super.draw();
		}
		else {
			drawOutline();
		}
	}
	
	/*
	 * Method to draw an "outline" of the building when it is in an unfinished state. Currently this
	 * is purely to make it easier to figure out where the foundation is on the map. In the future we
	 * may want to look at the current progress of the building and print appropriate models for each
	 * stage.
	 */
	public void drawOutline() {
		double xMin = position.x - (width/2);
		double xMax = position.x + (width/2);
		double zMin = position.z - (height/2);
		double zMax = position.z + (height/2);
		
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor3f(1f, 0, 0);

		// Four lines of height 10 at the four corners of the foundation
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(xMin, 	Resources.map.getHeight((float)xMin, (float)zMin), 		zMin);
			GL11.glVertex3d(xMin, 	Resources.map.getHeight((float)xMin, (float)zMin) + 10, zMin);
			
			GL11.glVertex3d(xMax, 	Resources.map.getHeight((float)xMax, (float)zMin), 		zMin);
			GL11.glVertex3d(xMax, 	Resources.map.getHeight((float)xMax, (float)zMin) + 10, zMin);
			
			GL11.glVertex3d(xMax, 	Resources.map.getHeight((float)xMax, (float)zMax), 		zMax);
			GL11.glVertex3d(xMax, 	Resources.map.getHeight((float)xMax, (float)zMax) + 10, zMax);
			
			GL11.glVertex3d(xMin, 	Resources.map.getHeight((float)xMin, (float)zMax), 		zMax);
			GL11.glVertex3d(xMin, 	Resources.map.getHeight((float)xMin, (float)zMax) + 10, zMax);
		GL11.glEnd();

		
		GL11.glPopMatrix();
	}
	
	public String info() {
		String ret = "";
		ret += name + "\n\n";
		
		if (itemPrereqs.size() > 0) {
			ret += "Required Items:\n";
			for (Item i : itemPrereqs.keySet()) {
				ret += itemPrereqs.get(i) + " " + i.name + "\n";
			}
		}
		
		if (buildingPrereqs.size() > 0) {
			ret += "Required Buildings:\n";
			for (Building b : buildingPrereqs) {
				ret += b.name + "\n";
			}
		}
		
		return ret;
	}

	/*
	 * Static method to check if the given clan meets the necessary requirements for the creation of the
	 * given building. 
	 */
	public static boolean meetsRequirements(Clan playerClan, Building building) {
		
		if (building.buildingPrereqs.size() > 0) {
			if (!playerClan.buildings.containsAll(building.buildingPrereqs))
				return false;
		}
		if (building.itemPrereqs.size() > 0) {
			for (Item i : building.itemPrereqs.keySet()) {
				if (playerClan.clanStockpile.items.get(i) == null || 
					playerClan.clanStockpile.items.get(i) < building.itemPrereqs.get(i))
					return false;
			}
		}
		return true;
	}

	public static void layFoundation(float[] pos, Building buildingToBeBuilt) {
		
		//---Test to see if this is a valid location for the foundation---//
		
		for (Entity e : Resources.entities) {
			
			if (Physics.distSquared(new Vector3d(pos[0], pos[1], pos[2]), e.position) < 1) {
				
			}
		}
		
		//----------------------------------------------------------------//
		
		Game.buildingToBeBuilt.position.x = pos[0];
		Game.buildingToBeBuilt.position.y = pos[1];
		Game.buildingToBeBuilt.position.z = pos[2];
		Game.buildingToBeBuilt.averageHeight = Physics.calculateAverageHeight(Game.buildingToBeBuilt);
		Game.playerClan.buildings.add(Game.buildingToBeBuilt.copy());
		Game.playerClan.payBuildingReqs(Game.buildingToBeBuilt);
		Game.buildingToBeBuilt = null;
		
		//search for the closest builder, if none exists, set status to 'paused'
	}
}