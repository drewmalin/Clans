package com.cube.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;

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
	
	//------------- Misc ------------------//
	public static Random generator;
	
	public static double randomClamped() {
		return (generator.nextFloat() * 2) - 1;
	}
	
	public static double distSquared(float[] point1, float[] point2) {
		float a, b, c;
		
		a = (point1[0] - point2[0]) * (point1[0] - point2[0]);
		b = (point1[1] - point2[1]) * (point1[1] - point2[1]);
		c = (point1[2] - point2[2]) * (point1[2] - point2[2]);

		return a + b + c;
	}
	
	// *** Temporary location *** //
	public static String printArray(float[] array) {
		String ret = "(";
		
		for (float a : array) {
			ret += (a + ", ");
		}
		return ret + ")";
	}
	
	//------------- Wander ----------------//
	public static double targetDistance = 25;
	
	/*
	 * generateTarget takes the current position of the entity and randomly generates a 
	 * target position within targetDistance of the entity.
	 */
	public static void updateDestination(Entity e) {
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
