package com.cube.core;

public class Particle {
	
	public float position[];
	public float movement[];
	public float maximum[];
	public float scale[];
	public float color[];
	
	public float direction;
	public float acceleration;
	public float deceleration;
	
	public Particle() {
		position = new float[3];
		movement = new float[3];
		maximum = new float[3];
		scale = new float[3];
		color = new float[3];
	}
}
