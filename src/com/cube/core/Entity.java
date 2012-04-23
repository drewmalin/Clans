package com.cube.core;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import com.cube.states.State;
import com.cube.util.Texture;

public class Entity {
	
	public final static int NEUTRAL 		= 0;
	public final static int HUNTABLE 		= 1;
	public final static int GATHERABLE 		= 2;
	public final static int MINEABLE 		= 3;
	
	protected int selectionRingRotation;
	
	public int objectID;
	public Vector3d position;
	public Vector3d destination;
	public Vector3d direction;
	public float[] color;
	public float[] colorID;
	public float[] rotation;
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
	private int pause;
	public Clan clanRef;
	public int timedump;
	
	public Entity() {
		
		color 		= new float[3];
		rotation 	= new float[3];
		colorID		= new float[3];
		destination = new Vector3d(0, 0, 0);
		direction	= new Vector3d(0, 0, 0);
		position 	= new Vector3d(0, 0, 0);

		force 			= new Vector2d(0, 0);
		acceleration 	= new Vector2d(0, 0);
		velocity 		= new Vector2d(0, 0);
		mass			= 100;
		max_v			= .1;
		
		color[0] 	= color[1] 		= color[2] 		= 1f;
		rotation[0] = rotation[1]	 = rotation[2] 	= 0f;
		
		scale 		= .1f;
		show 		= true;
		objectID 	= -1;
		
		inventory = new Inventory();
		clanRef = null;
		
		tex = null;
		timedump = 0;
		pause = 0;
		selectionRingRotation = 0;

		/* Grab a unique color ID from Resources (float[3]) save it to this entity,
		 * convert it into a string ({12, 34, 51} becomes 123451... hashmaps don't
		 * work well with float arrays as keys) and save it to the global picking
		 * hashmap.
		 */
		setColorID(Resources.getNextColorID());
		Resources.pickingHashMap.put(Resources.colorIDToStringKey(colorID), this);
		System.out.println("New Entity! ColorID: " + Physics.printArray(colorID));

	}
	
	public void setType(int _type) {
		type = _type;
	}
	
	public void draw() {
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			
			GL11.glTranslated(position.x, position.y, position.z);
			GL11.glRotatef(rotation[0], 1, 0, 0);
			GL11.glRotatef(rotation[1], 0, 1, 0);
			GL11.glRotatef(rotation[2], 0, 0, 1);
			GL11.glScalef(scale, scale, scale);
			
			if (Graphics.colorPicking) {
				GL11.glColor3ub((byte)colorID[0], (byte)colorID[1], (byte)colorID[2]);
				Resources.objectLibrary[objectID].drawOBJ();
			}
			else {
				GL11.glColor3f(color[0], color[1], color[2]);
				if(tex == null)
				{
					Resources.objectLibrary[objectID].draw();
				}else{
					Resources.objectLibrary[objectID].draw(tex);
				}
			}
			
			if (this == Input.selectedEntity)
				drawSelectionRing();
			
		GL11.glPopMatrix();
	}
	
	/*
	 * Draws a texture underneath the entity. This texture is loaded in at load-time within the
	 * Resources module. The scale/positioning are currently hard coded. The function assumes the
	 * 'center' of the ring texture is at (textureWidth/2, textureHeight/2) and corrects for this
	 * by translating, rotating, and translating again to get the rotation to center at the texture's
	 * center.
	 */
	protected void drawSelectionRing() {
		Texture texture = Resources.selectionRing;
		if (selectionRingRotation > 360) selectionRingRotation -= 360;
		
		GL11.glPushMatrix();
		texture.bind();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);	
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glLoadIdentity();
		
		//Translate to the unit's position
		GL11.glTranslated(position.x, position.y - 0.1, position.z);
		//Rotate the ring about the origin
		GL11.glRotatef(selectionRingRotation++, 0, 1, 0);
		//Translate such that the center of the ring is at the origin
		GL11.glTranslated(-1, 0, -1);
		
		GL11.glScalef(3f, 3f, 3f);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		
		GL11.glBegin(GL11.GL_QUADS);	
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3d(0, 0, 0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3d(1, 0, 0);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3d(1, 0, 1);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3d(0, 0, 1);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);	
		GL11.glEnable(GL11.GL_LIGHTING);
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
			
			direction.x = velocity.x / velocity.length();
			direction.z = velocity.y / velocity.length();
			
		}

		if (currentState != null) {
			currentState.execute(this);
		}
		
		timedump += timeElapsed;
	}

	public void retire() {
		focusEntity.type = Entity.NEUTRAL;
		focusEntity.rotation[0] += 180;
		focusEntity.position.y += 1;
		focusEntity = null;		
	}

	public void setDestination(float[] target) {
		destination.x = target[0];
		destination.y = target[1];
		destination.z = target[2];
	}

	public void setDestination(Vector3d pos) {
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

	public void setColorID(float[] nextColorID) {
		colorID[0] = nextColorID[0];
		colorID[1] = nextColorID[1];
		colorID[2] = nextColorID[2];
	}
}
