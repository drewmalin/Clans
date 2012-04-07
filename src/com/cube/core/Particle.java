package com.cube.core;

public class Particle {
	
	public float position[];
	public float movement[];
	public float maximum[];
	public float scale[];
	public float color[];
	/*
	public float xPos;
	public float yPos;
	public float zPos;
	
	public float xMov;
	public float yMov;
	public float zMov;
	
	public float xMax;
	public float yMax;
	public float zMax;
	
	public float R;
	public float G;
	public float B;
	
	public float xScale;
	public float yScale;
	public float zScale;
	*/
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
