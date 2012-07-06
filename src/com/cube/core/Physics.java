package com.cube.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.cube.util.FileLogger;

public class Physics {
	public static ArrayList<Effect> effects;
	public enum SPEED {
		FAST(2f), MEDIUM(0.3f), SLOW(0.1f);
		
		private float val;
		private SPEED(float v) {
			this.val = v;
		}
		public float value() {
			return val;
		}
	};

	public static void initialize() {
		generator = new Random(System.currentTimeMillis());
		effects = new ArrayList<Effect>();
		FileLogger.logger.log(Level.INFO, "Physics initialized");
	}
		
	public static void drawEffects() {
		for (Effect e : effects) {
			e.updateParticles();
			e.drawParticles();
		}
	}
	
	//-------------------------------------------------------------------------//
	//---------------------- Miscellaneous Functions --------------------------//
	//-------------------------------------------------------------------------//
	public static Random generator;
	
	/*
	 * Method to return a random number between the values of -1.0 and 1.0.
	 */
	public static float randomClamped() {
		return (generator.nextFloat() * 2) - 1;
	}
	
	/*
	 * Method to return the Euclidean distance between two 3D points. The distance returned
	 * from the function is the squared distance--the use of the expensive square root operator
	 * has been avoided.
	 */
	public static float distSquared(Vector3f vector3f, Vector3f position) {
		float a, b, c;
		
		a = (vector3f.x - position.x) * (vector3f.x - position.x);
		b = (vector3f.y - position.y) * (vector3f.y - position.y);
		c = (vector3f.z - position.z) * (vector3f.z - position.z);

		return a + b + c;
	}
	/*
	 * Rotate a vector theta degrees about the y axis. This is meant to be used for movement
	 * vectors that are fixed to the x-z plane.
	 */
	public static void rotateVector2f(Vector2f vect, double theta) {
		
		float convertToRads = (float) ((2 * Math.PI) / 360); 

		float rx = (float) ((vect.x * Math.cos(theta * convertToRads)) + (vect.y * Math.sin(theta * convertToRads)));
		float ry = (float) ((-vect.x * Math.sin(theta * convertToRads)) + (vect.y * Math.cos(theta * convertToRads)));
		
		vect.x = rx;
		vect.y = ry;
	}
	
	public static void rotateVector3f(Vector3f v, float rotX, float rotY, float rotZ) {
		float convertToRads = (float) ((2 * Math.PI) / 360); 
		float tempX, tempY, tempZ;
		
		//Rotate about the x axis
		tempX = v.x;
		tempY = (float) ((v.y * Math.cos(rotX * convertToRads)) - (v.z * Math.sin(rotX * convertToRads)));
		tempZ = (float) ((v.y * Math.sin(rotX * convertToRads)) + (v.z * Math.cos(rotX * convertToRads)));
		
		v.x = tempX;
		v.y = tempY;
		v.z = tempZ;
		
		//Rotate about the y axis
		tempX = (float) ((v.x * Math.cos(rotY * convertToRads)) + (v.z * Math.sin(rotY * convertToRads)));
		tempY = v.y;
		tempZ = (float) ((-v.x * Math.sin(rotY * convertToRads)) + (v.z * Math.cos(rotY * convertToRads)));
		
		v.x = tempX;
		v.y = tempY;
		v.z = tempZ;
		
		//Rotate about the z axis
		tempX = (float) ((v.x * Math.cos(rotZ * convertToRads)) - (v.y * Math.sin(rotZ * convertToRads)));
		tempY = (float) ((v.x * Math.sin(rotZ * convertToRads)) + (v.y * Math.cos(rotZ * convertToRads)));
		tempZ = v.z;
		
		v.x = tempX;
		v.y = tempY;
		v.z = tempZ;
	}
	
	/*
	 * Clamp a value (original) to the range of min, max. For example, the parms (10, 6, 12)
	 * will return 10 (original is within the range) whereas the parms(7, 12, 18) will return
	 * 12 (12 is the closest value within the range to the original value).
	 */
	public static float clamp(float original, float min, float max) {
		if (original >= min && original <= max)
			return original;
		else if (original < min)
			return min;
		else 
			return max;
	}
	
	//-------------------------------------------------------------------------//
	//--------------------------- Movement Functions --------------------------//
	//-------------------------------------------------------------------------//
	
