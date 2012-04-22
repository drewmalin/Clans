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
	}

	@Override
	public void execute(Entity e) {
		if (Physics.distSquared(e.destination, e.position) < 5) {
			Physics.haltEntity(e);
			
			if (e.type == Clan.HUNTER && e.inventory.isEmpty()) { //Assume you've arrived at a resource (gather)
				System.out.println("Arrived, going to start gathering...");
				e.changeState( GatherState.getState() );
			}
			else if (e.type == Clan.HUNTER && !e.inventory.isEmpty()) { //Assume you've arrived at home (deposit)

				if (e.pause(50)) {
					System.out.println("Depositing mah resources... ");
					System.out.println("Inventory count: " + e.inventory.count());

					e.inventory.removeItem();
					e.clanRef.meatCount++;
					
					if (e.inventory.isEmpty()) { //Done depositing
						if (e.focusEntity == null || e.focusEntity.inventory.isEmpty()) { //Last target is empty
							e.changeState( HuntState.getState() );
						}
						else { //Last target still has resources
							e.setDestination(e.focusEntity.position);
						}
					}
				}
			}
		}
		else {
			tempVect = Physics.updateVelocity(e);
			e.force.set(tempVect.x * Physics.FAST, tempVect.y * Physics.FAST);
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