package com.cube.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector3f;

import com.cube.util.BMPParser;
import com.cube.util.ShaderManager;
import com.cube.util.Utilities;
import com.cube.util.Vertex;

public class Map extends Drawable {
	
	private int size;
	public float scale;
	public String file;
	public int[] colorID;
	
	public int vboVertexHandle;
	public int vboNormalHandle;
	public int vboTextureHandle;
	
	public static boolean showNormals = false;
	
	public float[][] heightMap;
	public String filename;
	public int pixels[][][], width, height;
	public Vertex vertexNormals[][];
	public float heightModifier = .1f;
	static FloatBuffer lightPosition;
	
	//Get the size of primitive types in bytes
	int intSize 	= Integer.SIZE/8;
	int floatSize 	= Float.SIZE/8;
	int doubleSize 	= Double.SIZE/8;
	
	int vertexSize 	= 3*intSize;
	int normalSize 	= 3*floatSize;
	int colorSize 	= 3*floatSize;
	
	//Get the size of an element
	int elementSize = vertexSize+normalSize+colorSize;
	
	public Map(int _size) {
		size = _size;
		colorID = new int[3];		
		scale = 1f;
	}
	
	@Override
	public void draw() {
		//drawGrid();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//Graphics.shaderManager.bindShader(ShaderManager.HEMISPHERE);
		drawMap();
		//Graphics.shaderManager.unbindShader(ShaderManager.HEMISPHERE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void initialize() {
		load(file);
		
		if (Engine.VBO)
			setupVBO();
	}	
	