	/*
	 * Entities within the game each have a destination towards which they can travel. The updateDestination
	 * method calculates a new random destination (for use mainly with any sort of wander, hunt, or search
	 * type of movement). The calculation of a new destination places the destination at targetDistance away
	 * from the entity parm's current position.
	 */
	public static void updateDestination(Entity e, float targetDistance) {
		float currentX = e.position.x;
		float currentZ = e.position.z;
		Vector2f target;
		Vector2f current;
		
		target = new Vector2f(randomClamped(), randomClamped());
		current = new Vector2f(currentX, currentZ);
		
		target.normalise();
		target.scale(targetDistance);
		//target.add(current);
		Vector2f.add(target, current, target);
		
		e.destination.x = target.x;
		e.destination.z = target.y;
	}
	
	/*
	 * In order to encourage an entity to move towards its destination position, the updateVelocity
	 * method provides a push in that direction. The purpose of this method is to return the new
	 * force vector the entity would require to arrive at its destination, however, the entity has
	 * a current velocity vector, meaning this force only slightly modifies the entity's movement;
	 * it does not guarantee instant arrival at the destination.
	 */
	public static Vector2f seekDestination(Entity e) {
		Vector2f velocityFix = new Vector2f();
		Vector2f obstacleAvoid = new Vector2f();
		
		velocityFix.x = e.destination.x - e.position.x;
		velocityFix.y = e.destination.z - e.position.z;
		velocityFix.normalise();
		velocityFix.scale((float) e.max_v);
		
		//velocityFix.sub(e.velocity);
		Vector2f.sub(velocityFix, e.velocity, velocityFix);
		obstacleAvoid = avoidObstacles(e);
		
		//velocityFix.add(obstacleAvoid);
		Vector2f.add(velocityFix, obstacleAvoid, velocityFix);
		
		return velocityFix;
	}
	
	/*
	 * Function to avoid obstacles in the path of the unit/entity. The function creates a rectangle extending 20
	 * distance-units in front of the entity, and 1 distance-unit to either side. Each entity has a 1-distance-unit 
	 * radius collision circle around them. If these collision circles ever overlap the unit's rectangle, the unit
	 * is push laterally in the direction that will avoid collision the quickest, and slows down depending on how 
	 * close the colliding entity's collision circle is.
	 */
	public static Vector2f avoidObstacles(Entity e) {
		
		Vector2f push = new Vector2f(0, 0);
		double dist;
		
		// For all entities on the map
		for (Entity ent : Resources.entities) {
			//If the entity is not the current entity
			if (!e.equals(ent) && ent != e.focusEntity) {
				
				Vector2f globalEntPos = new Vector2f(ent.position.x, ent.position.z);
				Vector2f globalEPos = new Vector2f(e.position.x, e.position.z);
				
				dist = distSquared(e.position, ent.position);

				// If the two entities are close enough to eventually collide
				if (dist < 25 && Vector2f.dot(globalEntPos, globalEPos) > 0) {

					//Convert the ent position to move it into e's local axis
					globalEntPos.x = (globalEntPos.x - e.position.x);
					globalEntPos.y = (globalEntPos.y - e.position.z);
					rotateVector2f(globalEntPos, -e.rotation[1]);

					//From here on, disregard the rotation of e
					float top 		= -30;
					float bottom 	= 0;
					float left 	= -1;
					float right 	= 1;
					
					//Find the closest point to the circle within the rectangle
					float closestX = clamp(globalEntPos.x, left, right);
					float closestZ = clamp(globalEntPos.y, top, bottom);
					
					//Calculate the distance between the circle's center and the closest point
					float distanceX = globalEntPos.x - closestX;
					float distanceZ = globalEntPos.y - closestZ;
					
					//If the the distance is less than the circle's radius, collision
					float distSquared = (distanceX * distanceX) + (distanceZ * distanceZ);

					//Hard code circle radius to 1
					float radius = 1;
					if (distSquared < (radius * radius)) {
						
						float brakingWeight = 0.2f;
						push.y = ((radius - globalEntPos.y) * brakingWeight);
						
						float multiplier = 1.0f + (20 + globalEntPos.y) / 20;
						push.x = (radius - globalEntPos.x) * multiplier * .25f;
						
						rotateVector2f(push, e.rotation[1]);
						
					}
				}
			}
		}
		return push;
	}
	
