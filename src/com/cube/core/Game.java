package com.cube.core;

import java.util.logging.Level;

public class Game {

	
	public static void initialize() {
		FileLogger.logger.log(Level.INFO, "Game initialized");
	}

	public static void update() {
		Input.poll();
		Graphics.update();
	}
}