	public void load(String filename) {
		try {

			BMPParser parser = new BMPParser(filename);
			width = parser.width;
			height = parser.height;
			pixels = parser.pixels;
			calculateNormals();
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setupVBO() {
		vboVertexHandle = GL15.glGenBuffers();
		vboNormalHandle = GL15.glGenBuffers();
		vboTextureHandle = GL15.glGenBuffers();
		
		/* Why h*w*18 ? There are h*w tiles in the map (pixels in the heightmap),
		 * multiply by 2 triangles per tile, multiply by 3 corners per triangle, 
		 * and finally multiply by 3 floats per corner
		 * 
		 * For textures: h*w*12 : h*w faces, multiply by 2 triangles per tile, 3
		 * corners per triangle, 2 floats per corner
		 */
		FloatBuffer vertices = BufferUtils.createFloatBuffer(height*width*18);
		FloatBuffer normals = BufferUtils.createFloatBuffer(height*width*18);
		FloatBuffer textures = BufferUtils.createFloatBuffer(height*width*12);
		
		for (int j = 0; j < height-1; j++) {
			for (int i = 0; i < width-1; i++) {
				normals.put(new float[] {vertexNormals[i][j].x, vertexNormals[i][j].y, vertexNormals[i][j].z});
				vertices.put(new float[] {i, Math.abs(pixels[i][j][0] * heightModifier), j});
				textures.put(new float[] {0, 0});
				
				normals.put(new float[] {vertexNormals[i][j+1].x, vertexNormals[i][j+1].y, vertexNormals[i][j+1].z});
				vertices.put(new float[] {i, Math.abs(pixels[i][j+1][0] * heightModifier), j+1});
				textures.put(new float[] {0, 1});

				normals.put(new float[] {vertexNormals[i+1][j].x, vertexNormals[i+1][j].y, vertexNormals[i+1][j].z});
				vertices.put(new float[] {i+1, Math.abs(pixels[i+1][j][0] * heightModifier), j});
				textures.put(new float[] {1, 0});

				normals.put(new float[] {vertexNormals[i+1][j+1].x, vertexNormals[i+1][j+1].y, vertexNormals[i+1][j+1].z});
				vertices.put(new float[] {i+1, Math.abs(pixels[i+1][j+1][0] * heightModifier), j+1});
				textures.put(new float[] {1, 1});

				normals.put(new float[] {vertexNormals[i+1][j].x, vertexNormals[i+1][j].y, vertexNormals[i+1][j].z});
				vertices.put(new float[] {i+1, Math.abs(pixels[i+1][j][0] * heightModifier), j});
				textures.put(new float[] {1, 0});

				normals.put(new float[] {vertexNormals[i][j+1].x, vertexNormals[i][j+1].y, vertexNormals[i][j+1].z});
				vertices.put(new float[] {i, Math.abs(pixels[i][j+1][0] * heightModifier), j+1});
				textures.put(new float[] {0, 1});

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
	
	public void calculateNormals() {
		vertexNormals = new Vertex[width][height];
		calculateVertexNormals();
	}
	
	public String getColorIDAsString() {
		String ret = colorID[0] + " " + colorID[1] + " " + colorID[2];
		return ret;
	}
	
	public void calculateVertexNormals() {

		Vector3f north;
		Vector3f east;
		Vector3f south;
		Vector3f west;
		Vector3f sum;
		Vector3f out;
		
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {

				sum = new Vector3f(0f, 0f, 0f);
				out = new Vector3f(0f, 0f, 0f);
				north = east = south = west = null;

				if (j < (height-1)) { //Grab the north vector
					north = new Vector3f(0f, (pixels[i][j+1][0] * heightModifier)-(pixels[i][j][0] * heightModifier), 1f);
				}
				if (i < (width-1)) { //Grab the east vector
					east = new Vector3f(1f, (pixels[i+1][j][0] * heightModifier)-(pixels[i][j][0] * heightModifier), 0f);
				}
				if (j > 0) { //Grab the south vector
					south = new Vector3f(0f, (pixels[i][j-1][0] * heightModifier)-(pixels[i][j][0] * heightModifier), -1f);
				}
				if (i > 0) { //Grab the west vector
					west = new Vector3f(-1f, (pixels[i-1][j][0] * heightModifier)-(pixels[i][j][0] * heightModifier), 0f);
				}
				
				
				if (north != null && west != null) {
					Vector3f.cross(west, north, out);
					Vector3f.add(sum, out, sum);
					sum.normalise();
				}
				if (west != null && south != null) {
					Vector3f.cross(south, west, out);
					Vector3f.add(sum, out, sum);
					sum.normalise();
				}
				if (south != null && east != null) {
					Vector3f.cross(east, south, out);
					Vector3f.add(sum, out, sum);
					sum.normalise();
				}
				if (east != null && north != null) {
					Vector3f.cross(north, east, out);
					Vector3f.add(sum, out, sum);
					sum.normalise();
				}
				
				vertexNormals[i][j] = new Vertex(sum);
			}
		}
	}
	
	public float getHeight(float x, float z) {

		x += Resources.map.width/2;
		z += Resources.map.width/2;
		
		if (x < 0 || z < 0 || 
				x >= Resources.map.width - 1 || z >= Resources.map.height - 1) return 0;
		
		int baseX = (int)x;
		int baseZ = (int)z;
		float heightA = pixels[baseX][baseZ][0];
		float heightB = pixels[baseX+1][baseZ][0];
		float heightC = pixels[baseX][baseZ+1][0];
		float heightD = pixels[baseX+1][baseZ+1][0];
		float fHeight;
		float perX = x - baseX;
		float perZ = z - baseZ;
		
		if ((perX + perZ) < 1) {
			fHeight = heightA;
			fHeight += (heightB - heightA) * perX;
			fHeight += (heightC - heightA) * perZ;
		}
		else {
			fHeight = heightD;
			fHeight += (heightB - heightD) * (1f - perZ);
			fHeight += (heightC - heightD) * (1f - perX);
		}
		
		return heightModifier * scale * fHeight;

	}
	
	
	public void drawMap() {
		
		if (Engine.VBO) {
			Resources.textureLibrary.get("default").bind();

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertexHandle);
			GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboNormalHandle);
			GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTextureHandle);
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
			
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			
			GL11.glColor3f(0, 1, 0);
			GL11.glTranslatef((-width * scale)/2, 0, (-height * scale)/2);
			GL11.glScalef(scale, scale, scale);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, height*width*18);
			
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		else { 
			GL11.glPushMatrix();
			
			GL11.glTranslatef((-width * scale)/2, 0, (-height * scale)/2);
			GL11.glScalef(scale, scale, scale);
	
			GL11.glColor3f(0, 1, 0);
	
			for (int j = 1; j < height; j++) {
				GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
				for (int i = 0; i < width; i++) {
					GL11.glNormal3f(vertexNormals[i][j].x, vertexNormals[i][j].y, vertexNormals[i][j].z);
					GL11.glVertex3f(i, Math.abs(pixels[i][j][0] * heightModifier), j);
					GL11.glNormal3f(vertexNormals[i][j-1].x, vertexNormals[i][j-1].y, vertexNormals[i][j-1].z);
					GL11.glVertex3f(i, Math.abs(pixels[i][j-1][0] * heightModifier), j-1);
				}
				GL11.glEnd();
			}
	
			GL11.glPopMatrix();
		}
		
	}
	
	/*
	 * Method to draw a red grid along the y=0 plane. Setting the size parm (passed to the
	 * Map's constructor) determines how large the grid will be.
	 */
	private void drawGrid() {
		
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		
		GL11.glColor3f(.9f, 0f, 0f);

		GL11.glBegin(GL11.GL_LINES);
		
		for (int x = -size; x <= size; x++) {

			GL11.glVertex3d((double)x, 0d, -size);
			GL11.glVertex3d((double)x, 0d, size);

		}
		
		for (int z = -size; z <= size; z++) {
			
			GL11.glVertex3d(size, 0d, (double)z);
			GL11.glVertex3d(-size, 0d, (double)z);

		}
		
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}

	//***TEMPORARY METHOD***//
	public void massSmooth() {
		int x = (int) (width/2 + Game.playerClan.buildings.get(0).position.x);
		int z = (int) (height/2 + Game.playerClan.buildings.get(0).position.z);

		for (int j = z - (Game.playerClan.buildings.get(0).height/2); j < z + (Game.playerClan.buildings.get(0).height/2); j++) {
			for (int i = x - (Game.playerClan.buildings.get(0).width/2); i < x + (Game.playerClan.buildings.get(0).width/2); i++) {
				pixels[i][j][0] = (int) (Game.playerClan.buildings.get(0).averageHeight);
				vertexNormals[i][j].x = 0;
				vertexNormals[i][j].y = 1;
				vertexNormals[i][j].z = 0;
			}
		}
	}
}