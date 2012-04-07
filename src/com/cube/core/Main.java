package com.cube.core;

import java.util.logging.Level;

public class Main {
	
	public static void main(String[] args) {
		
		/* Create a hook to handle the game shutdown process. If the parent thread is ever killed,
		 * this 'run()' command will be executed, and the necessary cleanup can be performed
		 * immediately prior to shutdown.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { 
		    	FileLogger.logger.log(Level.INFO, "Game closing");
		    }
		});
		
		// Static call to start the game engine
		Engine.start();

	}
}