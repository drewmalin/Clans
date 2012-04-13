package com.cube.states;

import com.cube.core.Entity;

public class AttackState extends State {
	
	int counter;
	
	@Override
	public void enter(Entity e) {

		counter = 0;
		System.out.println("Entity " + e + " is now attacking!");
		
	}
	
	@Override
	public void execute(Entity e) {

		counter++;
		if (counter >= 100) {
			e.changeState( NeutralState.getState() );
		}
	}
	
	@Override
	public void exit(Entity e) {

		System.out.println("Entity " + e + " is no longer attacking!");
		
	}
	
	//----- Singleton attributes and methods ----//
	
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
