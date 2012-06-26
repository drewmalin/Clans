package com.cube.states;

import javax.vecmath.Vector2d;

import com.cube.core.Building;
import com.cube.core.Entity;
import com.cube.core.Game;
import com.cube.core.Physics;
import com.cube.core.Resources;

public class BuildState extends State {
	
	Vector2d tempVect;
	Building building;
	int x, z;
	
	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now building!");
		
		building = ((Building)(e.focusEntity));
		e.setDestination(new float[]{(int)building.position.x, 0, (int)building.position.z});
		
		x = (int)(building.position.x + (Resources.map.width/2));
		z = (int)(building.position.z + (Resources.map.height/2));
	}

	@Override
	public void execute(Entity e) {
		/* Depending on the size/resource requirements of the building, the unit should walk
		 * to each individual tile that makes up the building's foundation and smooth it before 
		 * the building is to be considered "complete." Each visit to a tile increases the 
		 * building's current production state. A building with 10 tiles will require 10 smoothing
		 * visits and thus, each visit will increase the level of production by 10%.
		 * 
		 * The following proceeds similarly to the TravelState execution. Each "Destination" is each
		 * tile in the foundation. The execute method will loop through all tiles, finding ones that
		 * need lowering or raising. 
		 */ 
		boolean unfinished = false;
		
		if (Physics.distSquared(e.destination, e.position) < 2) {			//If you've reached your destination, stop
			Physics.haltEntity(e); 
			
			// Fix the elevation of the current tile
			Resources.map.pixels[(int)e.destination.x][(int)e.destination.z][0] = (int) building.averageHeight;
			
			// Search for an unfinished tile
			for (int j = z - (building.height/2); j < z + (building.height/2); j++) {
				for (int i = x - (building.width/2); i < x + (building.width/2); i++) {
					//If an unfinished tile is found, target it
					if (Resources.map.pixels[i][j][0] != building.averageHeight) {
						e.setDestination(new float[]{(int)(i - (Resources.map.width/2)), 0, (int)(j - (Resources.map.height/2))});
						Resources.map.vertexNormals[i][j].x = 0;
						Resources.map.vertexNormals[i][j].y = 1;
						Resources.map.vertexNormals[i][j].z = 0;
						unfinished = true;
						break;
					}
				}
			}
			//If no unfinished tile could be found, this building is complete
			if (!unfinished) {
				building.complete = true;
				building.paused = false;
				e.previousState = null;
				e.changeState( NeutralState.getState() );
			}
			//An unfinished tile was found, wait for 2 seconds, then fix it.
			else {
				e.previousState = this;
				e.waitMillis = 2000;
				e.changeState( WaitState.getState() );
			}
		}
		else {
			tempVect = Physics.seekDestination(e);
			e.force.set(tempVect.x * Physics.SPEED.MEDIUM.value(),
					    tempVect.y * Physics.SPEED.MEDIUM.value());
		}
	}

	@Override
	public void exit(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is no longer building.");
		if (!building.complete) building.paused = true;
	}

	//-------------------------------------------------------------------------//
	//------------------------ Singleton Necessities --------------------------//
	//-------------------------------------------------------------------------//
	
	/*
	 * The following is required for every singleton. Characteristics of java and object orientation
	 * means that this needs to be copied (and slightly modified) for each new state :(. This will 
	 * probably be modified in the future to be a bit cleaner.
	 */
	
	// Self reference
	protected static State ref;
	
	// Return the instance of this singleton state
	public static synchronized State getState() {
		if (ref == null) {
			ref = new BuildState();
		}
		return ref;
	}
}