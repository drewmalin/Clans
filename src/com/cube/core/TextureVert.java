package com.cube.core;

public class TextureVert {
	public float x;
	public float y;
	
	public TextureVert(String data) {
		data = data.substring(3);
		String[] tokens = data.split("\\s");
		
		x = Float.valueOf(tokens[0]);
		y = 1 - Float.valueOf(tokens[1]);
	}
}
