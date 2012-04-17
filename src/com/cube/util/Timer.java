package com.cube.util;

import java.util.logging.Level;


public class Timer {
	private static long lastCheckTime;

	public static final long FRAME_LENGTH_MINIMUM = 10000000;
	public static long frameDelta;
	
	public Timer(){
		lastCheckTime = System.nanoTime();
	}
	
	public static void initialize() {
		lastCheckTime = System.nanoTime();
			
		FileLogger.logger.log(Level.INFO, "Timer initialized");
	}
	
	public static long getNanoDelta(){
		long temp = System.nanoTime();
		long delta = temp - lastCheckTime;
		lastCheckTime = temp;
		return delta;
	}

	public static void update() {
		frameDelta = getNanoDelta();
	}
}
