package com.cube.util;

import java.util.HashMap;
import java.util.logging.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShaderManager {

	//Used to read in the XML config file
	private static BufferedReader br;
	
	//ArrayList to contain the shaders
	private HashMap<String, Shader> shaderMap;
	
//CONSTRUCTORS:::
	/**
	 * Create and instance of ShaderManager
	 * 
	 * @param filename The filename of the shader XML config file
	 */
	public ShaderManager(String filename) {
		//Initialize the hashmap
		shaderMap = new HashMap<String, Shader>();
		//Load the shaders
		loadShaders(filename);
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
		
	/**
	 * Creates a shader from the source parameters and returns it
	 * 
	 * @param vertexShaderSource The source as a string for the vertex shader
	 * @param fragmentShaderSource The Source as a string for the fragment shader
	 * @return Shader instance that was created.
	 */
	public Shader createShader(String vertexShaderSource, String fragmentShaderSource) {
		return new Shader(vertexShaderSource, fragmentShaderSource);
	}
	
	//Grab a shader from the HashMap
	public final Shader getShader(String key) {
		return shaderMap.get(key);
	}
		
	public void initialize() {
    	FileLogger.logger.log(Level.INFO, "Shaders initialized");
	}
	
	public void loadShaders(String filename) {
		XMLParser xp = new XMLParser(filename);
		String shaderID = new String();
		String vertexShaderFile = new String();
		String fragmentShaderFile = new String();
		
		//Loop through each <shader> tag
		for(Node shaderEl : xp.root.children) {
			//Reset the variables
			shaderID = new String();
			vertexShaderFile = new String();
			fragmentShaderFile = new String();
			
			//Loop through each data tag within
			for(Node dataEl : shaderEl.children) {
				if(dataEl.name.toLowerCase().equals("id")) {
					shaderID = dataEl.data;
				} else if(dataEl.name.toLowerCase().equals("vertexshader")) {
					vertexShaderFile = dataEl.data;
				} else if(dataEl.name.toLowerCase().equals("fragmentshader")) {
					fragmentShaderFile = dataEl.data;
				}
			}//end data loop
			//Make sure all of the data has a value
			if( (!shaderID.equals("")) && (!vertexShaderFile.equals("")) && (!fragmentShaderFile.equals("")) ) {
				//Create a new shader from the data
				Shader s = new Shader(vertexShaderFile, fragmentShaderFile);
				//Add the shader to the map
				addShader(shaderID, s);
			}
		}//end <shader> loop
		
	}
	
	public String readShaderSource(String filename) {
		StringBuilder source = new StringBuilder();
		try {
			//Prepare the file for reading
			br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(filename)));
            String line;
            //Read the file, line by line
            while ((line = br.readLine()) != null) {
                source.append(line).append('\n');
            }
		} catch (IOException e) {
	    	FileLogger.logger.log(Level.SEVERE, "Vertex shader failed to load properly!");
		}
		return source.toString();
	}
	
	//Unbinds a shader directly from the hashmap
	public void unbindShader(String key) {
		shaderMap.get(key).unbind();
	}
	
	

//PRIVATE METHODS:::

}
