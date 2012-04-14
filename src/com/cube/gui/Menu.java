package com.cube.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.cube.core.Engine;
import com.cube.core.FileLogger;
import com.cube.gui.MessageBox;

public class Menu {
	public static int PAUSE = 0;
	
	public static ArrayList<Window> windows;
	public static ArrayList<Window> windowStack;
	
	public static void initialize() {
		windows = new ArrayList<Window>();
		windowStack = new ArrayList<Window>();
		
		createPauseMenu();
		
		FileLogger.logger.log(Level.INFO, "Menu initialized");

	}


	private static void createPauseMenu() {
		windows.add(new Window((int)(Engine.WIDTH * .4), (int)(Engine.HEIGHT * .2), 210, 113, false));	
		windows.get(PAUSE).id = PAUSE;
		windows.get(PAUSE).setColor(0f, 0f, 0f, .8f);
		windows.get(PAUSE).createMessageBox(5, 5, 100, 14, "Times New Roman", 28, Color.WHITE);
		windows.get(PAUSE).messageBoxes.get(0).setMessage("Game Paused");
		windows.get(PAUSE).createButton(5, 50, 200, 28, false);
		windows.get(PAUSE).createButton(5, 80, 200, 28, false);
		
		windows.get(PAUSE).buttons.get(0).setColor(.5f, .7f, .2f, 1f);
		windows.get(PAUSE).buttons.get(0).createMessageBox(5, 5, 200, 16, "Times New Roman", 18, Color.BLUE);
		windows.get(PAUSE).buttons.get(0).messageBoxes.get(0).setMessage("Resume");
		windows.get(PAUSE).buttons.get(0).setClickListener(new ClickListener() {
			public void onClick() {
				if (Display.isFullscreen())
					Mouse.setGrabbed(true);
				else
					Mouse.setGrabbed(false);
				popMenuStack();
			}
		});
		
		windows.get(PAUSE).buttons.get(1).setColor(.5f, .7f, .2f, 1f);
		windows.get(PAUSE).buttons.get(1).createMessageBox(5, 5, 200, 16, "Times New Roman", 18, Color.BLUE);
		windows.get(PAUSE).buttons.get(1).messageBoxes.get(0).setMessage("Exit");
		windows.get(PAUSE).buttons.get(1).setClickListener(new ClickListener() {
			public void onClick() {
				System.exit(0);
			}
		});
	}

	public static void draw() {
		for (Window w : windows) {
			w.draw();
		}
	}
	
	public static void pause() {
		Mouse.setGrabbed(false);
		pushMenuStack(Menu.windows.get(Menu.PAUSE));
	}
	
	public static void unPause() {
		if (Display.isFullscreen())
			Mouse.setGrabbed(true);
		else
			Mouse.setGrabbed(false);
		popMenuStack();
	}
	
	public static void pushMenuStack(Window w) {
		Window topWindow;
		
		if (windowStack.size() > 0) {
			topWindow = windowStack.get(windowStack.size()-1);
			close(topWindow);
		}
		open(w);
		windowStack.add(w);
		
	}
	
	public static void popMenuStack() {
		Window topWindow;
		
		if (windowStack.size() > 0) {
			topWindow = windowStack.get(windowStack.size()-1);
			close(topWindow);
			windowStack.remove(windowStack.size()-1);
			
			if (windowStack.size() > 0) {
				topWindow = windowStack.get(windowStack.size()-1);
				open(topWindow);
			}
		}
	}
	
	private static void open(Window w) {
		for (MessageBox mw : w.messageBoxes) {
			mw.show = true;
		}
		for (Button b : w.buttons) {
			if (b.messageBoxes.size() != 0)
				b.messageBoxes.get(0).show = true;
			b.show = true;
		}
		w.show = true;
		w.stealContext = true;
	}
	
	private static void close(Window w) {
		for (MessageBox mw : w.messageBoxes) {
			mw.show = false;
		}
		for (Button b : w.buttons) {
			if (b.messageBoxes.size() != 0)
				b.messageBoxes.get(0).show = false;
			b.show = false;
		}
		w.show = false;
		w.stealContext = false;
	}
}
