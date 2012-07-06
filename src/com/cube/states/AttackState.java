package com.cube.states;

import org.lwjgl.util.vector.Vector2f;

import com.cube.core.Entity;
import com.cube.core.Physics;
import com.cube.gui.Menu;

public class AttackState extends State {

	Vector2f tempVect;

	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now attacking!");
		e.focusEntity.underAttack = true;
	}
	
	@Override
	public void execute(Entity e) {
		
		if (Physics.proximityCollision(e, e.focusEntity)) {			//If you've reached your destination, attack
			
			e.focusEntity.curHealth--;
			if (debugMessages) System.out.println("Entity " + e.focusEntity.toString() + " is now at " + e.focusEntity.curHealth + " health!");
			// If the focus entity died, start gathering
			if (e.focusEntity.curHealth <= 0) {				
				e.focusEntity.changeState( DeadState.getState() );
				e.previousState = null;
				e.changeState(GatherState.getState());
				return;
			}
			// If the focus entity is still alive, wait for 1 second (so the entity doesn't insta-kill its
			// target) and then continue;
			else {
				e.previousState = this;
				e.waitMillis = 1000;
				e.changeState( WaitState.getState() );
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
