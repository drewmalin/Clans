package com.cube.states;

import javax.vecmath.Vector2d;

import com.cube.core.Clan;
import com.cube.core.Entity;
import com.cube.core.Physics;

public class TravelState extends State {

	Vector2d tempVect;

	@Override
	public void enter(Entity e) {
		
		System.out.println("Entity " + e + " is now traveling!");
		Physics.haltEntity(e);

		e.pause = 0;
	}

	@Override
	public void execute(Entity e) {
		if (Physics.distSquared(e.destination, e.position) < 5) {
			Physics.haltEntity(e);
			
			if (e.type == Clan.HUNTER && e.inventoryEmpty()) {
				System.out.println("Arrived, going to start gathering...");
				e.changeState( GatherState.getState() );
			}
			else if (e.type == Clan.HUNTER && !e.inventoryEmpty()) {
				
				if (e.pause < 250) {
					e.pause++;
				}
				else {
					System.out.println("Arrived, going to start depositing...");

					e.pause = 0;
					
					e.inventory = 0;
					if (e.focusEntity == null || e.focusEntity.inventoryEmpty()) {
						e.changeState( HuntState.getState() );
					}
					else {
						e.destination[0] = e.focusEntity.position[0];
						e.destination[1] = e.focusEntity.position[1];
						e.destination[2] = e.focusEntity.position[2];
					}
				}
			}
		}
		else {
			tempVect = Physics.updateVelocity(e);
			e.force.set(tempVect.x * .1, tempVect.y * .1);
		}
	}

	@Override
	public void exit(Entity e) {

		System.out.println("Entity " + e + " is no longer traveling.");

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