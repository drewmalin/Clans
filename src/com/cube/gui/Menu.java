package com.cube.gui;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.cube.core.Engine;
import com.cube.core.Graphics;
import com.cube.gui.MessageBox;
import com.cube.core.Resources;

public class Menu {
	public static int PAUSE = 0;
	public static int DIALOGUE = 1;
	public static int QUEST_LOG = 2;
	public static int INVENTORY = 3;
	public static int INFO = 4;
	public static int LOADING = 5;
	public static int START = 6;
	public static int OPTIONS = 7;
	
	public static ArrayList<Window> windows;
	public static ArrayList<Window> windowStack;
	
	public static void initialize() {
		windows = new ArrayList<Window>();
		windowStack = new ArrayList<Window>();
		
		createPauseMenu();
		createDialogueMenu();
		createQuestLog();
		createInventoryMenu();
		createInformationScreen();
		createLoadingScreen();
		createStartMenu();
		createOptionsMenu();
	}
	
	private static void createOptionsMenu() {
		windows.add(new Window(0, 0, (int)(Engine.WIDTH), (int)(Engine.HEIGHT), false));
		windows.get(OPTIONS).id = OPTIONS;
		windows.get(OPTIONS).setColor(0f, 0f, 0f, .5f);
		windows.get(OPTIONS).createMessageBox((int)(windows.get(OPTIONS).width * .05), (int)(windows.get(OPTIONS).height * .05), 500, 14, "Arial", 38, Color.WHITE);
		windows.get(OPTIONS).messageBoxes.get(0).setMessage("Options");
		
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .75), (int)(windows.get(OPTIONS).height * .2), 200, 28, false);
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .75), (int)(windows.get(OPTIONS).height * .4), 200, 28, false);
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .4), (int)(windows.get(OPTIONS).height * .2f), 120, 28, false);
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .4), (int)(windows.get(OPTIONS).height * .25f), 120, 28, false);
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .4), (int)(windows.get(OPTIONS).height * .3f), 120, 28, false);

		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .1), (int)(windows.get(OPTIONS).height * .2), 120, 28, false);
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .1), (int)(windows.get(OPTIONS).height * .25), 120, 28, false);
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .1), (int)(windows.get(OPTIONS).height * .3f), 120, 28, false);
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .1), (int)(windows.get(OPTIONS).height * .35f), 120, 28, false);
		windows.get(OPTIONS).createButton((int)(windows.get(OPTIONS).width * .1), (int)(windows.get(OPTIONS).height * .4f), 120, 28, false);
		
		windows.get(OPTIONS).buttons.get(0).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(0).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(0).messageBoxes.get(0).setMessage("Accept");
		windows.get(OPTIONS).buttons.get(0).setClickListener(new ClickListener() {
			public void onClick() {
				Menu.saveSettings();
				//Resources.setVideoAudioSettings();
				//Graphics.reloadDisplaySettings();
				Menu.reloadTextures();
				Menu.reloadWindows();
				//Resources.reloadSkybox();
			}
		});
		
		windows.get(OPTIONS).buttons.get(1).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(1).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(1).messageBoxes.get(0).setMessage("Cancel");
		windows.get(OPTIONS).buttons.get(1).setClickListener(new ClickListener() {
			public void onClick() {
				popMenuStack();
			}
		});		
		
		windows.get(OPTIONS).buttons.get(2).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(2).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(2).messageBoxes.get(0).setMessage("Vertical Sync");
		windows.get(OPTIONS).buttons.get(2).selected = Engine.vSync;
		windows.get(OPTIONS).buttons.get(2).setClickListener(new ClickListener() {
			public void onClick() {
				windows.get(OPTIONS).buttons.get(2).selected = !windows.get(OPTIONS).buttons.get(2).selected;
			}
		});		
		
		windows.get(OPTIONS).buttons.get(3).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(3).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(3).messageBoxes.get(0).setMessage("Full Screen");
		windows.get(OPTIONS).buttons.get(3).selected = Engine.fullscreen;
		windows.get(OPTIONS).buttons.get(3).setClickListener(new ClickListener() {
			public void onClick() {
				windows.get(OPTIONS).buttons.get(3).selected = !windows.get(OPTIONS).buttons.get(3).selected;
			}
		});		
		
		windows.get(OPTIONS).buttons.get(4).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(4).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(4).messageBoxes.get(0).setMessage("Anti Aliasing");
		windows.get(OPTIONS).buttons.get(4).selected = Engine.AA;
		windows.get(OPTIONS).buttons.get(4).setClickListener(new ClickListener() {
			public void onClick() {
				windows.get(OPTIONS).buttons.get(4).selected = !windows.get(OPTIONS).buttons.get(4).selected;
			}
		});
		/* Start Resolution */
		windows.get(OPTIONS).buttons.get(5).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(5).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(5).messageBoxes.get(0).setMessage("1024x640");
		windows.get(OPTIONS).buttons.get(5).selected = Engine.AA;
		windows.get(OPTIONS).buttons.get(5).setClickListener(new ClickListener() {
			public void onClick() {
				windows.get(OPTIONS).buttons.get(5).selected = !windows.get(OPTIONS).buttons.get(5).selected;
				
				windows.get(OPTIONS).buttons.get(6).selected = false;
				windows.get(OPTIONS).buttons.get(7).selected = false;
				windows.get(OPTIONS).buttons.get(8).selected = false;
				windows.get(OPTIONS).buttons.get(9).selected = false;
			}
		});		
		
		windows.get(OPTIONS).buttons.get(6).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(6).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(6).messageBoxes.get(0).setMessage("1152x720");
		windows.get(OPTIONS).buttons.get(6).selected = Engine.AA;
		windows.get(OPTIONS).buttons.get(6).setClickListener(new ClickListener() {
			public void onClick() {
				windows.get(OPTIONS).buttons.get(6).selected = !windows.get(OPTIONS).buttons.get(6).selected;
				
				windows.get(OPTIONS).buttons.get(5).selected = false;
				windows.get(OPTIONS).buttons.get(7).selected = false;
				windows.get(OPTIONS).buttons.get(8).selected = false;
				windows.get(OPTIONS).buttons.get(9).selected = false;
			}
		});		
		
		windows.get(OPTIONS).buttons.get(7).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(7).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(7).messageBoxes.get(0).setMessage("1280x1024");
		windows.get(OPTIONS).buttons.get(7).selected = Engine.AA;
		windows.get(OPTIONS).buttons.get(7).setClickListener(new ClickListener() {
			public void onClick() {
				windows.get(OPTIONS).buttons.get(7).selected = !windows.get(OPTIONS).buttons.get(7).selected;
				
				windows.get(OPTIONS).buttons.get(5).selected = false;
				windows.get(OPTIONS).buttons.get(6).selected = false;
				windows.get(OPTIONS).buttons.get(8).selected = false;
				windows.get(OPTIONS).buttons.get(9).selected = false;
			}
		});		
		windows.get(OPTIONS).buttons.get(8).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(8).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(8).messageBoxes.get(0).setMessage("1440x852");
		windows.get(OPTIONS).buttons.get(8).selected = Engine.AA;
		windows.get(OPTIONS).buttons.get(8).setClickListener(new ClickListener() {
			public void onClick() {
				windows.get(OPTIONS).buttons.get(8).selected = !windows.get(OPTIONS).buttons.get(8).selected;
				
				windows.get(OPTIONS).buttons.get(5).selected = false;
				windows.get(OPTIONS).buttons.get(6).selected = false;
				windows.get(OPTIONS).buttons.get(7).selected = false;
				windows.get(OPTIONS).buttons.get(9).selected = false;
			}
		});		
		windows.get(OPTIONS).buttons.get(9).setColor(.5f, .7f, .2f, 1f);
		windows.get(OPTIONS).buttons.get(9).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(OPTIONS).buttons.get(9).messageBoxes.get(0).setMessage("1680x1050");
		windows.get(OPTIONS).buttons.get(9).selected = Engine.AA;
		windows.get(OPTIONS).buttons.get(9).setClickListener(new ClickListener() {
			public void onClick() {
				windows.get(OPTIONS).buttons.get(9).selected = !windows.get(OPTIONS).buttons.get(9).selected;
				
				windows.get(OPTIONS).buttons.get(5).selected = false;
				windows.get(OPTIONS).buttons.get(6).selected = false;
				windows.get(OPTIONS).buttons.get(7).selected = false;
				windows.get(OPTIONS).buttons.get(8).selected = false;
			}
		});		
	}

	protected static void reloadWindows() {
		windows.get(OPTIONS).height = Engine.HEIGHT;
		windows.get(OPTIONS).width = Engine.WIDTH;
		windows.get(START).height = Engine.HEIGHT;
		windows.get(START).width = Engine.WIDTH;
		windows.get(LOADING).height = Engine.HEIGHT;
		windows.get(LOADING).width = Engine.WIDTH;
		windows.get(PAUSE).x = (int) (Engine.WIDTH * .4);
		windows.get(PAUSE).y = (int) (Engine.HEIGHT * .2);

	}

	protected static void reloadTextures() {
		for (Window w : windows) {
			for (MessageBox mb : w.messageBoxes) {
				mb.reload();
			}
			for (Button b : w.buttons) {
				b.messageBoxes.get(0).reload();
			}
		}
	}

	protected static void saveSettings() {
		boolean tempAA, tempVS, tempFS;
		int tempMSAA, tempWidth = 0, tempHeight = 0;
		String tempRes;
		
		tempVS = windows.get(OPTIONS).buttons.get(2).selected;
		tempFS = windows.get(OPTIONS).buttons.get(3).selected;
		tempAA = windows.get(OPTIONS).buttons.get(4).selected;
		tempMSAA = Engine.MSAA;
		
		for (int i = 5; i <= 9; i++) {
			if (windows.get(OPTIONS).buttons.get(i).selected) {
				tempRes = windows.get(OPTIONS).buttons.get(i).messageBoxes.get(0).message;
				tempWidth = Integer.parseInt(tempRes.substring(0, tempRes.indexOf("x")));
				tempHeight = Integer.parseInt(tempRes.substring(tempRes.indexOf("x")+1));
				break;
			}
				
		}
		//Resources.saveSettings(tempVS, tempFS, tempAA, tempMSAA, tempWidth, tempHeight);
	}

	private static void createStartMenu() {
		windows.add(new Window(0, 0, (int)(Engine.WIDTH), (int)(Engine.HEIGHT), false));
		windows.get(START).id = START;
		windows.get(START).setColor(0f, 0f, 0f, .5f);
		windows.get(START).createMessageBox((int)(windows.get(START).width * .05), (int)(windows.get(START).height * .05), 500, 14, "resources/fonts/ZeroThree.ttf", 38, Color.WHITE);
		windows.get(START).messageBoxes.get(0).setMessage("DISCONNECT");
		windows.get(START).createButton((int)(windows.get(START).width * .75), (int)(windows.get(START).height * .2), 200, 28, false);
		windows.get(START).createButton((int)(windows.get(START).width * .75), (int)(windows.get(START).height * .4), 200, 28, false);
		windows.get(START).createButton((int)(windows.get(START).width * .75), (int)(windows.get(START).height * .6), 200, 28, false);
		windows.get(START).createButton((int)(windows.get(START).width * .75), (int)(windows.get(START).height * .8), 200, 28, false);

		windows.get(START).buttons.get(0).setColor(.5f, .7f, .2f, 1f);
		windows.get(START).buttons.get(0).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(START).buttons.get(0).messageBoxes.get(0).setMessage("New Game");
		windows.get(START).buttons.get(0).setClickListener(new ClickListener() {
			public void onClick() {
				popMenuStack();
				//Resources.changeLevel("resources/levels/start.lvl");
			}
		});
		
		windows.get(START).buttons.get(1).setColor(.5f, .7f, .2f, 1f);
		windows.get(START).buttons.get(1).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(START).buttons.get(1).messageBoxes.get(0).setMessage("Load Game");
		windows.get(START).buttons.get(1).setClickListener(new ClickListener() {
			public void onClick() {
				System.out.println("Load Game!!!");
			}
		});
		
		windows.get(START).buttons.get(2).setColor(.5f, .7f, .2f, 1f);
		windows.get(START).buttons.get(2).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(START).buttons.get(2).messageBoxes.get(0).setMessage("Options");
		windows.get(START).buttons.get(2).setClickListener(new ClickListener() {
			public void onClick() {
				pushMenuStack(windows.get(OPTIONS));
			}
		});
		
		windows.get(START).buttons.get(3).setColor(.5f, .7f, .2f, 1f);
		windows.get(START).buttons.get(3).createMessageBox(5, 5, 200, 16, "Arial", 18, Color.BLUE);
		windows.get(START).buttons.get(3).messageBoxes.get(0).setMessage("Exit");
		windows.get(START).buttons.get(3).setClickListener(new ClickListener() {
			public void onClick() {
				popMenuStack();
				System.exit(0);
			}
		});		
	}

	private static void createLoadingScreen() {
		windows.add(new Window(0, 0, (int)(Engine.WIDTH), (int)(Engine.HEIGHT), false));
		windows.get(LOADING).id = LOADING;
		windows.get(LOADING).setColor(0f, 0f, 0f, 1f);
		windows.get(LOADING).createMessageBox((int)(Engine.WIDTH * .3), (int)(Engine.HEIGHT * .8), 500, 14, "Arial", 18, Color.WHITE);		
	}

	private static void createInformationScreen() {
		windows.add(new Window((int)(Engine.WIDTH * .7), (int)(Engine.HEIGHT * .8), 185, 48, false));
		windows.get(INFO).id = INFO;
		windows.get(INFO).setColor(1f, 1f, 1f, 0.8f);
		windows.get(INFO).createMessageBox(5, 5, 25, 14, "Times New Roman", 18, Color.BLACK);		
	}

	private static void createInventoryMenu() {
		windows.add(new Window(0, 0, 0, 0, false));
		windows.get(INVENTORY).id = INVENTORY;		
	}

	private static void createQuestLog() {
		windows.add(new Window(0, 0, 0, 0, false));
		windows.get(QUEST_LOG).id = QUEST_LOG;		
	}

	private static void createDialogueMenu() {
		windows.add(new Window(0, 0, 510, 100, false));
		windows.get(DIALOGUE).id = DIALOGUE;
		windows.get(DIALOGUE).setColor(1.0f, 1.0f, 1.0f, 0.8f);
		windows.get(DIALOGUE).createMessageBox(5, 5, 70, 16, "Times New Roman", 18, Color.BLUE);		
	}

	private static void createPauseMenu() {
		windows.add(new Window((int)(Engine.WIDTH * .4), (int)(Engine.HEIGHT * .2), 210, 113, false));	
		windows.get(PAUSE).id = PAUSE;
		windows.get(PAUSE).setColor(0f, 0f, 0f, .8f);
		windows.get(PAUSE).createMessageBox(5, 5, 100, 14, "resources/fonts/ZeroThree.ttf", 28, Color.WHITE);
		windows.get(PAUSE).messageBoxes.get(0).setMessage("Game Paused");
		windows.get(PAUSE).createButton(5, 50, 200, 28, false);
		windows.get(PAUSE).createButton(5, 80, 200, 28, false);
		
		windows.get(PAUSE).buttons.get(0).setColor(.5f, .7f, .2f, 1f);
		windows.get(PAUSE).buttons.get(0).createMessageBox(5, 5, 200, 16, "resources/fonts/ZeroThree.ttf", 18, Color.BLUE);
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
		windows.get(PAUSE).buttons.get(1).createMessageBox(5, 5, 200, 16, "resources/fonts/ZeroThree.ttf", 18, Color.BLUE);
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
