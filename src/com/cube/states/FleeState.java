package com.cube.states;

import javax.vecmath.Vector2d;

import com.cube.core.Entity;
import com.cube.core.Physics;

public class FleeState extends State {
	
	Vector2d tempVect;
	
	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now fleeing!");		
	}

	@Override
	public void execute(Entity e) {
		tempVect = Physics.fleeFocusEntity(e);
		e.force.set(tempVect.x * Physics.FAST, tempVect.y * Physics.FAST);
	}

	@Override
	public void exit(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is no longer fleeing.");
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
			ref = new FleeState();
		}
		return ref;
	}
}