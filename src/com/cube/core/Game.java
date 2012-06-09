package com.cube.core;

import java.util.logging.Level;

import com.cube.gui.Menu;
import com.cube.util.FileLogger;
import com.cube.util.Timer;
import com.cube.util.Utilities;

public class Game {

	public static Entity selectedEntity;
	public static Building buildingToBeBuilt;
	public static Clan playerClan;
	
	public static void initialize() {
		FileLogger.logger.log(Level.INFO, "Game initialized");
	}

	public static void update() {
		Input.poll();
		Graphics.update();
		
		if (!Menu.windows.get(Menu.PAUSE).stealContext)
			updateScene();
		
		Utilities.updateFrameRate();
		
	}
	
	private static void updateScene() {
		int timeElapsed_ms = (int) Timer.getNanoDelta() / 1000000;

		if (timeElapsed_ms > 10) {
			timeElapsed_ms = 1;
		}

		for (Entity e : Resources.entities) {
			e.update(timeElapsed_ms);
		}
		for (Clan c : Resources.clans) {
			c.update(timeElapsed_ms);
		}
	}
}
