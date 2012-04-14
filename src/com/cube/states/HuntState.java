package com.cube.states;

import javax.vecmath.Vector2d;

import com.cube.core.Entity;
import com.cube.core.Physics;
import com.cube.core.Resources;

public class HuntState extends State {

	Vector2d tempVect;
	
	/*
	 * Entering the Hunt state means entering the 'search' or probably more accurately, 'wander,' movement
	 * mechanic. The hunting entity is given a randomly generated destination to begin walking towards, and
	 * is pushed in that direction.
	 */
	@Override
	public void enter(Entity e) {
		
		System.out.println("Entity " + e + " is now hunting!");
		
		Physics.updateDestination(e, 25);
		e.force.set(e.destination[0] * .01, e.destination[2] * .01);
	}

	/*
	 * The execution for the hunter primarily means continuing his search through the world for food. If the 
	 * hunter gets close enough to his destination, it should reset to continue his search (this is in order
	 * to avoid the effect of circling a position tighter and tighter until the hunter steps on the destination
	 * point. Getting within a close enough position means his search looks much more realistic).
	 */
	@Override
	public void execute(Entity e) {

		// We are close enough to the destination to determine a new one
		for (Entity x : Resources.entities) {
			if (x.type == Entity.HUNTABLE) {
				if (Physics.distSquared(x.position, e.position) < 10) {
					e.destination[0] = x.position[0];
					e.destination[1] = x.position[1];
					e.destination[2] = x.position[2];
					System.out.println("Entity " + e + " is going to start hunting...");
					e.changeState( GatherState.getState() );
					return;
				}
			}
		}
		
		if (Physics.distSquared(e.position, e.destination) < 10) {
			System.out.println("setting a new target");
			Physics.updateDestination(e, 25);
			e.force.set(e.destination[0] * .01, e.destination[2] * .01);
		}
		
		tempVect = Physics.updateVelocity(e);
		e.force.set(tempVect.x * .1, tempVect.y * .1);

	}

	/*
	 * Exiting the hunt means cleaning up any junk we set up in order to start the hunt! 
	 */
	@Override
	public void exit(Entity e) {
		
		System.out.println("Entity " + e + " is no longer hunting!");

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
			ref = new HuntState();
		}
		return ref;
	}
}