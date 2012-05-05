package com.cube.states;

import javax.vecmath.Vector2d;

import com.cube.core.Entity;
import com.cube.core.Physics;

public class AttackState extends State {

	Vector2d tempVect;

	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now attacking!");
		e.focusEntity.underAttack = true;
	}
	
	@Override
	public void execute(Entity e) {
		
		if (Physics.proximityCollision(e, e.focusEntity)) {			//If you've reached your destination, attack
			
			if (e.pause(10)) {
				e.focusEntity.curHealth--;
				if (debugMessages) System.out.println("Entity " + e.focusEntity.toString() + " is now at " + e.focusEntity.curHealth + " health!");
				if (e.focusEntity.curHealth <= 0) {
					e.focusEntity.underAttack = false;
					e.focusEntity.changeState( DeadState.getState() );
					
					e.changeState(GatherState.getState());
					return;
				}
			}
			e.setDestination(e.focusEntity.position);
			tempVect = Physics.arrive(e);
			//tempVect = Physics.seekDestination(e);
			e.force.set(tempVect.x * Physics.SPEED.FAST.value(), 
					    tempVect.y * Physics.SPEED.FAST.value());
		}
		else { 		//run towards the target
			e.setDestination(e.focusEntity.position);
			tempVect = Physics.seekDestination(e);
			e.force.set(tempVect.x * Physics.SPEED.FAST.value(), 
					    tempVect.y * Physics.SPEED.FAST.value());
		}
	}
	
	@Override
	public void exit(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is no longer attacking!");
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
