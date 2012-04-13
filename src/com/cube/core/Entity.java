package com.cube.core;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import com.cube.states.State;

public class Entity {
	public int objectID;
	public float[] position;
	public float[] destination;
	public float[] color;
	public float[] rotation;
	public float scale;
	public boolean show;
	public int type;

	public State currentState;

	public Vector2d force;
	public Vector2d acceleration;
	public Vector2d velocity;
	public double mass;
	public double max_v;
	
	public Entity() {
		position 	= new float[3];
		color 		= new float[3];
		rotation 	= new float[3];
		destination = new float[3];
		
		force 			= new Vector2d(0, 0);
		acceleration 	= new Vector2d(0, 0);
		velocity 		= new Vector2d(0, 0);
		mass			= 100;
		max_v			= .1;
		
		position[0] = position[1] 	= position[2] 	= 0f;
		color[0] 	= color[1] 		= color[2] 		= 1f;
		rotation[0] = rotation[1]	 = rotation[2] 	= 0f;
		destination[0] = destination[1] = destination[2] = 0f;
		
		scale 		= .1f;
		show 		= true;
		objectID 	= -1;
		
	}
	
	public void draw() {
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glColor3f(color[0], color[1], color[2]);
			GL11.glTranslatef(position[0], position[1], position[2]);
			GL11.glRotatef(rotation[0], 1, 0, 0);
			GL11.glRotatef(rotation[1], 0, 1, 0);
			GL11.glRotatef(rotation[2], 0, 0, 1);
			GL11.glScalef(scale, scale, scale);
			Resources.objectLibrary[objectID].draw();
			
			GL11.glColor3f(1.0f, 0f, 0f);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2d(2 * velocity.x + position[0], 2 * velocity.y + position[2]);
			GL11.glVertex2d(position[0], position[2]);
			GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void startState() {
		if (currentState != null) {
			currentState.enter(this);
		}
	}
	
	public void changeState(State newState) {
		if (currentState != null && newState != null) {
			currentState.exit(this);
			currentState = newState;
			currentState.enter(this);
		}
	}
	
	public void update(int timeElapsed) {
		
		Physics.updatePosition(this);

		if (velocity.length() != 0) {
			rotation[1] = (float) ((180/Math.PI) * Math.acos((-1 * velocity.y)/velocity.length()));
			
			if (velocity.x > 0)
				rotation[1] *= -1;
		}

		if (currentState != null) {
			currentState.execute(this);
		}
	}
}
