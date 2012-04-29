package com.cube.core;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.cube.util.BMPparser;
import com.cube.util.Quad;
import com.cube.util.Vertex;
//import com.cube.util.ShaderManager.ShaderType;

public class Map extends Drawable {

	private int size;
	public float scale;
	public String file;
	public int[] colorID;
	
	
	public static boolean showNormals = false;
	
	public float[][] heightMap;
	public Quad[][][] planeEq;
	public String filename;
	public int pixels[][][], width, height;
	public Vertex vertexNormals[][];
	public float heightModifier = .1f;
	static FloatBuffer lightPosition;
	
	
	public Map(int _size) {
		size = _size;
		colorID = new int[3];		
		scale = 1f;
	}
	
	@Override
	public void draw() {
		//drawGrid();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//Graphics.shaderManager.bindShader(ShaderType.HEMISPHERE);
		drawMap();
		//Graphics.shaderManager.unbindShader(ShaderType.HEMISPHERE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void initialize() {
		load(file);
	}
	
	public void load(String filename) {
		try {
			
			BMPparser parser = new BMPparser(filename);
			width = parser.width;
			height = parser.height;
			pixels = parser.pixels;

			calculateNormals();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void calculateNormals() {
		vertexNormals = new Vertex[width][height];
		planeEq = new Quad[width][height][2];

		calculatePlanarNormals();
		calculateVertexNormals();
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
	
	public void calculatePlanarNormals() {
		float a1, a2, b1, b2, c1, c2, d1, d2;
		
		Vector3f east;	//Relative to the top-left vector
		Vector3f south; //Relative to the top-left vector
		Vector3f north; //Relative to the bottom-right vector
		Vector3f west;  //Relative to the bottom-right vector
		Vector3f out;
		
		for (int j = 1; j < height; j++) {
			for (int i = 0; i < width - 1; i++) {
				
				//Top-left triangle
				out = new Vector3f(0f, 0f, 0f);
				east = new Vector3f(1f, (Math.abs(pixels[i+1][j][0]) * heightModifier)-(Math.abs(pixels[i][j][0]) * heightModifier), 0f);
				south = new Vector3f(0f, (Math.abs(pixels[i][j-1][0]) * heightModifier)-(Math.abs(pixels[i][j][0]) * heightModifier), -1f);
				
				Vector3f.cross(south, east, out);
				a1 = out.x;
				b1 = out.y;
				c1 = out.z;
				d1 = -(a1*i + b1*(Math.abs(pixels[i][j][0]) * heightModifier) + c1*j);
				
				planeEq[i][j][0] = new Quad(a1, b1, c1, d1);
				
				//Bottom-right triangle
				out = new Vector3f(0f, 0f, 0f);
				north = new Vector3f(0f, (Math.abs(pixels[i+1][j][0]) * heightModifier)-(Math.abs(pixels[i+1][j-1][0]) * heightModifier), 1f);
				west = new Vector3f(-1f, (Math.abs(pixels[i][j-1][0]) * heightModifier)-(Math.abs(pixels[i+1][j-1][0]) * heightModifier), 0f);
				
				Vector3f.cross(north, west, out);
				a2 = out.x;
				b2 = out.y;
				c2 = out.z;
				d2 = -(a2*(i+1) + b2*(Math.abs(pixels[i+1][j-1][0]) * heightModifier) + c2*(j-1));
				
				planeEq[i][j][1] = new Quad(a2, b2, c2, d2);
			}
		}
	}
	
	public float getHeight(float x, float z) {

		x += Resources.map.width/2;
		z += Resources.map.width/2;
		
		float val, tX, tZ, charHeight;
		int aX, aZ, bX, bZ;
		int choice;
		
		x *= scale;
		z *= scale;
		
		//Determine which triangle we need
		//(Bx - Ax) * (Cz - Az) - (Bz - Az) * (Cx - Ax)
		//A = top right, B = bottom left, C = current point
		
		aX = (int) (x + 1);
		aZ = (int) z;
		bX = (int) x; 
		bZ = (int) (z + 1);
		val = (bX - aX) * (z - aZ) - (bZ - aZ) * (x - aX);
		choice = val >= 0 ? 1 : 0; 							//0 is the top-left triangle, 1 is the bottom-right

		tX = x;
		tZ = z;
		
		
		if (tX < 0 || tZ < 1 || tX > width-1 || tZ > height-1) return 0; 
			
		charHeight = - (planeEq[(int)tX][(int)tZ][choice].d 
					 + (planeEq[(int)tX][(int)tZ][choice].a * x)
					 + (planeEq[(int)tX][(int)tZ][choice].c * z))
					 / planeEq[(int)tX][(int)tZ][choice].b;
		
		return Math.abs(charHeight * scale);
	}
	
	public void drawMap() {
		
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

}