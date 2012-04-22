package com.cube.core;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import com.cube.states.State;
import com.cube.util.Texture;

public class Entity {
	
	public static int NEUTRAL 		= 0;
	public static int HUNTABLE 		= 1;
	public static int GATHERABLE 	= 2;
	public static int MINEABLE 		= 3;
	
	public int objectID;
	public float[] position;
	public float[] destination;
	public float[] color;
	public float[] rotation;
	public float[] direction;
	public float scale;
	public boolean show;
	public int type;

	public State currentState;
	public Entity focusEntity;
	
	public Vector2d force;
	public Vector2d acceleration;
	public Vector2d velocity;
	public double mass;
	public double max_v;
	
	public Inventory inventory;
	
	public Texture tex;
	
	//** Temporary Variables... implementation will change **//
	//public int inventory;
	public int pause;
	public Clan clanRef;

	public int timedump;
	
	public Entity() {
		
		position 	= new float[3];
		color 		= new float[3];
		rotation 	= new float[3];
		destination = new float[3];
		direction = new float[3];
		
		force 			= new Vector2d(0, 0);
		acceleration 	= new Vector2d(0, 0);
		velocity 		= new Vector2d(0, 0);
		mass			= 100;
		max_v			= .1;
		
		position[0] = position[1] 	= position[2] 	= 0f;
		color[0] 	= color[1] 		= color[2] 		= 1f;
		rotation[0] = rotation[1]	 = rotation[2] 	= 0f;
		destination[0] = destination[1] = destination[2] = 0f;
		direction[0] = direction[1] = direction[2] = 0;
		
		scale 		= .1f;
		show 		= true;
		objectID 	= -1;
		
		//inventory = 2; //full inventory
		inventory = new Inventory();
		clanRef = null;
		
		tex = null;
		timedump = 0;
	}
	
	public void setType(int _type) {
		type = _type;
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
			if(tex == null)
			{
				Resources.objectLibrary[objectID].draw();
			}else{
				Resources.objectLibrary[objectID].draw(tex);
			}
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
			
			direction[0] = (float) (velocity.x / velocity.length());
			direction[2] = (float) (velocity.y / velocity.length());
			
		}

		if (currentState != null) {
			currentState.execute(this);
		}
		
		timedump += timeElapsed;
	}
/*
	public boolean inventoryEmpty() {
		if (inventory == 0) 
			return true;
		else 
			return false;
	}
	*/

	public void retire() {
		focusEntity.type = Entity.NEUTRAL;
		focusEntity.rotation[0] += 180;
		focusEntity.position[1] += 1;
		focusEntity = null;		
	}

	public void setDestination(float[] target) {
		destination[0] = target[0];
		destination[1] = target[1];
		destination[2] = target[2];
	}
}
