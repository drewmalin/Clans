package com.cube.core;

import java.nio.FloatBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import com.cube.core.Engine;
import com.cube.gui.Menu;
import com.cube.util.FileLogger;


public class Graphics {
	
	public static Camera camera;
	private static FloatBuffer matSpecular;
	private static FloatBuffer lModelAmbient;
	
	public static void initialize() {
		
		setupCamera();
		createDisplay();
		setupLighting();
		setupBlend();
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
		camera.setRadius(15.5f);
		camera.setThetaX(46.9f);
		camera.setThetaY(-22.3f);
		camera.updatePosition();
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
			
		Light sun = new Light("SUN");
		sun.setPosition(-50, 50, -50);
		sun.setColor(1f, 1f, 1f, 1f);
		sun.create(GL11.GL_LIGHT0);
		Resources.lights.add(sun);

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
			drawGUI();
		GL11.glPopMatrix();
		
		Display.update();
		Display.sync(Engine.framerate);
		
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
}
