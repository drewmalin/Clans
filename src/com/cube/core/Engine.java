package com.cube.core;

import java.util.logging.Level;

import org.lwjgl.opengl.Display;

import com.cube.gui.Menu;
import com.cube.util.FileLogger;
import com.cube.util.Timer;



public class Engine {
	
	public 	static boolean 	VBO = true;

	public	static int		WIDTH 	= 1024;
	public	static int		HEIGHT 	= 640;
	public 	static boolean	vSync 	= false;
	public 	static boolean 	fullscreen = false;
	public 	static int		MSAA 	= 4;
	public	static boolean	AA 		= false;
	public 	static int		framerate = 60;
	
	public	static float	zNear	= .1f;
	public	static float	zFar	= 1000f;
	public	static float	frust	= 45f;
	public  static String   title   = "Cuuuuuuuubes";
	
	public static void start() {
		
		FileLogger.initialize();
		Resources.initialize();
		Graphics.initialize();
		Input.initialize();
		Game.initialize();
		Physics.initialize();
		Timer.initialize();
		Menu.initialize();
		
		Resources.loadItems("res/items.xml");
		Resources.loadBuildings("res/buildings.xml");
		Resources.loadLevel("res/level1.xml");
		
		Menu.create();
		
		try {
			enterGameLoop();
		} catch (Exception e) {
			FileLogger.logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void enterGameLoop() throws Exception {
		
		while( Display.isCloseRequested() == false ) {
			
			Timer.update();
			
			if (Timer.frameDelta < Timer.FRAME_LENGTH_MINIMUM)
				Thread.sleep(10);
			else
				Game.update();
		}
		
		Display.destroy();
	}
}