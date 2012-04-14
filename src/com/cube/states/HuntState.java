package com.cube.states;

import javax.vecmath.Vector2d;

import com.cube.core.Entity;
import com.cube.core.Physics;

public class HuntState extends State {

	int counter;
	Vector2d tempVect;

	@Override
	public void enter(Entity e) {
		
		counter = 0;
		System.out.println("Entity " + e + " is now hunting!");
		Physics.updateDestination(e, 25);
		e.force.set(e.destination[0] * .01, e.destination[2] * .01);
	}

	@Override
	public void execute(Entity e) {

		//------------Code for wander------------//

		if (Physics.distSquared(e.position, e.destination) < 10) {
			System.out.println("setting a new target");
			// We are close enough to the destination to determine a new one
			Physics.updateDestination(e, 25);
			e.force.set(e.destination[0] * .01, e.destination[2] * .01);
		}
		
		tempVect = Physics.updateVelocity(e);
		e.force.set(tempVect.x * .1, tempVect.y * .1);

//		System.out.println("Current position: " + Physics.printArray(e.position));
//		System.out.println("destination: " + Physics.printArray(e.destination));

	}

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