package com.cube.core;

public class Texture {
	public float x;
	public float y;
	
	public Texture(String data) {
		data = data.substring(3);
		String[] tokens = data.split("\\s");
		
		x = Float.valueOf(tokens[0]);
		y = Float.valueOf(tokens[1]);
	}
}
