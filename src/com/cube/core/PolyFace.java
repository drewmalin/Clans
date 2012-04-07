package com.cube.core;

import java.util.ArrayList;

public class PolyFace {
	public ArrayList<Integer> vertexIndices;
	public ArrayList<Integer> textureIndices;
	public ArrayList<Integer> normalIndices;
	public float[] normal;
	
	public PolyFace() {
		vertexIndices = new ArrayList<Integer>();
		textureIndices = new ArrayList<Integer>();
		normalIndices = new ArrayList<Integer>();
		normal = new float[3];
	}
	
	public void calcNormals() {
		normal[0] = 1;
		normal[1] = 1;
		normal[2] = 1;
	}

	public void addVertex(String data) {

		data = data.substring(2);
		String[] tokens = data.split("\\s");
		
		for (String token : tokens) {
			vertexIndices.add(Integer.valueOf(token) - 1);
		}
		
	}

	public void addVertexAndNormal(String data) {
		data = data.substring(2);
		String[] tokens = data.split("\\s");

		for (String token : tokens) {
			String[] subTokens = token.split("/");
			vertexIndices.add(Integer.valueOf(subTokens[0]) - 1);
			normalIndices.add(Integer.valueOf(subTokens[2]) - 1);
		}
		
	}

	public void addVertexAndTexture(String data) {
		data = data.substring(2);
		String[] tokens = data.split("\\s");

		for (String token : tokens) {
			String[] subTokens = token.split("/");
			vertexIndices.add(Integer.valueOf(subTokens[0]) - 1);
			textureIndices.add(Integer.valueOf(subTokens[1]) - 1);
		}
	}

	public void addVertexAndTextureAndNormal(String data) {
		data = data.substring(2);
		String[] tokens = data.split("\\s");

		for (String token : tokens) {
			String[] subTokens = token.split("/");
			vertexIndices.add(Integer.valueOf(subTokens[0]) - 1);
			textureIndices.add(Integer.valueOf(subTokens[1]) - 1);
			normalIndices.add(Integer.valueOf(subTokens[2]) - 1);
		}
	}
}
