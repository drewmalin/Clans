package com.cube.states;

import java.util.ArrayList;

import com.cube.core.Clan;
import com.cube.core.Entity;
import com.cube.util.Utilities;

public class NeutralState extends State {
	
	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now neutral!");		
	}

	@Override
	public void execute(Entity e) {
		
		ArrayList<Integer> test = new ArrayList<Integer>();
		
		test.add(Clan.HUNTER);
		test.add(Clan.BUILDER);
		test.add(Entity.AGGRESSIVE);
		test.add(Entity.PASSIVE);
		
		if (e.focusEntity != null) {
			e.interactWithFocusEntity();
		}
		else if (Utilities.containsAny(e.types, test)) {
			e.changeState( SearchState.getState() );
		}
		
	}

	@Override
	public void exit(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is no longer neutral.");
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