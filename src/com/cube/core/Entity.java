package com.cube.core;

import java.util.ArrayList;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import org.lwjgl.opengl.GL11;

import com.cube.states.AttackState;
import com.cube.states.FleeState;
import com.cube.states.State;
import com.cube.states.TravelState;
import com.cube.util.Texture;
import com.cube.util.Utilities;

public class Entity extends Movable {
		
	public String name;
	public String model;
	public String texture;
	
	public float[] scale;

	public Entity focusEntity;
	public Inventory inventory;
	public ArrayList<String> types;
	public ArrayList<String> targets;
	public ArrayList<String> regions;
	public Status status;

	public int maxHealth;
	public int curHealth;
	
	public float popDensity;
	
	//--- TEMPORARY LOCATION ---//
	protected int selectionRingRotation;
	public long waitMillis;
	public long waitDelta;
	
	//----------- Unique Entity attributes -------------//
	public State currentState;
	public State previousState;
	
	public boolean userControlled;	
	
	public float proximityRadius;
	
	public boolean underAttack;
	

	//----------------------------------------------------//
	public Clan clanRef;

	
	public Entity() {
		super();
		
		model = "default";
		
		proximityRadius = 1.5f;
		maxHealth = curHealth = 5;
		scale = new float[]{0.5f, 0.5f, 0.5f};
		
		inventory = new Inventory();
		
		texture = null;
		selectionRingRotation = 0;

		/* Grab a unique color ID from Resources (float[3]) save it to this entity,
		 * convert it into a string ({12, 34, 51} becomes 123451... hashmaps don't
		 * work well with float arrays as keys) and save it to the global picking
		 * hashmap.
		 */
		setColorID(Resources.getNextColorID());
		Resources.pickingHashMap.put(Resources.colorIDToStringKey(colorID), this);

		targets = new ArrayList<String>();
		types = new ArrayList<String>();
		regions = new ArrayList<String>();
		
		underAttack = false;
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
	
	public void setType(String _type) {
		types.add(_type);
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
			GL11.glScalef(scale[0], scale[1], scale[2]);
			
			if (Graphics.colorPicking) {
				GL11.glColor3ub((byte)colorID[0], (byte)colorID[1], (byte)colorID[2]);
			//	Resources.objectLibrary.get(objectID).drawOBJ();
				Resources.modelLibrary.get(model).drawOBJ();
			}
			else {
				GL11.glColor3f(color[0], color[1], color[2]);
				if(texture == null)
				{
					Resources.modelLibrary.get(model).draw();
				}else{
					Resources.modelLibrary.get(model).draw(Resources.textureLibrary.get(texture));
				}
			}
			
			if (this == Game.selectedEntity)
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
		
	/*
	 * Separated from testInteraction solely for the purposes of tidying up code. Method tests
	 * the types of the focus entity and switches the states of this entity and its focus entity
	 * accordingly.
	 * 
	 * N.B.: This does not assume the entity is in close proximity to its focus.
	 */
	public void interactWithFocusEntity() {
		//Focus entity is immediately edible/gatherable, travel to it
		if (focusEntity.types.contains(Type.edible) || focusEntity.types.contains(Type.gatherable))
			changeState( TravelState.getState() );
		//Focus entity is a building
		else if (focusEntity.getClass().equals(Building.class)) {
			changeState( TravelState.getState() );
		}
		//Focus entity must be attacked
		else {
			changeState( AttackState.getState() );
			focusEntity.focusEntity = this;

			// If the focus is passive, it should flee
			if (focusEntity.types.contains(Type.passive))	
				focusEntity.changeState( FleeState.getState() );
			// If the focus is not passive, it counter attacks
			else
				focusEntity.changeState( AttackState.getState() );
		}
	}
	
	/*
	 * Method to change the state of the selected entity in order to interact properly with the
	 * target entity. 
	 */
	public void testInteraction(Entity targetEntity) {
		//User has selected the target this unit is already focusing on. Ignore and return
		if (focusEntity != null && focusEntity.equals(targetEntity)) {
			return;
		}
		else {
			//The entity is not already focused on this new target, if the entity is
			//able to interact with the new target, redirect
			if (Utilities.containsAny(targets, targetEntity.types)) {
				focusEntity = targetEntity;
				interactWithFocusEntity();
			}
			//The entity is not currently focused on the new target, but it also cannot
			//interact with it. Ignore and return.
			else {
				return;
			}
		}
	}
	
	public void changeState(State newState) {
		if (currentState != null && newState != null && !currentState.equals(newState)) {
			currentState.exit(this);
			currentState = newState;
			currentState.enter(this);
		}
	}

	public void retire() {
		focusEntity.types.clear();
		focusEntity.types.add(Type.neutral);
		focusEntity = null;		
	}
	
	public void setColorID(float[] nextColorID) {
		colorID[0] = nextColorID[0];
		colorID[1] = nextColorID[1];
		colorID[2] = nextColorID[2];
	}

	public void setScale(float[] scaleArr) {
		scale[0] = scaleArr[0];
		scale[1] = scaleArr[1];
		scale[2] = scaleArr[2];
	}

	public void setTypes(String[] typeArr) {
		for (String type : typeArr) {
			types.add(type);
		}
	}

	public void setTargets(String[] targetArr) {
		for (String target : targetArr) {
			targets.add(target);
		}
	}

	public void setRegions(String[] regionArr) {
		for (String region : regionArr) {
			regions.add(region);
		}
	}

	public void setPopDensity(float density) {
		popDensity = density;
	}

	public void setTexture(String textureFile) {
		texture = textureFile.substring(textureFile.lastIndexOf("/")+1, textureFile.indexOf("."));
		if (Resources.textureLibrary.get(texture) == null) {
			try {
				Texture newTexture = Resources.texLoader.getTexture(textureFile);
				Resources.textureLibrary.put(texture, newTexture);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setModel(String modelFile) {
		model = modelFile.substring(modelFile.lastIndexOf("/")+1, modelFile.indexOf("."));
		if (Resources.modelLibrary.get(model) == null) {
			Model newModel = new Model(modelFile);
			Resources.modelLibrary.put(model, newModel);
		}
	}

	public ArrayList<String> getRoles() {
		return null;
	}
	
	public Clan getClanRef() {
		return clanRef;
	}
}
