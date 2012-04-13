package com.cube.core;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public abstract class Drawable {
	
	public ArrayList<Vertex> vertexArray;
	public ArrayList<Vertex> vertexNormalArray;
	public ArrayList<TextureVert> textureArray;
	public ArrayList<PolyFace> polyfaceArray;
	
	public abstract void draw();
	
	public Drawable() {
		vertexArray = new ArrayList<Vertex>();
		vertexNormalArray = new ArrayList<Vertex>();
		textureArray = new ArrayList<TextureVert>();
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
				if (textureArray.size() > 0) {
					texIndex = polyfaceArray.get(i).textureIndices.get(j);

					GL11.glTexCoord2f(textureArray.get(texIndex).x,
								      textureArray.get(texIndex).y);
				}
				if (vertexArray.size() > 0) {

					vertIndex = polyfaceArray.get(i).vertexIndices.get(j);
					
					GL11.glVertex3f(vertexArray.get(vertIndex).x, 
								    vertexArray.get(vertIndex).y, 
								    vertexArray.get(vertIndex).z);
					
				}
				
				
			}

		}
		GL11.glEnd();
		/*
		GL11.glBegin(GL11.GL_LINES);
		for (int i = 0; i < polyfaceArray.size(); i++) {
			for (int j = 0; j < polyfaceArray.get(i).vertexIndices.size(); j++) {
				
				if (vertexNormalArray.size() > 0 && vertexArray.size() > 0) {

				vertIndex = polyfaceArray.get(i).vertexIndices.get(j);
				normIndex = polyfaceArray.get(i).normalIndices.get(j);
				
				GL11.glVertex3f(vertexNormalArray.get(vertIndex).x, 
						vertexNormalArray.get(vertIndex).y, 
						vertexNormalArray.get(vertIndex).z);
				
				GL11.glVertex3f(vertexNormalArray.get(vertIndex).x + vertexNormalArray.get(normIndex).x, 
						vertexNormalArray.get(vertIndex).y + vertexNormalArray.get(normIndex).y-1, 
						vertexNormalArray.get(vertIndex).z + vertexNormalArray.get(normIndex).z-1);
				}
			}
		}
		GL11.glEnd();
		*/
	}
}
