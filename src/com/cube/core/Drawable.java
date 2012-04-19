package com.cube.core;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

import com.cube.util.OBJParser;
import com.cube.util.PolyFace;
import com.cube.util.TextureVert;
import com.cube.util.Vertex;
import com.cube.util.GeometryGroup;

public abstract class Drawable {
	
	public ArrayList<Vertex> vertexArray;
	public ArrayList<Vertex> vertexNormalArray;
	public ArrayList<TextureVert> textureArray;
	public ArrayList<GeometryGroup> geoGroups;
	
	public abstract void draw();
	
	public Drawable() {
		vertexArray = new ArrayList<Vertex>();
		vertexNormalArray = new ArrayList<Vertex>();
		textureArray = new ArrayList<TextureVert>();
		geoGroups = new ArrayList<GeometryGroup>();
	}
	
	public void loadObj(String file) {
		try {
			
			OBJParser parser = new OBJParser(file);
			vertexArray = parser.v;
			vertexNormalArray = parser.vn;
			textureArray = parser.t;
			for(GeometryGroup gg : parser.ggs) {
				geoGroups.add(gg);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void drawOBJ() {
		
		
		boolean proc = false;
		boolean texEnable = false;

		for(GeometryGroup gg : geoGroups) {
			//ArrayList<PolyFace> gg.f = gg.f;
			proc = gg.status == GeometryGroup.PROCEDURAL_ANIMATED;
			texEnable = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
			if(gg.status == GeometryGroup.STATIC || gg.status == GeometryGroup.GAME_ANIMATED) {
				drawGroup(gg);
			}else if(proc) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glPushMatrix();
					//GL11.glLoadIdentity();
					//GL11.glColor3f(0f, 0f, 1f);
					//GL11.glTranslatef(3f, 0f, 0f);
					drawGroup(gg);
			}
				
				if(proc && texEnable) {
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}
				if(proc) {
					GL11.glPopMatrix();
				}
		}
	}
	
	public void drawGroup(GeometryGroup gg)
	{
		int vertIndex;
		int texIndex;
		int normIndex;
		
		if (gg.f.get(0).vertexIndices.size() == 3)
			GL11.glBegin(GL11.GL_TRIANGLES);
		else if (gg.f.get(0).vertexIndices.size() == 4)
			GL11.glBegin(GL11.GL_QUADS);
		else
			GL11.glBegin(GL11.GL_POLYGON);
		
		
		for (int i = 0; i < gg.f.size(); i++) {

			for (int j = 0; j < gg.f.get(i).vertexIndices.size(); j++) {
				
				if (vertexNormalArray.size() > 0) {
					normIndex = gg.f.get(i).normalIndices.get(j);

					GL11.glNormal3f(vertexNormalArray.get(normIndex).x, 
								    vertexNormalArray.get(normIndex).y, 
								    vertexNormalArray.get(normIndex).z);
					
				}
				if (textureArray.size() > 0) {
					texIndex = gg.f.get(i).textureIndices.get(j);

					GL11.glTexCoord2f(textureArray.get(texIndex).x,
								      textureArray.get(texIndex).y);
				}
				if (vertexArray.size() > 0) {

					vertIndex = gg.f.get(i).vertexIndices.get(j);
					
					GL11.glVertex3f(vertexArray.get(vertIndex).x, 
								    vertexArray.get(vertIndex).y, 
								    vertexArray.get(vertIndex).z);
					
				}
				
				
			}

		}
		GL11.glEnd();
	}
}
