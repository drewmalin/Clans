package com.cube.states;

import com.cube.core.Entity;
import com.cube.core.Physics;

public class WaitState extends State {
	
	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now waiting!");
		e.waitDelta = System.currentTimeMillis();
		Physics.haltEntity(e);
	}

	@Override
	public void execute(Entity e) {
		if ((System.currentTimeMillis() - e.waitDelta) > e.waitMillis) {
			if (e.previousState != null)
				e.changeState(e.previousState);
			else
				e.changeState(NeutralState.getState());
		}
	}

	@Override
	public void exit(Entity e) {
		e.waitDelta = e.waitMillis = 0;
		if (debugMessages) System.out.println("Entity " + e + " is no longer waiting.");
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
			ref = new WaitState();
		}
		return ref;
	}
}