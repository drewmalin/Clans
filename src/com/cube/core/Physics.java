package com.cube.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import javax.vecmath.Vector2d;

public class Physics {
	public static ArrayList<Effect> effects;
	

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
	
	// *** Temporary location *** //
	/*
	 * Method to neatly print the contents of a float array.
	 */
	public static String printArray(float[] array) {
		String ret = "(";
		
		for (float a : array) {
			ret += (a + ", ");
		}
		return ret + ")";
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
		double currentX = e.position[0];
		double currentZ = e.position[2];
		Vector2d target;
		Vector2d current;
		
		target = new Vector2d(randomClamped(), randomClamped());
		current = new Vector2d(currentX, currentZ);
		
		target.normalize();
		target.scale(targetDistance);
		target.add(current);
		
		e.destination[0] = (float)target.x;
		e.destination[2] = (float) target.y;
	}
	
	/*
	 * In order to encourage an entity to move towards its destination position, the updateVelocity
	 * method provides a push in that direction. The purpose of this method is to return the new
	 * force vector the entity would require to arrive at its destination, however, the entity has
	 * a current velocity vector, meaning this force only slightly modifies the entity's movement;
	 * it does not guarantee instant arrival at the destination.
	 */
	public static Vector2d updateVelocity(Entity e) {
		Vector2d velocityFix = new Vector2d();
		
		velocityFix.x = e.destination[0] - e.position[0];
		velocityFix.y = e.destination[2] - e.position[2];
		velocityFix.normalize();
		velocityFix.scale(e.max_v);
		
		velocityFix.x -= e.velocity.x;
		velocityFix.y -= e.velocity.y;
		
		return velocityFix;
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

		e.position[0] += e.velocity.x;
		e.position[2] += e.velocity.y;		
	}

}
