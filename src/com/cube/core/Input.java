package com.cube.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.cube.gui.Menu;
import com.cube.gui.Window;
import com.cube.states.TravelState;
import com.cube.util.FileLogger;
import com.cube.util.Utilities;


public class Input {
	
	private static float panSpeed 		= .1f;
	private static float rotateSpeed 	= .1f;
	private static float scrollSpeed 	= .1f;
	private static int deltaX = 0;
	private static int deltaY = 0;
	
	private	static	IntBuffer	viewport;
	private static	FloatBuffer	modelview;
	private static	FloatBuffer projection;
	private static	FloatBuffer	positionNear;


	public static void initialize() {
		
		viewport	= BufferUtils.createIntBuffer(16);
		modelview	= BufferUtils.createFloatBuffer(16);
		projection	= BufferUtils.createFloatBuffer(16);
		
		positionNear	= BufferUtils.createFloatBuffer(3);
		
		FileLogger.logger.log(Level.INFO, "Input initialized");
	}

	/*
	 * General method to poll the input from the user. If menu windows are currently displayed,
	 * and the menu can take context away from the main game (e.g. 'Pause') then polling should
	 * continue according to that window's specifications. Otherwise, check to see if any non-
	 * context-stealing menus (e.g. 'Build Menu') have been interacted with, and continue to
	 * poll for interaction with the main game.
	 */
	public static void poll() {
		
		for (Window w : Menu.windows) {
			
			if (w.stealContext) {
				w.poll();
				return;
			}
			else if (w.show) {
				w.checkGuiClick();
			}
		}
	
		pollKeyboard();
		pollMouse();
	}

	private static void pollMouse() {
		
		int x = Mouse.getX();
		int y = (Engine.HEIGHT - 1) - Mouse.getY();

		deltaX = x - deltaX;
		deltaY = y - deltaY;

		//Handle Buttons
		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				switch (Mouse.getEventButton()) {
					case 0:
						processLeftClick();
						break;
					case 1:
						processRightClick();
						break;
				}
			}
		}		
		
		processScrollWheel();

		//Handle Position
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			Graphics.camera.changeThetaY(-rotateSpeed * deltaX);
			Graphics.camera.changeThetaX(-rotateSpeed * deltaY);
		}
		else {
			if (x <= 4) 						
				Graphics.camera.panRight(panSpeed);
			if (x >= Engine.WIDTH - 5) 
				Graphics.camera.panRight(-panSpeed);
			if (y <= 4) 
				Graphics.camera.panForward(-panSpeed);
			if (y >= Engine.HEIGHT - 5) 
				Graphics.camera.panForward(panSpeed);
		}
		
		deltaX = x;
		deltaY = y;
	}

	private static void processLeftClick() {
		// If the user has selected a building to build from the Building menu,
		// handle the special processing.
		if (Game.buildingToBeBuilt != null) {
			float[] pos = new float[3];
			pos = getMousePosition(Mouse.getX(), Mouse.getY());
			Building.layFoundation(pos, Game.buildingToBeBuilt);
		}
		// Undergo default left click processing
		else {
			Graphics.colorPickingMode();
			Game.selectedEntity = processPick(Mouse.getX(), Mouse.getY());
		}
	}
	
	private static void processRightClick() {
		// If the user has an entity selected, handle the special processing to
		// redirect that entity.
		if (Game.selectedEntity != null) {
			Graphics.colorPickingMode();
			Entity target = processPick(Mouse.getX(), Mouse.getY());
			// If the user right clicked on an entity, attempt to redirect the selected
			// entity to the target
			if (target != null) {
				Game.selectedEntity.testInteraction(target);
			}
			// If the user did not right click on an entity, move the selected entity
			// to the position of the right click
			else {
				float[] pos = new float[3];
				Graphics.update();
				pos = getMousePosition(Mouse.getX(), Mouse.getY());
				pos[1] = 0;
				Game.selectedEntity.previousState = Game.selectedEntity.currentState;
				Game.selectedEntity.setDestination(pos);
				Game.selectedEntity.userControlled = true;
				Game.selectedEntity.changeState( TravelState.getState() );
			}
		}
	}
	
	private static void processScrollWheel() {
		Graphics.camera.modifyRadius(-Mouse.getDWheel()/120 * scrollSpeed); //1 'unit' of scrolling is equal to 120
	}
	
	private static void pollKeyboard() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				switch (Keyboard.getEventKey()) {
					case Keyboard.KEY_ESCAPE:
						Game.buildingToBeBuilt = null;
						Menu.windows.get(Menu.CONSOLE).closeWindow();
						Menu.windows.get(Menu.BUILDING).closeWindow();
						Menu.pause();
						break;
					case Keyboard.KEY_0:
						Menu.windows.get(Menu.CONSOLE).show = !Menu.windows.get(Menu.CONSOLE).show;
						break;
					case Keyboard.KEY_P:
						Resources.map.massSmooth();
						break;
				}
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			panSpeed = .5f;
		else
			panSpeed = .1f;
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			Graphics.camera.panForward(-panSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			Graphics.camera.panForward(panSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			Graphics.camera.panRight(panSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			Graphics.camera.panRight(-panSpeed);
		
		// ----- FOR DEBUGGING PURPOSES ---- //
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			Resources.lights.get(0).moveSun(1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			Resources.lights.get(0).moveSun(-1);
		}
	}
	
	public static Entity processPick(int pickX, int pickY) {
		
		float id[] = new float[3];

		ByteBuffer pixels = ByteBuffer.allocateDirect(12).order(ByteOrder.nativeOrder());
		
		GL11.glReadPixels(pickX, pickY, 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);
		
		id[0] = pixels.get();
		id[1] = pixels.get();
		id[2] = pixels.get();

		if (id[0] < 0) id[0] += 256;
		if (id[1] < 0) id[1] += 256;
		if (id[2] < 0) id[2] += 256;
		
		Entity tempEnt = Resources.pickingHashMap.get(Resources.colorIDToStringKey(id));
		
		return tempEnt;
	}
	
	public static float[] getMousePosition(int mouseX, int mouseY) {
	
		ByteBuffer pixels = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
		float[] pos = new float[3];

		
		//--------------------- Save the view matrices ------------------//
		GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, modelview );
		GL11.glGetFloat( GL11.GL_PROJECTION_MATRIX, projection );
		GL11.glGetInteger( GL11.GL_VIEWPORT, viewport );
		//---------------------------------------------------------------//
		
		
		GL11.glReadPixels(mouseX, mouseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, pixels);
		GLU.gluUnProject((float)mouseX, (float)mouseY, pixels.getFloat(0), modelview, projection, viewport, positionNear);

		
		pos[0] = positionNear.get(0);
		pos[1] = positionNear.get(1);
		pos[2] = positionNear.get(2);

		return pos;
	}

	public static boolean isMouseButtonUp() {
		boolean ret = Mouse.getEventButtonState() && Mouse.getEventButton()==0 && !Mouse.isButtonDown(0);
		return ret;
	}

}
