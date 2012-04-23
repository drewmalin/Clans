package com.cube.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.cube.gui.Menu;
import com.cube.gui.Window;
import com.cube.util.FileLogger;


public class Input {
	
	private static float panSpeed 		= .1f;
	private static float rotateSpeed 	= .1f;
	private static float scrollSpeed 	= .1f;
	private static int deltaX = 0;
	private static int deltaY = 0;
	
	public static void initialize() {
		FileLogger.logger.log(Level.INFO, "Input initialized");
	}

	public static void poll() {
		for (Window w : Menu.windows) {
			if (w.stealContext) {
				w.poll();
				return;
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
					case 0:	//Left click
						System.out.println("left click");
						Graphics.colorPickingMode();
						processPick(Mouse.getX(), Mouse.getY());
						break;
					case 1: //Right click
						System.out.println("right click");
						break;
				}
			}
		}		
		
		//Handle Scrollwheel
		Graphics.camera.modifyRadius(-Mouse.getDWheel()/120 * scrollSpeed); //1 'unit' of scrolling is equal to 120

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

	private static void pollKeyboard() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				switch (Keyboard.getEventKey()) {
					//Quit the game
					case Keyboard.KEY_ESCAPE:
						Menu.pause();
						break;
					case Keyboard.KEY_UP:
						System.out.println("Key UP!");
						break;
					case Keyboard.KEY_DOWN:
						System.out.println("Key DOWN!");
						break;
					case Keyboard.KEY_LEFT:
						System.out.println("Key LEFT!");
						break;
					case Keyboard.KEY_RIGHT:
						System.out.println("Key RIGHT!");
						break;
				}
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			Graphics.camera.panForward(-panSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			Graphics.camera.panForward(panSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			Graphics.camera.panRight(panSpeed);
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			Graphics.camera.panRight(-panSpeed);
	}
	
	public static void processPick(int pickX, int pickY) {
		
		float id[] = new float[3];

		ByteBuffer pixels = ByteBuffer.allocateDirect(12).order(ByteOrder.nativeOrder());
		
		GL11.glReadPixels(pickX, pickY, 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);
		
		id[0] = pixels.get();
		id[1] = pixels.get();
		id[2] = pixels.get();

		if (id[0] < 0) id[0] += 256;
		if (id[1] < 0) id[1] += 256;
		if (id[2] < 0) id[2] += 256;
		
		Entity tempEnt = Resources.pickingHashMap.get(id);
		System.out.println("clicked id: " + Physics.printArray(id));

		if (tempEnt != null) {
			System.out.println("clicked id: " + Physics.printArray(id));
			System.out.println("position of entity: " + Physics.printVector(tempEnt.position));
		}
	
	}
}
