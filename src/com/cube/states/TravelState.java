package com.cube.states;

import org.lwjgl.util.vector.Vector2f;

import com.cube.core.Building;
import com.cube.core.Clan;
import com.cube.core.Entity;
import com.cube.core.Physics;
import com.cube.core.Role;
import com.cube.core.Status;
import com.cube.core.Type;
import com.cube.core.Unit;

public class TravelState extends State {

	Vector2f tempVect;

	@Override
	public void enter(Entity e) {
		if (debugMessages) System.out.println("Entity " + e + " is now traveling!");
		Physics.haltEntity(e);
	}

	@Override
	public void execute(Entity e) {
		
		if (Physics.distSquared(e.destination, e.position) < 5) {			//If you've reached your destination, stop
			Physics.haltEntity(e);

			if (e.focusEntity == null && e.userControlled == true) {		//If you were directed to this position by the user, wait
				e.userControlled = false;
				e.waitMillis = 5000;
				e.changeState( WaitState.getState() );
			}
			else if (e.getClass().equals(Unit.class) && 
					 e.destination.equals(e.clanRef.position)) {			// Entity is a unit and focus is its clan: deposit
																			
				e.changeState( DepositState.getState() );
			}
			else if (e.focusEntity.status.equals(Status.dead) ||			// Focus is edible/gatherable/dead: immediately gather
					 e.focusEntity.types.contains(Type.edible) ||
					 e.focusEntity.types.contains(Type.gatherable)){ 
				e.changeState( GatherState.getState() );

			}
			else if (e.focusEntity.types.contains(Type.aggressive) ||
					 e.focusEntity.types.contains(Type.passive)) {		// Focus is aggressive/passive: attack
				e.changeState( AttackState.getState() );
			}
			else if (e.focusEntity.getClass().equals(Building.class)) {		// Focus is building: interact
				// Building is incomplete and entity is a builder. Build.
				if (e.getRoles().contains(Role.builder) && !((Building) (e.focusEntity)).complete) {
					e.changeState( BuildState.getState() );
				}
				//Entity is merely interacting with a building. Interact.
				else {
					//e.changeState( SomeBuildingSpecificState.getState() );      
				}
			}
		}
		//If you haven't reached your destination yet, keep going
		else {
			tempVect = Physics.seekDestination(e);
			e.force.set(tempVect.x * Physics.SPEED.MEDIUM.value(),
					    tempVect.y * Physics.SPEED.MEDIUM.value());
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