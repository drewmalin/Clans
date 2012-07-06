package com.cube.core;

import java.nio.FloatBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import com.cube.core.Engine;
import com.cube.gui.Menu;
import com.cube.util.FileLogger;
import com.cube.util.ShaderManager;
import com.cube.util.Utilities;


public class Graphics {
	
	public static Camera camera;
	public static ShaderManager shaderManager;
	public static boolean colorPicking;

	private static FloatBuffer matSpecular;
	private static FloatBuffer lModelAmbient;
	public static int frameCount;
	
	public static void initialize() {
		
		frameCount = 0;
		
		setupCamera();
		createDisplay();
		setupLighting();
		setupBlend();
		setupShaderManager();

		FileLogger.logger.log(Level.INFO, "Graphics initialized");
	}

	private static void setupBlend() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private static void setupCamera() {
		camera = new Camera();
		camera.setUp(0f, 1f, 0f);
		camera.setTarget(0f, 0f, 0f); 
		camera.setRadius(30f);
		camera.setThetaX(46.9f);
		camera.setThetaY(-22.3f);
	}
	
	private static void setupLighting() {
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);											// enables lighting
		GL11.glShadeModel(GL11.GL_SMOOTH);

		matSpecular = BufferUtils.createFloatBuffer(4);
		matSpecular.put(0.7f).put(0.7f).put(0.7f).put(1.0f).flip();
		
