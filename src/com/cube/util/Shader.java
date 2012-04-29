package com.cube.util;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

import org.lwjgl.opengl.GL20;

import com.cube.core.Graphics;
import com.cube.core.Resources;

public class Shader {
	
	//Handles on the shader program and shaders
	private int shaderProgram;
	private int vertexShader;
	private int fragmentShader;
	//Location of the shader source codes
	private String vertexShaderSourceFile;
	private String fragmentShaderSourceFile;
	//Strings to hold the shader code that will be compiled
	private StringBuilder vertexShaderSource;
	private StringBuilder fragmentShaderSource;
		
	private Shader() {		
		//Initialize the shader program and vertex and fragment shaders
		shaderProgram = glCreateProgram();
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		//Initialize the string builders that will hold the shader code
		vertexShaderSource = new StringBuilder();
		fragmentShaderSource = new StringBuilder();
	}
	
	public Shader(String vertexShaderFile, String fragmentShaderFile) {
		this();
		//Set the source files to the provided files
		setVertexShaderSourceFile(vertexShaderFile);
		setFragmentShaderSourceFile(fragmentShaderFile);
		//Load the shader source code
		loadShaders();
		//Link the shaders
		linkShaders();
	}
	
	public void bind() {
		glUseProgram(shaderProgram);

		int location0 = GL20.glGetUniformLocation(shaderProgram, "cameraPosX");
		int location1 = GL20.glGetUniformLocation(shaderProgram, "cameraPosY");
		int location2 = GL20.glGetUniformLocation(shaderProgram, "cameraPosZ");

		GL20.glUniform1f(location0, Graphics.camera.getPosition(0) + Resources.map.width/2);
		GL20.glUniform1f(location1, Graphics.camera.getPosition(1));
		GL20.glUniform1f(location2, Graphics.camera.getPosition(2) + Resources.map.height/2);

	}
	
	public void unbind() {
		//Sets the OpenGL pipeline to use the default pipeline
		//rather than any compiled shader
		glUseProgram(0);
	}
	
	public void linkShaders() {
		//Attach the shader to the shader program
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		//Link the shader program (I have no idea what this does).
        glLinkProgram(shaderProgram);
        //Validate the shader program
        glValidateProgram(shaderProgram);
	}
	
	public void loadShaders() {
		loadVertexShader();
		loadFragmentShader();
	}
	
	public void loadVertexShader() {
		try {
			//Prepare the file for reading
			BufferedReader reader = new BufferedReader(new FileReader(ShaderManager.BASE_DIR + vertexShaderSourceFile));
            String line;
            //Read the file, line by line
            while ((line = reader.readLine()) != null) {
                vertexShaderSource.append(line).append('\n');
            }
		} catch (IOException e) {
	    	FileLogger.logger.log(Level.SEVERE, "Vertex shader failed to load properly!");
		}
		//Attach the shader source to the shader
        glShaderSource(vertexShader, vertexShaderSource);
        //Compile the shader
        glCompileShader(vertexShader);
        //Check to make sure that compile was successful
        if (glGetShader(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
	    	FileLogger.logger.log(Level.SEVERE, "Vertex shader failed to compile!");
        }
	}
	
	public void loadFragmentShader() {
		try {
			//Prepare the file for reading
			BufferedReader reader = new BufferedReader(new FileReader(ShaderManager.BASE_DIR + fragmentShaderSourceFile));
            String line;
            //Read the file, line by line
            while ((line = reader.readLine()) != null) {
                fragmentShaderSource.append(line).append('\n');
            }
		} catch (IOException e) {
	    	FileLogger.logger.log(Level.SEVERE, "Fragment shader failed to load properly!");
		}
		//Attach the shader source to the shader
        glShaderSource(fragmentShader, fragmentShaderSource);
        //Compile the shader
        glCompileShader(fragmentShader);
        //Check to make sure that compile was successful
        if (glGetShader(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
	    	FileLogger.logger.log(Level.SEVERE, "Fragment shader failed to compile!");
        }
	}
	
	public String getVertexShaderSourceFile() {
		return vertexShaderSourceFile;
	}
	
	public int getShaderProgram() {
		return shaderProgram;
	}
	
	public void setVertexShaderSourceFile(String source) {
		vertexShaderSourceFile = source;
	}
	
	public String getFragmentShaderSourceFile() {
		return fragmentShaderSourceFile;
	}
	
	public void setFragmentShaderSourceFile(String source) {
		fragmentShaderSourceFile = source;
	}
}
