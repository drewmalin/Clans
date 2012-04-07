package com.cube.core;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public abstract class Drawable {
	
	public ArrayList<Vertex> vertexArray;
	public ArrayList<Vertex> vertexNormalArray;
	public ArrayList<Texture> textureArray;
	public ArrayList<PolyFace> polyfaceArray;
	
	public abstract void draw();
	
	public Drawable() {
		vertexArray = new ArrayList<Vertex>();
		vertexNormalArray = new ArrayList<Vertex>();
		textureArray = new ArrayList<Texture>();
		polyfaceArray = new ArrayList<PolyFace>();
	}
	
	public void loadObj(String file) {
		try {
			
			OBJParser parser = new OBJParser(file);
			vertexArray = parser.v;
			vertexNormalArray = parser.vn;
			textureArray = parser.t;
			polyfaceArray = parser.f;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void drawOBJ() {
		int vertIndex;
		int texIndex;
		int normIndex;


		if (polyfaceArray.get(0).vertexIndices.size() == 3)
			GL11.glBegin(GL11.GL_TRIANGLES);
		else if (polyfaceArray.get(0).vertexIndices.size() == 4)
			GL11.glBegin(GL11.GL_QUADS);
		else
			GL11.glBegin(GL11.GL_POLYGON);
		
		
		for (int i = 0; i < polyfaceArray.size(); i++) {

			for (int j = 0; j < polyfaceArray.get(i).vertexIndices.size(); j++) {
				
				if (vertexNormalArray.size() > 0) {
					normIndex = polyfaceArray.get(i).normalIndices.get(j);

					GL11.glNormal3f(vertexNormalArray.get(normIndex).x, 
								    vertexNormalArray.get(normIndex).y, 
								    vertexNormalArray.get(normIndex).z);
					
				}
				if (vertexArray.size() > 0) {

					vertIndex = polyfaceArray.get(i).vertexIndices.get(j);
					
					GL11.glVertex3f(vertexArray.get(vertIndex).x, 
								    vertexArray.get(vertIndex).y, 
								    vertexArray.get(vertIndex).z);
				}
				if (textureArray.size() > 0) {
					texIndex = polyfaceArray.get(i).textureIndices.get(j);

					GL11.glTexCoord2f(textureArray.get(texIndex).x,
								      textureArray.get(texIndex).y);
				}
				
			}

		}
		GL11.glEnd();
	}
}
