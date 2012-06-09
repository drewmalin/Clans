package com.cube.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import com.cube.util.FileLogger;

public class Physics {
	public static ArrayList<Effect> effects;
	public enum SPEED {
		FAST(2), MEDIUM(0.3), SLOW(0.1);
		
		private double val;
		private SPEED(double v) {
			this.val = v;
		}
		public double value() {
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
	public static double randomClamped() {
		return (generator.nextFloat() * 2) - 1;
	}
	
	/*
	 * Method to return the Euclidean distance between two 3D points. The distance returned
	 * from the function is the squared distance--the use of the expensive square root operator
	 * has been avoided.
	 */
	public static double distSquared(float[] point1, float[] point2) {
		float a, b, c;
		
		a = (point1[0] - point2[0]) * (point1[0] - point2[0]);
		b = (point1[1] - point2[1]) * (point1[1] - point2[1]);
		c = (point1[2] - point2[2]) * (point1[2] - point2[2]);

		return a + b + c;
	}
	
	public static double distSquared(Vector3d point1, Vector3d point2) {
		double a, b, c;
		
		a = (point1.x - point2.x) * (point1.x - point2.x);
		b = (point1.y - point2.y) * (point1.y - point2.y);
		c = (point1.z - point2.z) * (point1.z - point2.z);

		return a + b + c;
	}
	
	/*
	 * Rotate a vector theta degrees about the y axis. This is meant to be used for movement
	 * vectors that are fixed to the x-z plane.
	 */
	public static void rotateVector2d(Vector2d vect, double theta) {
		
		double convertToRads = (float) ((2 * Math.PI) / 360); 

		double rx = (vect.x * Math.cos(theta * convertToRads)) + (vect.y * Math.sin(theta * convertToRads));
		double ry = (-vect.x * Math.sin(theta * convertToRads)) + (vect.y * Math.cos(theta * convertToRads));
		
		vect.x = rx;
		vect.y = ry;
	}
	
	public static void rotateVector3d(Vector3d v, float rotX, float rotY, float rotZ) {
		float convertToRads = (float) ((2 * Math.PI) / 360); 
		double tempX, tempY, tempZ;
		
		//Rotate about the x axis
		tempX = v.x;
		tempY = (v.y * Math.cos(rotX * convertToRads)) - (v.z * Math.sin(rotX * convertToRads));
		tempZ = (v.y * Math.sin(rotX * convertToRads)) + (v.z * Math.cos(rotX * convertToRads));
		
		v.x = tempX;
		v.y = tempY;
		v.z = tempZ;
		
		//Rotate about the y axis
		tempX = (v.x * Math.cos(rotY * convertToRads)) + (v.z * Math.sin(rotY * convertToRads));
		tempY = v.y;
		tempZ = (-v.x * Math.sin(rotY * convertToRads)) + (v.z * Math.cos(rotY * convertToRads));
		
		v.x = tempX;
		v.y = tempY;
		v.z = tempZ;
		
		//Rotate about the z axis
		tempX = (v.x * Math.cos(rotZ * convertToRads)) - (v.y * Math.sin(rotZ * convertToRads));
		tempY = (v.x * Math.sin(rotZ * convertToRads)) + (v.y * Math.cos(rotZ * convertToRads));
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
	public static double clamp(double original, double min, double max) {
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
	public static void updateDestination(Entity e, double targetDistance) {
		double currentX = e.position.x;
		double currentZ = e.position.z;
		Vector2d target;
		Vector2d current;
		
		target = new Vector2d(randomClamped(), randomClamped());
		current = new Vector2d(currentX, currentZ);
		
		target.normalize();
		target.scale(targetDistance);
		target.add(current);
		
		e.destination.x = (float)target.x;
		e.destination.z = (float) target.y;
	}
	
	/*
	 * In order to encourage an entity to move towards its destination position, the updateVelocity
	 * method provides a push in that direction. The purpose of this method is to return the new
	 * force vector the entity would require to arrive at its destination, however, the entity has
	 * a current velocity vector, meaning this force only slightly modifies the entity's movement;
	 * it does not guarantee instant arrival at the destination.
	 */
	public static Vector2d seekDestination(Entity e) {
		Vector2d velocityFix = new Vector2d();
		Vector2d obstacleAvoid = new Vector2d();
		
		velocityFix.x = e.destination.x - e.position.x;
		velocityFix.y = e.destination.z - e.position.z;
		velocityFix.normalize();
		velocityFix.scale(e.max_v);
		
		velocityFix.sub(e.velocity);

		obstacleAvoid = avoidObstacles(e);
		
		velocityFix.add(obstacleAvoid);
		
		return velocityFix;
	}
	
	/*
	 * Function to avoid obstacles in the path of the unit/entity. The function creates a rectangle extending 20
	 * distance-units in front of the entity, and 1 distance-unit to either side. Each entity has a 1-distance-unit 
	 * radius collision circle around them. If these collision circles ever overlap the unit's rectangle, the unit
	 * is push laterally in the direction that will avoid collision the quickest, and slows down depending on how 
	 * close the colliding entity's collision circle is.
	 */
	public static Vector2d avoidObstacles(Entity e) {
		
		Vector2d push = new Vector2d(0, 0);
		double dist;
		
		// For all entities on the map
		for (Entity ent : Resources.entities) {
			//If the entity is not the current entity
			if (!e.equals(ent) && ent != e.focusEntity) {
				
				Vector2d globalEntPos = new Vector2d(ent.position.x, ent.position.z);
				Vector2d globalEPos = new Vector2d(e.position.x, e.position.z);
				
				dist = distSquared(e.position, ent.position);

				// If the two entities are close enough to eventually collide
				if (dist < 25 && globalEntPos.dot(globalEPos) > 0) {

					//Convert the ent position to move it into e's local axis
					globalEntPos.x = (globalEntPos.x - e.position.x);
					globalEntPos.y = (globalEntPos.y - e.position.z);
					rotateVector2d(globalEntPos, -e.rotation[1]);

					//From here on, disregard the rotation of e
					double top 		= -30;
					double bottom 	= 0;
					double left 	= -1;
					double right 	= 1;
					
					//Find the closest point to the circle within the rectangle
					double closestX = clamp(globalEntPos.x, left, right);
					double closestZ = clamp(globalEntPos.y, top, bottom);
					
					//Calculate the distance between the circle's center and the closest point
					double distanceX = globalEntPos.x - closestX;
					double distanceZ = globalEntPos.y - closestZ;
					
					//If the the distance is less than the circle's radius, collision
					double distSquared = (distanceX * distanceX) + (distanceZ * distanceZ);

					//Hard code circle radius to 1
					double radius = 1;
					if (distSquared < (radius * radius)) {
						
						double brakingWeight = 0.2;
						push.y = ((radius - globalEntPos.y) * brakingWeight);
						
						double multiplier = 1.0 + (20 + globalEntPos.y) / 20;
						push.x = (radius - globalEntPos.x) * multiplier * .25;
						
						rotateVector2d(push, e.rotation[1]);
						
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
	public static void updatePosition(Entity e) {
		// a = F/m
		e.acceleration.x = e.force.x / e.mass;
		e.acceleration.y = e.force.y / e.mass;
		
		e.velocity.x += e.acceleration.x;
		e.velocity.y += e.acceleration.y;
		
		if (Math.abs(e.velocity.x) > e.max_v) e.velocity.x = e.max_v * (e.velocity.x / Math.abs(e.velocity.x));
		if (Math.abs(e.velocity.y) > e.max_v) e.velocity.y = e.max_v * (e.velocity.y / Math.abs(e.velocity.y));

		e.position.x += e.velocity.x;
		e.position.z += e.velocity.y;		
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

	public static Vector2d arrive(Entity e) {
		Vector2d velocityFix = new Vector2d(0, 0);
		
		double dist = distSquared(e.position, e.destination);
		double deceleration = SPEED.FAST.value();
		double speed;
		
		if (dist < 8) {
			speed = dist / deceleration;
			speed = Math.min(speed, e.max_v);
			
			velocityFix.x = e.position.x - e.destination.x;
			velocityFix.y = e.position.z - e.destination.z;
			
			velocityFix.normalize();
			velocityFix.scale(speed);
		}
		
		return velocityFix;
	}
	
	public static Vector2d fleeFocusEntity(Entity e) {
		Vector2d velocityFix = new Vector2d();
		Vector2d obstacleAvoid = new Vector2d();
		
		velocityFix.x = e.position.x - e.focusEntity.position.x;
		velocityFix.y = e.position.z - e.focusEntity.position.z;
		velocityFix.normalize();
		velocityFix.scale(e.max_v);
		
		velocityFix.sub(e.velocity);

		obstacleAvoid = avoidObstacles(e);
		velocityFix.add(obstacleAvoid);
		return velocityFix;
	}
	
	public static Vector2d pursueFocusEntity(Entity e) {
		Vector2d velocityFix = new Vector2d();
		Vector2d obstacleAvoid = new Vector2d();
		Vector2d estimatedPosition = new Vector2d();
		
		double estimateTime = Math.sqrt(Physics.distSquared(e.position, e.focusEntity.position));
		estimateTime /= (e.max_v + e.focusEntity.max_v);
		
		estimatedPosition.x = e.focusEntity.position.x + e.focusEntity.max_v * estimateTime;
		estimatedPosition.y = e.focusEntity.position.z + e.focusEntity.max_v * estimateTime;
		
		velocityFix.x = estimatedPosition.x - e.position.x;
		velocityFix.y = estimatedPosition.y - e.position.z;
		velocityFix.normalize();
		velocityFix.scale(e.max_v);
		
		velocityFix.sub(e.velocity);
		
		obstacleAvoid = avoidObstacles(e);
		velocityFix.add(obstacleAvoid);
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
