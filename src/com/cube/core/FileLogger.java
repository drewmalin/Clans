package com.cube.core;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogger {
	
	private static FileHandler handler;
	private static SimpleFormatter formatter;
	public static Logger logger;
	
	public static void initialize() {
		try {
			formatter = new SimpleFormatter();
			handler = new FileHandler("logs/status.log");
			handler.setFormatter(formatter);
			
			logger = Logger.getLogger("Status");
			logger.addHandler(handler);
			
			logger.log(Level.INFO, "Logger created");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
