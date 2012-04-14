package com.cube.gui;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import com.cube.core.Engine;
import com.cube.gui.MessageBox;

public class Window extends Canvas {
	public 	ArrayList<Button> 		buttons;
	
	public Window(int _x, int _y, int w, int h, boolean _show) {
		super(_x, _y, w, h, _show);
		buttons = new ArrayList<Button>();
		stealContext = false;
	}

	public void draw() {
		if (show) {
			super.draw();
			drawButtons();
		}
	}

	public void drawButtons() {
		for (Button b : buttons) {
			b.draw();
		}
	}
	public void closeWindow() {
		for (MessageBox mb : messageBoxes) {
			mb.show = false;
		}
		show = false;
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
	
	public void poll() {
		pollMouse();
		pollKeyboard();
	}
	
	public void pollMouse() {
		for (Button b : buttons) {
			b.checkHover(Mouse.getX(), Engine.HEIGHT - Mouse.getY());
		}
		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				switch (Mouse.getEventButton()) {
					case 0:	//Left click
						for (Button b : buttons)
							if (b.hovering) {
								b.onClick();
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
						if (id == Menu.PAUSE)
							Menu.unPause();
						break;
				}
			}
		}
	}
	
	public void createButton(int xOffset, int yOffset, int width, int height, boolean show) {
		buttons.add(new Button(x + xOffset, y + yOffset, width, height, show));
	}

}