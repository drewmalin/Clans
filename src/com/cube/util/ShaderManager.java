package com.cube.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import java.io.File;

public class ShaderManager {

	//Base directory which will store all of the shader code
	public static final String BASE_DIR = "res/shaders/";
	
	public static ArrayList<String> shaderFiles;
	
	//An enum of all of the shaders available
	public static enum ShaderType {HEMISPHERE};
	private static String[] shaderTypeString = {"hemi"};

	//ArrayList to contain the shaders
	private HashMap<String, Shader> shaderMap;
	
//CONSTRUCTOR:::
	public ShaderManager() {
		//Initialize the hashmap
		shaderMap = new HashMap<String, Shader>();
		shaderFiles = new ArrayList<String>();
	}
	
//PUBLIC METHODS:::
	//Adds a shader to the hashmap
	public void addShader(String key, Shader shader) {
		shaderMap.put(key, shader);
	}
	
	//Binds a shader directly from the hashmap
	public void bindShader(String key) {
		shaderMap.get(key).bind();
	}
	
	//Binds a shader directly from the hashmap
	public void bindShader(ShaderType key) {
		String keyString = shaderTypeString[key.ordinal()];
		shaderMap.get(keyString).bind();
	}
	
	//Creates a shader from the source file parameters and returns it
	public Shader createShader(String vertexShaderSource, String fragmentShaderSource) {
		return new Shader(vertexShaderSource, fragmentShaderSource);
	}
	
	//Grab a shader from the HashMap
	public final Shader getShader(String key) {
		return shaderMap.get(key);
	}
	
	//Grab a shader using the enum key
	public Shader getShader(ShaderType key) {
		String keyString = shaderTypeString[key.ordinal()];
		return shaderMap.get(keyString);
	}
	
	public void initialize() {
		findShaderFiles();
    	FileLogger.logger.log(Level.SEVERE, "Shaders initialized");
	}
	
	//Unbinds a shader directly from the hashmap
	public void unbindShader(String key) {
		shaderMap.get(key).unbind();
	}

	//Unbinds a shader directly from the hashmap
	public void unbindShader(ShaderType key) {
		String keyString = shaderTypeString[key.ordinal()];
		shaderMap.get(keyString).unbind();
	}

//PRIVATE METHODS:::
	//dynamically load shader files
	private void findShaderFiles() {
		ArrayList<String> vertNames = new ArrayList<String>();
		ArrayList<String> fragNames = new ArrayList<String>();
		//Get the base folder
		File folder = new File(BASE_DIR);
		//Create an array of all files in the folder
		ArrayList<File> fileArray = new ArrayList<File>(Arrays.asList(folder.listFiles()));
		//loop on each file in the folder
		for(File f: fileArray) {
			//make sure the file is a file, and not a directory
			if(f.isFile()) {
				//Get the name of the file
				String fileName = f.getName();
				//grab only the vertex shaders
				if(fileName.endsWith(".vert") || fileName.endsWith(".VERT")) {
					//grab only the name of the file and clip the extension
					//and add it to the list of vertex shader names
					vertNames.add(fileName.split("\\.")[0]);
				}
				if(fileName.endsWith(".frag") || fileName.endsWith(".FRAG")) {
					//grab only the name of the file and clip the extension
					//and add it to the list of fragment shader names
					fragNames.add(fileName.split("\\.")[0]);
				}
			}
		}//end for
		
		//for each vertex name we retrieved
		for(String vert: vertNames) {
			//check each fragment name we received
			for(String frag: fragNames) {
				//and see if they are the same
				if(vert.equals(frag)) {
					//If so, add the shader to the map under the filename
					addShader(vert, createShader(vert + ".vert", frag + ".frag"));
				} else {
			    	FileLogger.logger.log(Level.WARNING, "Vertex shader without matching fragment shader: " + vert + ".vert");
				}
			}
		}//end outer for
		
	}
	
}
