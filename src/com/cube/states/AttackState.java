package com.cube.states;

import com.cube.core.Entity;

public class AttackState extends State {
	
	int counter;
	
	@Override
	public void enter(Entity e) {

		counter = 0;
		System.out.println("Entity " + e + " is now attacking!");
		
	}
	
	@Override
	public void execute(Entity e) {

		counter++;
		if (counter >= 100) {
			e.changeState( NeutralState.getState() );
		}
	}
	
	@Override
	public void exit(Entity e) {

		System.out.println("Entity " + e + " is no longer attacking!");
		
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
			ref = new AttackState();
		}
		return ref;
	}
}
