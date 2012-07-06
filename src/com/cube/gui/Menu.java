package com.cube.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.cube.core.Engine;
import com.cube.core.Graphics;
import com.cube.gui.MessageBox;
import com.cube.util.FileLogger;
import com.cube.util.Node;
import com.cube.util.XMLParser;

public class Menu {
	
	public static HashMap<String, Window> windows;
	public static ArrayList<Window> windowStack;
	
	/** Initialize the Menu system. Create a new hashmap to store all possible windows,
	 * and instantiate the window stack.
	 * 
	 */
	public static void initialize() {
		
		windows = new HashMap<String, Window>();
		windowStack = new ArrayList<Window>();
		
		FileLogger.logger.log(Level.INFO, "Menu initialized");

	}
	
	/** Load a menu xml file and store the resulting window, along with all of its message
	 * boxes, buttons, and attributes.
	 * 
	 * @param filename
	 */
	public static void loadMenu(String filename) {
		XMLParser itemLib = new XMLParser(filename);

		for (Node windowEl : itemLib.root.children) {
			Window window = new Window();
			window.name = filename.substring(filename.lastIndexOf("/")+1, filename.indexOf("."));
				
			for (Node infoEl : windowEl.children) {
				if (infoEl.name.equals("x"))
					window.x = infoEl.readInt();
				else if (infoEl.name.equals("y"))
					window.y = infoEl.readInt();
				else if (infoEl.name.equals("width")) {
					if (infoEl.readString().equals("FULL"))
						window.width = Engine.WIDTH;
					else
						window.width = infoEl.readInt();
				}
				else if (infoEl.name.equals("height")) {
					if (infoEl.readString().equals("FULL"))
						window.height = Engine.HEIGHT;
					else
						window.height = infoEl.readInt();
				}
				else if (infoEl.name.equals("backgroundImage"))
					window.setBackgroundImage(infoEl.readString());
				else if (infoEl.name.equals("backgroundColor"))
					window.setColor(infoEl.readFloatArray());
				else if (infoEl.name.equals("messageBox"))
					loadMessageBox(infoEl, window);
				else if (infoEl.name.equals("button")) {
					Button button = new Button();
				
					for (Node buttonEl : infoEl.children) {
						if (buttonEl.name.equals("name"))
							button.name = buttonEl.readString();
						else if (buttonEl.name.equals("x"))
							button.x = window.x + buttonEl.readInt();
						else if (buttonEl.name.equals("y"))
							button.y = window.y + buttonEl.readInt();
						else if (buttonEl.name.equals("width"))
							button.width = buttonEl.readInt();
						else if (buttonEl.name.equals("height"))
							button.height = buttonEl.readInt();
						else if (buttonEl.name.equals("messageBox"))
							loadMessageBox(buttonEl, button);
						else if (buttonEl.name.equals("hoverMessageBox"))
							loadHoverMessageBox(buttonEl, button);
					}
					window.buttons.put(button.name, button);
				}
			}
			windows.put(window.name, window);
		}
	}
	
	private static void loadMessageBox(Node xmlContext, Canvas canvas) {
		MessageBox mb = new MessageBox();
		for (Node mbEl : xmlContext.children) {
			if (mbEl.name.equals("x"))
				mb.x = canvas.x + mbEl.readInt();
			else if (mbEl.name.equals("y"))
				mb.y = canvas.y + mbEl.readInt();
			else if (mbEl.name.equals("width"))
				mb.width = mbEl.readInt();
			else if (mbEl.name.equals("height"))
				mb.height = mbEl.readInt();
			else if (mbEl.name.equals("fontName"))
				mb.fontName = mbEl.readString();
			else if (mbEl.name.equals("fontSize"))
				mb.fontSize = mbEl.readInt();
			else if (mbEl.name.equals("fontColor"))
				mb.setFontColor(mbEl.readString());
			else if (mbEl.name.equals("message"))
				mb.message = mbEl.readString();
		}
		mb.load();
		canvas.messageBoxes.add(mb);
	}
	
	private static void loadHoverMessageBox(Node xmlContext, Button button) {
		MessageBox mb = new MessageBox();
		
		for (Node mbEl : xmlContext.children) {
			if (mbEl.name.equals("width"))
				mb.width = mbEl.readInt();
			else if (mbEl.name.equals("height"))
				mb.height = mbEl.readInt();
			else if (mbEl.name.equals("fontName"))
				mb.fontName = mbEl.readString();
			else if (mbEl.name.equals("fontSize"))
				mb.fontSize = mbEl.readInt();
			else if (mbEl.name.equals("fontColor"))
				mb.setFontColor(mbEl.readString());
			else if (mbEl.name.equals("message"))
				mb.message = mbEl.readString();
		}
	
		button.hoverMessageBox = mb;
	}
	
	/** Update the window system-- draw the windows in the stack, poll for input
	 * from the context of the top of the window stack.
	 * 
	 */
	public static void update() {
		Graphics.disable3D();
		GL11.glDisable(GL11.GL_LIGHTING);											
		GL11.glMatrixMode(GL11.GL_PROJECTION);

		GL11.glLoadIdentity();
		GL11.glOrtho(0, Engine.WIDTH, Engine.HEIGHT, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glPushMatrix();
		
		drawStack();
		poll();
		
		Display.update();
		Display.sync(Engine.framerate);
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_LIGHTING);											
		Graphics.enable3D();
	}

	private static void drawStack() {
		for (Window w : windowStack) {
			w.draw();
		}
	}
	
	private static void poll() {
		windows.get(peekMenuStack()).poll();
	}
	
	/** Pushes a previously-loaded menu onto the menu stack. This has the effect
	 * of inactivating the previous window on the top of the stack (meaning user
	 * input does not effect it) and activating the new window.
	 * 
	 * @param window
	 */
	public static void pushMenuStack(String window) {
		Window topWindow;
		
		if (windowStack.size() > 0) {
			topWindow = windowStack.get(windowStack.size()-1);
			inactivate(topWindow);
		}
		activate(windows.get(window), true);
		windowStack.add(windows.get(window));
	}
	
	/** View the name of the window at the top of the menu stack.
	 * 
	 * @return
	 */
	public static String peekMenuStack() {
		return windowStack.get(windowStack.size()-1).name;
	}
	
	/** Remove the topmost window from the menu stack. 
	 * 
	 */
	public static void popMenuStack() {
		Window topWindow;
		
		if (windowStack.size() > 0) {
			topWindow = windowStack.get(windowStack.size()-1);
			inactivate(topWindow);
			windowStack.remove(windowStack.size()-1);
			
			if (windowStack.size() > 0) {
				topWindow = windowStack.get(windowStack.size()-1);
				activate(topWindow, true);
			}
		}
	}
	
	private static void activate(Window w, boolean active) {
		for (MessageBox mw : w.messageBoxes) {
			mw.show = true;
		}
		for (String b : w.buttons.keySet()) {
			if (w.buttons.get(b).messageBoxes.size() != 0)
				w.buttons.get(b).messageBoxes.get(0).show = true;
			w.buttons.get(b).show = true;
		}
		w.show = true;
		
		if (active)
			w.active = true;
	}
	
	private static void inactivate(Window w) {
		for (MessageBox mw : w.messageBoxes) {
			mw.show = false;
		}
		for (String b : w.buttons.keySet()) {
			if (w.buttons.get(b).messageBoxes.size() != 0)
				w.buttons.get(b).messageBoxes.get(0).show = false;
			w.buttons.get(b).show = false;
		}
		w.show = false;
		w.active = false;
	}
}
