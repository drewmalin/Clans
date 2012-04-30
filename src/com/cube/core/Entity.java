package com.cube.core;

import java.util.ArrayList;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import org.lwjgl.opengl.GL11;
import com.cube.states.State;
import com.cube.util.Texture;

public class Entity {
	
	/* Entities each are given a unique type that dictates how they can
    be interacted with (if at all):
    
    0 - NEUTRAL    (no interaction, e.g. static scenery)
    1 - HUNTABLE   (entities that yield meat) <- may be deprecated
    2 - PASSIVE    (entities that yield meat/will flee if attacked)
    3 - AGGRESSIVE (entities that yield meat/will attack if attacked)
    4 - GATHERABLE (indedible entities like trees, dead animals)
    5 - MINEABLE   (mines, ore, gems, etc.)
    6 - EDIBLE     (entities that could be immediately eaten, e.g. berries)
	 */
	
	public final static int NEUTRAL 		= 0;
	public final static int HUNTABLE 		= 1;
	public final static int PASSIVE			= 2;
	public final static int AGGRESSIVE		= 3;
	public final static int GATHERABLE 		= 4;
	public final static int MINEABLE 		= 5;
	public final static int EDIBLE			= 6;
	
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
	
	public ArrayList<Integer> targets;
	public boolean underAttack;
	
	public int maxHealth;
	public int curHealth;
	
	public Entity() {
		
		maxHealth = curHealth = 5;
		
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

		targets = new ArrayList<Integer>();
		underAttack = false;
	}
	
	public void setType(int _type) {
		type = _type;
	}
	
	public void setMaxVelocity(float v) {
		max_v = v;
	}
	
	public void draw() {
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			
			GL11.glTranslated(position.x, position.y + Resources.map.getHeight((float)position.x, (float)position.z), position.z);
			GL11.glRotatef(rotation[0], 1, 0, 0);
			GL11.glRotatef(rotation[1], 0, 1, 0);
			GL11.glRotatef(rotation[2], 0, 0, 1);
			GL11.glScalef(scale, scale, scale);
			
			if (Graphics.colorPicking) {
				GL11.glColor3ub((byte)colorID[0], (byte)colorID[1], (byte)colorID[2]);
				Resources.objectLibrary.get(objectID).drawOBJ();
			}
			else {
				GL11.glColor3f(color[0], color[1], color[2]);
				if(tex == null)
				{
					Resources.objectLibrary.get(objectID).draw();
				}else{
					Resources.objectLibrary.get(objectID).draw(tex);
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
		GL11.glTranslated(position.x, position.y - 0.1 + Resources.map.getHeight((float)position.x, (float)position.z), position.z);
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
