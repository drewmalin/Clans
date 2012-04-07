package com.cube.core;

import org.lwjgl.util.vector.Vector3f;

public class Vertex {
	public float x;
	public float y;
	public float z;
	
	public Vertex(String data) {
		data = data.substring(2);
		if(data.charAt(0) == ' ')
		{
			data = data.substring(1);
		}
		String[] tokens = data.split("\\s");
		
		x = Float.valueOf(tokens[0]);
		y = Float.valueOf(tokens[1]);
		z = Float.valueOf(tokens[2]);
	}

	public Vertex(float[] array) {
		assert array.length == 3;
		x = array[0];
		y = array[1];
		z = array[2];
	}

	public Vertex(Vector3f sum) {
		x = sum.x;
		y = sum.y;
		z = sum.z;
	}
}
