package com.cube.states;

import com.cube.core.Entity;
import com.cube.core.Physics;

public class DeadState extends State {
	
	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now dead!");		
		Physics.haltEntity(e);
		e.underAttack = false;
		e.types.clear();
		e.types.add(Entity.NEUTRAL);
		e.types.add(Entity.DEAD);
		e.rotation[0] += 180;
		e.position.y += 2;
	}

	@Override
	public void execute(Entity e) {
		//Only the black void...
	}

	@Override
	public void exit(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is no longer dead (???).");
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
			ref = new DeadState();
		}
		return ref;
	}
}