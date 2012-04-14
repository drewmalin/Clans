package com.cube.states;

import com.cube.core.Entity;

public class GatherState extends State {
	
	@Override
	public void enter(Entity e) {
		
		System.out.println("Entity " + e + " is now gathering!");
		
	}

	@Override
	public void execute(Entity e) {
		
	}

	@Override
	public void exit(Entity e) {

		System.out.println("Entity " + e + " is no longer gathering.");

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
			ref = new GatherState();
		}
		return ref;
	}
}