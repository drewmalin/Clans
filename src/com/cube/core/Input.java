package com.cube.core;

import java.util.logging.Level;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.cube.gui.Menu;
import com.cube.gui.Window;


public class Input {
	
	private static float panSpeed = .1f;
	private static float rotateSpeed = .1f;
	private static float scrollSpeed = .1f;
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
					case Keyboard.KEY_W:
						System.out.println("Key W!");
						break;
					case Keyboard.KEY_S:
						System.out.println("Key S!");
						break;
					case Keyboard.KEY_A:
						System.out.println("Key A!");
						break;
					case Keyboard.KEY_D:
						System.out.println("Key D!");
						break;
				}
			}
		}
	}
}
