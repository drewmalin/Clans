package com.cube.core;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

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

	public int vboVertexHandle;
	public int vboNormalHandle;
	public int vboTextureHandle;
	
	int faceCount;
	
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

			// done in Resources -> setupVBO();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setupVBO() {
		int vertIndex;
		int normIndex;
		int texIndex;
		
		vboVertexHandle = GL15.glGenBuffers();
		vboNormalHandle = GL15.glGenBuffers();
		vboTextureHandle = GL15.glGenBuffers();
		
		for (GeometryGroup geoGroup : geoGroups) {
			faceCount += geoGroup.f.size();
		}

		FloatBuffer vertices = BufferUtils.createFloatBuffer(faceCount * 9); //3 verts per triangle, 3 floats per vert
		FloatBuffer normals = BufferUtils.createFloatBuffer(faceCount * 9); //3 verts per triangle, 3 floats per vert
		FloatBuffer textures = BufferUtils.createFloatBuffer(faceCount * 6); //3 verts per triangle, 2 floats per vert
		
		for(GeometryGroup gg : geoGroups) {
			
			for (int i = 0; i < gg.f.size(); i++) {
				for (int j = 0; j < gg.f.get(i).vertexIndices.size(); j++) {
					
					if (vertexNormalArray.size() > 0) {
						normIndex = gg.f.get(i).normalIndices.get(j);
						
						normals.put(new float[] {vertexNormalArray.get(normIndex).x, 
									    	     vertexNormalArray.get(normIndex).y, 
									    	     vertexNormalArray.get(normIndex).z});
					}
					
					if (textureArray.size() > 0) {
						texIndex = gg.f.get(i).textureIndices.get(j);

						textures.put(new float[] {textureArray.get(texIndex).x,
									      		  textureArray.get(texIndex).y});
					}
					
					if (vertexArray.size() > 0) {
						vertIndex = gg.f.get(i).vertexIndices.get(j);
						
						vertices.put(new float[] {vertexArray.get(vertIndex).x, 
									    		  vertexArray.get(vertIndex).y, 
									    		  vertexArray.get(vertIndex).z});
					}	
				}
			}	
		}

		vertices.flip();
		normals.flip();
		textures.flip();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertexHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboNormalHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normals, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTextureHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textures, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		vertices = null;
		normals = null;
		textures = null;
	}
	
	public void drawOBJ() {
		
		if (Engine.VBO) {

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertexHandle);
			GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboNormalHandle);
			GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0L);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTextureHandle);
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
			
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

			GL11.glColor3f(1, 1, 1);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, faceCount*9);
			
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		else {
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