	/*
	 * As entities move through space, their position must be updated based on their current movement.
	 * updatePosition calculates the current acceleration, velocity vector, and new position of the
	 * entity parm. This means that movement through the world is judged by the force vector applied
	 * to entities (this could have been replaced with just applying a velocity vector, but the ability
	 * for mass to play a role in later movement seems cool to me).
	 */
	public static void updatePosition(Movable movable) {
		// a = F/m
		movable.acceleration.x = movable.force.x / movable.mass;
		movable.acceleration.y = movable.force.y / movable.mass;
		
		movable.velocity.x += movable.acceleration.x;
		movable.velocity.y += movable.acceleration.y;
		
		if (Math.abs(movable.velocity.x) > movable.max_v) movable.velocity.x = movable.max_v * (movable.velocity.x / Math.abs(movable.velocity.x));
		if (Math.abs(movable.velocity.y) > movable.max_v) movable.velocity.y = movable.max_v * (movable.velocity.y / Math.abs(movable.velocity.y));

		movable.position.x += movable.velocity.x;
		movable.position.z += movable.velocity.y;		
	}


	/*
	 * Stop!
	 */
	public static void haltEntity(Entity e) {
		e.velocity.x = 0;
		e.velocity.y = 0;		
		
		e.force.x = 0;
		e.force.y = 0;
	}

	public static Vector2f arrive(Entity e) {
		Vector2f velocityFix = new Vector2f(0, 0);
		
		float dist = distSquared(e.position, e.destination);
		float deceleration = SPEED.FAST.value();
		float speed;
		
		if (dist < 8) {
			speed = dist / deceleration;
			speed = Math.min(speed, e.max_v);
			
			velocityFix.x = e.position.x - e.destination.x;
			velocityFix.y = e.position.z - e.destination.z;
			
			velocityFix.normalise();
			velocityFix.scale(speed);
		}
		
		return velocityFix;
	}
	
	public static Vector2f fleeFocusEntity(Entity e) {
		Vector2f velocityFix = new Vector2f();
		Vector2f obstacleAvoid = new Vector2f();
		
		velocityFix.x = e.position.x - e.focusEntity.position.x;
		velocityFix.y = e.position.z - e.focusEntity.position.z;
		velocityFix.normalise();
		velocityFix.scale(e.max_v);
		
		//velocityFix.sub(e.velocity);
		Vector2f.sub(velocityFix, e.velocity, velocityFix);
		
		obstacleAvoid = avoidObstacles(e);
		//velocityFix.add(obstacleAvoid);
		Vector2f.add(velocityFix, obstacleAvoid, velocityFix);
		return velocityFix;
	}
	
	public static Vector2f pursueFocusEntity(Entity e) {
		Vector2f velocityFix = new Vector2f();
		Vector2f obstacleAvoid = new Vector2f();
		Vector2f estimatedPosition = new Vector2f();
		
		float estimateTime = (float) Math.sqrt(Physics.distSquared(e.position, e.focusEntity.position));
		estimateTime /= (e.max_v + e.focusEntity.max_v);
		
		estimatedPosition.x = e.focusEntity.position.x + e.focusEntity.max_v * estimateTime;
		estimatedPosition.y = e.focusEntity.position.z + e.focusEntity.max_v * estimateTime;
		
		velocityFix.x = estimatedPosition.x - e.position.x;
		velocityFix.y = estimatedPosition.y - e.position.z;
		velocityFix.normalise();
		velocityFix.scale(e.max_v);
		
		//velocityFix.sub(e.velocity);
		Vector2f.sub(velocityFix, e.velocity, velocityFix);
		
		obstacleAvoid = avoidObstacles(e);
		//velocityFix.add(obstacleAvoid);
		Vector2f.add(velocityFix, obstacleAvoid, velocityFix);
		
		return velocityFix;
	}
	
	public static boolean proximityCollision(Entity e1, Entity e2) {
		
		double euclidDist = distSquared(e1.position, e2.position);
		double collisionDist = (e1.proximityRadius + e2.proximityRadius) * (e1.proximityRadius + e2.proximityRadius);
		
		if (euclidDist < collisionDist)
			return true;
		else
			return false;
	}

	public static float calculateAverageHeight(Building building) {
		float sum = 0, avg = 0;
		int x = (int) (Resources.map.width/2 + building.position.x);
		int z = (int) (Resources.map.height/2 + building.position.z);
		
		for (int j = z - (building.height/2); j < z + (building.height/2); j++) {
			for (int i = x - (building.width/2); i < x + (building.width/2); i++) {
				sum += Resources.map.pixels[i][j][0];
			}
		}
		
		avg = sum / (building.width * building.height);		
		return avg;
	}
}
