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
		
		float theta = (float) Math.acos(direction[0]);
		System.out.println(theta);
	}
	
	public void update() {
		//arccos direction[0]
		float theta = (float) Math.acos(direction[0]);
		System.out.println(theta);
	}
}
