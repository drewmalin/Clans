package com.cube.states;

import javax.vecmath.Vector2d;

import com.cube.core.Clan;
import com.cube.core.Entity;

public class GatherState extends State {

	Vector2d tempVect;

	/*
	 * At the beginning of the gather, the unit stops his current movement and starts walking
	 * towards the target. 
	 */
	@Override
	public void enter(Entity e) {
		
		System.out.println("Entity " + e + " is now gathering!");
		
	}

	/* 
	 * If the unit gets close enough to his destination to begin the gather, he should stop moving and start
	 * gathering! 
	 */
	@Override
	public void execute(Entity e) {
		
		if (e.focusEntity.inventoryEmpty()) {
			e.type = Entity.NEUTRAL;
			e.focusEntity.rotation[0] += 180;
			e.focusEntity = null;
			if (e.type == Clan.HUNTER)
				e.changeState( HuntState.getState() );
		}
		else {
			e.inventory++;
			e.focusEntity.inventory--;
			e.destination[0] = 0;
			e.destination[1] = 0;
			e.destination[2] = 0;
			e.changeState( TravelState.getState() );
		}

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