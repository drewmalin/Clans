package com.cube.core;


public class Unit extends Entity {
		
	public float[] direction;
	public Clan clanRef;
	
	public Unit(int _type, int _id, Clan c) {
		type = _type;
		objectID = _id;
		clanRef = c;
		
		show = true;
		direction = new float[3];
		direction[0] = 0;
		direction[1] = 0;
		direction[2] = -1;
	}
	
	public void update(int timeElapsed) {
		super.update(timeElapsed);
		/*TODO
		 * Update the y rotation of the unit based on the direction vector
		 */
	}
}
