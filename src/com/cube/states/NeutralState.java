package com.cube.states;

import com.cube.core.Clan;
import com.cube.core.Entity;

public class NeutralState extends State {

	int counter;
	
	@Override
	public void enter(Entity e) {
		
		counter = 0;
		System.out.println("Entity " + e + " is now neutral!");
		
	}

	@Override
	public void execute(Entity e) {
		
		counter++;
		if (counter >= 100 && e.type == Clan.HUNTER) {
			System.out.println("Entity " + e + " is going to start hunting...");
			e.changeState( HuntState.getState() );
		}
	}

	@Override
	public void exit(Entity e) {

		System.out.println("Entity " + e + " is no longer neutral.");

	}

	//----- Singleton attributes and methods ----//

	// Self reference
	protected static State ref;
	
	// Return the instance of this singleton state
	public static synchronized State getState() {
		if (ref == null) {
			ref = new NeutralState();
		}
		return ref;
	}
}