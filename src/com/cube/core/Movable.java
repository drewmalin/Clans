package com.cube.core;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Movable {
	//---------- Movement/Rendering attributes ---------//
	public Vector3f position;
	public Vector3f destination;
	public Vector3f direction;
	public Vector2f force;
	public Vector2f acceleration;
	public Vector2f velocity;
	public float mass;
	public float max_v;
	public float[] color;
	public float[] colorID;
	public float[] rotation;
	public boolean show;
	private int pause;
	public int timedump;

	public Movable() {
		
		color 		= new float[3];
		rotation 	= new float[3];
		colorID		= new float[3];
		destination = new Vector3f(0, 0, 0);
		direction	= new Vector3f(0, 0, 0);
		position 	= new Vector3f(0, 0, 0);

		force 			= new Vector2f(0, 0);
		acceleration 	= new Vector2f(0, 0);
		velocity 		= new Vector2f(0, 0);
		mass			= 100;
		max_v			= .1f;
		
		color[0] 	= color[1] 		= color[2] 		= 1f;
		rotation[0] = rotation[1]	 = rotation[2] 	= 0f;
		
		show 		= true;
		pause 		= 0;
		timedump = 0;

	}
	
	
	public void setDestination(float[] target) {
		destination.x = target[0];
		destination.y = target[1];
		destination.z = target[2];
	}

	public void setDestination(Vector3f pos) {
		destination.x = pos.x;
		destination.y = pos.y;
		destination.z = pos.z;
	}
	
	public boolean pause(int time) {
		if (pause < time) {
			pause++;
			return false;
		}
		else {
			pause = 0;
			return true;
		}
	}

}