		lModelAmbient = BufferUtils.createFloatBuffer(4);
		lModelAmbient.put(0.3f).put(0.3f).put(0.3f).put(1.0f).flip();
		
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, matSpecular);				// sets specular material color
		GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 5.0f);					// sets shininess
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, lModelAmbient);				// global ambient light 

		GL11.glEnable(GL11.GL_COLOR_MATERIAL);										// enables opengl to use glColor3f to define material color
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);			// tell opengl glColor3f effects the ambient and diffuse properties of material		
			
		/*
		Light sun = new Light("SUN");
		sun.setPosition(0, 200, 0);
		sun.setColor(1f, 1f, 1f, 1f);
		sun.create(GL11.GL_LIGHT0);
		sun.loadOBJ("res/obj/berryBushSans.obj");
		Resources.lights.add(sun);
		*/
	}

	private static void setupShaderManager() {
		shaderManager = new ShaderManager();
		shaderManager.initialize();
	}

	private static void createDisplay() {
		
		//---------- Will be moved for loading purposes later -----------------//
		try {
			Display.setDisplayMode(new DisplayMode( Engine.WIDTH, Engine.HEIGHT));
			Display.setTitle(Engine.title);
			Display.setVSyncEnabled(Engine.vSync);
			if (Engine.AA) {
				PixelFormat format = new PixelFormat(32, 0, 24, 8, Engine.MSAA);
				Pbuffer pb = new Pbuffer(Engine.WIDTH, Engine.HEIGHT, format, null);
				pb.makeCurrent();
				Display.create(format);
			}
			else {
				Display.create();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FileLogger.logger.log(Level.SEVERE, e.getMessage());
		}
		//---------------------------------------------------------------------//
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GLU.gluPerspective(
			Engine.frust, 
			(float)Engine.WIDTH/(float)Engine.HEIGHT, 
			Engine.zNear, Engine.zFar
		);
		
		GLU.gluLookAt(
			camera.getPosition(0),	camera.getPosition(1), 	camera.getPosition(2),
			camera.getTarget(0),	camera.getTarget(1), 	camera.getTarget(2),
			camera.getUp(0),		camera.getUp(1), 		camera.getUp(2)
		);
			
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}

	/* 
	 * The general point of entry for the visual rendering of the scene. Recreates the viewing
	 * frustum using the current position of the camera, performs OpenGL housekeeping, and 
	 * proceeds to draw the various entities, lights, and menus of the scene.
	 */
	public static void update() {
		
		updateCamera();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
				
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			drawLights();
			drawClans();
			drawObjects();
			updatePhysics();
			Resources.map.draw();
			drawSelection();
			drawGUI();
		GL11.glPopMatrix();
		
		Display.update();
		Display.sync(Engine.framerate);
		
		frameCount++;
		
	}
	
	/*
	 * The non-visual rendering method meant for use in selecting objects with the mouse cursor.
	 * Each entity capable of being selected is drawn (but Display is never updated, so the buffer
	 * they are drawn to is never printed to the screen) using a specific color value. This value
	 * uniquely identifies the object, and can be read directly from the pixel the mouse cursor
	 * selects.
	 */
	public static void colorPickingMode() {
		
		colorPicking = true;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			drawClans();
			drawObjects();
		GL11.glPopMatrix();
		
		colorPicking = false;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	private static void updatePhysics() {
		if (!Menu.windows.get(Menu.PAUSE).stealContext) {
			Physics.drawEffects();
		}
	}
	
	private static void drawClans() {
		for (Clan c : Resources.clans) {
			c.draw();
		}
	}
	
	private static void drawObjects() {
		for (Entity e : Resources.entities) {
			e.draw();
		}
	}
	private static void drawLights() {
		for (Light light : Resources.lights)
			light.draw();
	}
	
	/*
	 * Since we move the camera about a static scene instead of moving the world around a 
	 * static camera, we need to update the viewing frustum according to the current position
	 * of the camera.
	 */
	private static void updateCamera() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		camera.updatePosition();

		GLU.gluPerspective(
				Engine.frust, 
				(float)Engine.WIDTH/(float)Engine.HEIGHT, 
				Engine.zNear, Engine.zFar
			);
		
		GLU.gluLookAt(
				camera.getPosition(0),	camera.getPosition(1), 	camera.getPosition(2),
				camera.getTarget(0),	camera.getTarget(1), 	camera.getTarget(2),
				camera.getUp(0),		camera.getUp(1), 		camera.getUp(2)
			);

	}
	
	/*
	 * Method to switch OpenGL back into 3D rendering mode. This is meant for use after all necessary
	 * menus have been drawn to the screen (which require a different viewing mode to see).
	 */
	public static void enable3D() {
		GL11.glDepthMask(true);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GLU.gluPerspective(
							Engine.frust, 
							(float)Engine.WIDTH/(float)Engine.HEIGHT, 
							Engine.zNear, Engine.zFar);
		GLU.gluLookAt(
						camera.getPosition(0),	camera.getPosition(1), 	camera.getPosition(2),
						camera.getTarget(0),	camera.getTarget(1), 	camera.getTarget(2),
						camera.getUp(0),		camera.getUp(1), 		camera.getUp(2));
			
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

	}
	
	/* Function to disable 3D for drawing to the screen. This function is meant to be used prior
	 * to drawing a menu or GUI.
	 */
	public static void disable3D() {
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public static void drawGUI() {
		disable3D();
		GL11.glDisable(GL11.GL_LIGHTING);											
		GL11.glMatrixMode(GL11.GL_PROJECTION);

		GL11.glLoadIdentity();
		GL11.glOrtho(0, Engine.WIDTH, Engine.HEIGHT, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glPushMatrix();	
		Menu.draw();	
		Menu.update();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_LIGHTING);											
		enable3D();
	}
	
	public static void drawCube() {
		GL11.glBegin(GL11.GL_QUADS);
			// Bottom
			GL11.glVertex3f(0f, 0f, 0f);
			GL11.glVertex3f(1f, 0f, 0f);
			GL11.glVertex3f(1f, 0f, -1f);
			GL11.glVertex3f(0f, 0f, -1f);
			// Top
			GL11.glVertex3f(0f, 1f, 0f);
			GL11.glVertex3f(1f, 1f, 0f);
			GL11.glVertex3f(1f, 1f, -1f);
			GL11.glVertex3f(0f, 1f, -1f);
			// Front
			GL11.glVertex3f(0f, 0f, 0f);
			GL11.glVertex3f(1f, 0f, 0f);
			GL11.glVertex3f(1f, 1f, 0f);
			GL11.glVertex3f(0f, 1f, 0f);
			// Back
			GL11.glVertex3f(0f, 0f, -1f);
			GL11.glVertex3f(1f, 0f, -1f);
			GL11.glVertex3f(1f, 1f, -1f);
			GL11.glVertex3f(0f, 1f, -1f);
			// Right
			GL11.glVertex3f(1f, 0f, 0f);
			GL11.glVertex3f(1f, 0f, -1f);
			GL11.glVertex3f(1f, 1f, -1f);
			GL11.glVertex3f(1f, 1f, 0f);
			// Left
			GL11.glVertex3f(0f, 0f, 0f);
			GL11.glVertex3f(0f, 0f, -1f);
			GL11.glVertex3f(0f, 1f, -1f);
			GL11.glVertex3f(0f, 1f, 0f);
		GL11.glEnd();
	}
	
	/*
	 * Method to draw the foundation of the currently selected building. The selection is meant to move
	 * as the mouse cursor moves, and is currently very temporary. This may be expanded in the future to
	 * apply to other world objects.
	 */
	public static void drawSelection() {

		if (Game.buildingToBeBuilt != null && !Game.buildingToBeBuilt.complete) {
			int width = Game.buildingToBeBuilt.width;
			int height = Game.buildingToBeBuilt.height;

			float[] point = new float[3];
			
			point = Input.getMousePosition(Mouse.getX(), Mouse.getY());

			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glColor3f(1f, 0f, 0f);
			
			GL11.glBegin(GL11.GL_POINTS);
			
			for (int j = (int)point[2] - (height/2); j < (int)point[2] + (height/2); j++) {
				for (int i = (int)point[0] - (width/2); i < (int)point[0] + (width/2); i++) {
					GL11.glVertex3f(i, Resources.map.getHeight(i, j) + 0.5f, j);
				}
			}
			
			GL11.glEnd();
			GL11.glPopMatrix();
		}
	}
}
