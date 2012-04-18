package com.cube.states;

import com.cube.core.Clan;
import com.cube.core.Entity;

public class NeutralState extends State {
	
	@Override
	public void enter(Entity e) {
		
		e.pause = 0;
		System.out.println("Entity " + e + " is now neutral!");
		
	}

	@Override
	public void execute(Entity e) {
		
		e.pause++;
		if (e.pause >= 100 && e.type == Clan.HUNTER) {
			System.out.println("Entity " + e + " is going to start hunting...");
			e.changeState( HuntState.getState() );
			e.pause = 0;
		}
	}

	@Override
	public void exit(Entity e) {

		System.out.println("Entity " + e + " is no longer neutral.");

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
			ref = new NeutralState();
		}
		return ref;
	}
}