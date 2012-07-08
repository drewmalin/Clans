package com.cube.gui;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import com.cube.core.Engine;
import com.cube.core.Input;
import com.cube.gui.MessageBox;

public class Window extends Canvas {
	public HashMap<String, Button> buttons;
	public Button clickedButton;
	public String name;

	public Window() {
		buttons = new HashMap<String, Button>();
	}

	public void draw() {
		super.draw();
		drawMessages();
		drawButtons();
	}

	public void drawButtons() {
		for (String b : buttons.keySet()) {
			buttons.get(b).draw();
		}
//		for (String b : buttons.keySet()) {
//			if (buttons.get(b).hoverMessageBox != null) {
//				if (buttons.get(b).hovering)
//					buttons.get(b).openHoverMessageBox();
//				else
//					buttons.get(b).closeHoverMessageBox();
//			}
//		}
	}
	
	public void updateLoadingScreen(String message, float percentage) {
		
		this.draw();
		messageBoxes.get(0).setMessage(message);

		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor4f(1f, 0f, 0f, 1f);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f((Engine.WIDTH * .1f), (Engine.HEIGHT * .75f));
			GL11.glVertex2f((Engine.WIDTH * .1f), (Engine.HEIGHT * .75f) + 10);
			GL11.glVertex2f((Engine.WIDTH * .1f) + ((Engine.WIDTH * .8f) * percentage), (Engine.HEIGHT * .75f) + 10);		
			GL11.glVertex2f((Engine.WIDTH * .1f) + ((Engine.WIDTH * .8f) * percentage), (Engine.HEIGHT * .75f));
		GL11.glEnd();
		GL11.glPopMatrix();
		Display.update();

	}
	
	public void checkGuiClick() {
		for (String b : buttons.keySet()) {
			buttons.get(b).checkHover(Mouse.getX(), Engine.HEIGHT - Mouse.getY());
			if (buttons.get(b).hovering && Mouse.isButtonDown(0)) {
				clickedButton = buttons.get(b);
			}
			if (buttons.get(b).hovering && Input.isMouseButtonUp() && clickedButton != null && clickedButton.equals(b)) {
				buttons.get(b).onClick();
				clickedButton = null; 
			}
		}
	}
	
	public void poll() {
		pollMouse();
		pollKeyboard();
	}
	
	public void pollMouse() {
		for (String b : buttons.keySet()) {
			buttons.get(b).checkHover(Mouse.getX(), Engine.HEIGHT - Mouse.getY());
		}
		for (MessageBox mb : messageBoxes) {
			mb.checkHover(Mouse.getX(), Engine.HEIGHT - Mouse.getY());
		}
		
		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				switch (Mouse.getEventButton()) {
					case 0:	//Left click
						for (String b : buttons.keySet())
							if (buttons.get(b).hovering) {
								buttons.get(b).onClick();
							}
						break;
					case 1: //Right click
						break;
				}
			}
		}
	}
	
	public void pollKeyboard() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				
				switch (Keyboard.getEventKey()) {
					//Quit the game
					case Keyboard.KEY_ESCAPE:
						System.exit(0);
						break;
				}
			}
		}
	}
}