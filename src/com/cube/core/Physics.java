package com.cube.core;

import java.util.ArrayList;
import java.util.logging.Level;

public class Physics {
	public static ArrayList<Effect> effects;

	public static void initialize() {
		effects = new ArrayList<Effect>();
		FileLogger.logger.log(Level.INFO, "Physics initialized");
	}
		
	public static void drawEffects() {
		for (Effect e : effects) {
			e.updateParticles();
			e.drawParticles();
		}
	}
}
