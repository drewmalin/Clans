package com.cube.core;

import java.util.logging.Level;

import org.lwjgl.opengl.Display;
import com.cube.gui.Menu;
import com.cube.util.FileLogger;
import com.cube.util.Timer;
import com.cube.gui.ClickListener;

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
		Graphics.initialize();
		Resources.initialize();
		Input.initialize();
		Game.initialize();
		Physics.initialize();
		Timer.initialize();
		Menu.initialize();
		
		Resources.loadItemLib("/res/lib/ItemLib.xml");
		Resources.loadBuildingLib("/res/lib/BuildingLib.xml");
		Resources.loadEntityLib("/res/lib/EntityLib.xml");
		Resources.loadShaderLib("/res/lib/ShaderLib.xml");
		
		Menu.loadMenu("/res/menu/start.xml");
		Menu.loadMenu("/res/menu/options.xml");
		Menu.windows.get("start").buttons.get("newgame").setClickListener(new ClickListener() {
			public void onClick() {
				System.out.println("NEW GAME!");
			}
		});
		Menu.windows.get("start").buttons.get("continue").setClickListener(new ClickListener() {
			public void onClick() {
				Resources.loadSavedGame("/res/lvl/level1.xml");
				Menu.popMenuStack();
			}
		});
		Menu.windows.get("start").buttons.get("options").setClickListener(new ClickListener() {
			public void onClick() {
				Menu.pushMenuStack("options");
			}
		});
		Menu.windows.get("start").buttons.get("exit").setClickListener(new ClickListener() {
			public void onClick() {
				System.exit(0);
			}
		});
		
		Menu.windows.get("options").buttons.get("back").setClickListener(new ClickListener() {
			public void onClick() {
				Menu.popMenuStack();
			}
		});
		Menu.pushMenuStack("start");
					
		try {
			enterGameLoop();
		} catch (Exception e) {
			FileLogger.logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public static void enterGameLoop() throws Exception {
		
		while( Display.isCloseRequested() == false ) {
			
			if (!Menu.windowStack.isEmpty()) {
				Menu.update();
			}
			else {
				Timer.update();
			
				if (Timer.frameDelta < Timer.FRAME_LENGTH_MINIMUM)
					Thread.sleep(10);
				else
					Game.update();
			}
		}
		
		Display.destroy();
	}
}