package com.cube.states;

import javax.vecmath.Vector2d;

import com.cube.core.Entity;
import com.cube.core.Physics;

public class TravelState extends State {

	Vector2d tempVect;

	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now traveling!");
		Physics.haltEntity(e);
	}

	@Override
	public void execute(Entity e) {
		
		if (Physics.distSquared(e.destination, e.position) < 5) {			//If you've reached your destination, stop
			Physics.haltEntity(e);

			if (e.focusEntity.type == Entity.EDIBLE) {						// Focus is edible: immediately gather
				e.changeState( GatherState.getState() );
			}
			else if (e.focusEntity.type == Entity.AGGRESSIVE ||
					 e.focusEntity.type == Entity.PASSIVE) {				// Focus is aggressive/passive: attack
				e.changeState( AttackState.getState() );
			}
			else if (e.clanRef != null && 
					 e.destination.equals(e.clanRef.position)) {			// Focus is clan: deposit
				e.changeState( DepositState.getState() );
			}
		}
		//If you haven't reached your destination yet, keep going
		else {
			tempVect = Physics.seekDestination(e);
			e.force.set(tempVect.x * Physics.FAST, tempVect.y * Physics.FAST);
		}
	}

	@Override
	public void exit(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is no longer traveling.");
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
			ref = new TravelState();
		}
		return ref;
	}
}