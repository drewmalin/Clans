package com.cube.core;


public class Unit extends Entity {
		
	public float[] direction;
	
	public Unit(int _id) {
		id = _id;
		show = true;
		direction = new float[3];
		direction[0] = 0;
		direction[1] = 0;
		direction[2] = -1;
	}
	
	public void update() {
		super.update();
		/*TODO
		 * Update the y rotation of the unit based on the direction vector
		 */
	}
}
