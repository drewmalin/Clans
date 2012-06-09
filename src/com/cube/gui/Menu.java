package com.cube.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.cube.core.Building;
import com.cube.core.Clan;
import com.cube.core.Engine;
import com.cube.core.Game;
import com.cube.core.Input;
import com.cube.core.Resources;
import com.cube.gui.MessageBox;
import com.cube.util.FileLogger;
import com.cube.util.Utilities;

public class Menu {
	public static int PAUSE 	= 0;
	public static int INFO 		= 1;
	public static int CONSOLE 	= 2;
	public static int BUILDING 	= 3;
	
	public static ArrayList<Window> windows;
	public static ArrayList<Window> windowStack;
	
	public static String consoleMessage;
	private static String finalMessage;
	
	public static void initialize() {
		consoleMessage = "";
		finalMessage = "";
		
		windows = new ArrayList<Window>();
		windowStack = new ArrayList<Window>();
		
		FileLogger.logger.log(Level.INFO, "Menu initialized");

	}
	
	public static void create() {
		createPauseMenu();
		createInfoBar();
		createConsole();
		createBuildingSubMenu();
	}

	/*
	 * Pause menu to be opened using the escape key or button located on the info bar (future).
	 */
	private static void createPauseMenu() {
		windows.add(new Window((int)(Engine.WIDTH * .4), (int)(Engine.HEIGHT * .2), 210, 113, false));	
		windows.get(PAUSE).id = PAUSE;
		windows.get(PAUSE).setColor(0f, 0f, 0f, .8f);
		windows.get(PAUSE).createMessageBox(5, 5, 100, 14, "Times New Roman", 28, Color.WHITE);
		windows.get(PAUSE).messageBoxes.get(0).setMessage("Game Paused");
		windows.get(PAUSE).createButton(5, 50, 200, 28, false);
		windows.get(PAUSE).createButton(5, 80, 200, 28, false);
		
		
		//--------------------- Resume Button ---------------------//
		
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

		//--------------------- Exit Button ---------------------//

		windows.get(PAUSE).buttons.get(1).setColor(.5f, .7f, .2f, 1f);
		windows.get(PAUSE).buttons.get(1).createMessageBox(5, 5, 200, 16, "Times New Roman", 18, Color.BLUE);
		windows.get(PAUSE).buttons.get(1).messageBoxes.get(0).setMessage("Exit");
		windows.get(PAUSE).buttons.get(1).setClickListener(new ClickListener() {
			public void onClick() {
				System.exit(0);
			}
		});
	}
	
	/*
	 * Info bar - used to display pertinent information and house several menu and game-related buttons. 
	 */
	private static void createInfoBar() {
		windows.add(new Window(0, 0, Engine.WIDTH, 20, true));
		windows.get(INFO).id = INFO;
		windows.get(INFO).setColor(0f, 0f, 0f, .8f);
		
		windows.get(INFO).createMessageBox(0, 0, 125, 14, "Times New Roman", 14, Color.WHITE);
		windows.get(INFO).createMessageBox(125, 0, 125, 14, "Times New Roman", 14, Color.WHITE);
		windows.get(INFO).createMessageBox(250, 0, 125, 14, "Times New Roman", 14, Color.WHITE);
		windows.get(INFO).createMessageBox(375, 0, 125, 14, "Times New Roman", 14, Color.WHITE);
		windows.get(INFO).createMessageBox(500, 0, 125, 14, "Times New Roman", 14, Color.WHITE);
		windows.get(INFO).createMessageBox(Engine.WIDTH - 75, 0, 75, 14, "Times New Roman", 14, Color.WHITE);
		
		windows.get(INFO).createButton(625, 0, 20, 20, true);
		
		windows.get(INFO).messageBoxes.get(0).setMessage("Unit Count: ");
		windows.get(INFO).messageBoxes.get(1).setMessage("Meat Count: ");
		windows.get(INFO).messageBoxes.get(2).setMessage("Berry Count: ");
		windows.get(INFO).messageBoxes.get(3).setMessage("Wood Count: ");
		windows.get(INFO).messageBoxes.get(4).setMessage("Leather Count: ");
		windows.get(INFO).messageBoxes.get(5).setMessage("FPS: ");
	
		//--------------------- Building Button ---------------------//

		windows.get(INFO).buttons.get(0).setColor(.5f, .7f, .2f, 1f);
		windows.get(INFO).buttons.get(0).setClickListener(new ClickListener() {
			public void onClick() {
				if (windows.get(BUILDING).show) 
					close(windows.get(BUILDING));
				else
					open(windows.get(BUILDING), false);
			}
		});
	}
	
	/*
	 * Console - used primarily as an in-game debugging message printer. This will probably evolve into a per-unit
	 * information system.
	 */
	private static void createConsole() {
		windows.add(new Window(0, (int)(Engine.HEIGHT * .8), Engine.WIDTH, (int)(Engine.HEIGHT * .2), false));
		windows.get(CONSOLE).id = CONSOLE;
		windows.get(CONSOLE).setColor(0f, 0f, 0f, .8f);
		
		windows.get(CONSOLE).createMessageBox(0, 0, Engine.WIDTH, 14, "Times New Roman", 14, Color.WHITE);
		windows.get(CONSOLE).messageBoxes.get(0).setMessage("Test...");
	}
	
	/*
	 * Building submenu - used to select which buildings to create (probably will change to be more unit-specific instead of
	 * clan specific, but testing will show which way is better).
	 */
	private static void createBuildingSubMenu() {
		windows.add(new Window(windows.get(INFO).buttons.get(0).x, windows.get(INFO).y + windows.get(INFO).height, 200, 200, false));
		windows.get(BUILDING).id = BUILDING;
		windows.get(BUILDING).setColor(0f, 0f, 1f, .8f);
		
		for (int i = 0; i < 3; i++) {
			windows.get(BUILDING).createButton(5 + (50 * (i % 3)), 5 + (50 * (i / 3)), 20, 20, false);
			windows.get(BUILDING).buttons.get(i).setColor(.5f, .5f, .5f, 1.0f);
		}
		
		windows.get(BUILDING).buttons.get(0).setHoverMessage(Resources.buildingLibrary.get("PEN").info());
		windows.get(BUILDING).buttons.get(0).setClickListener(new ClickListener() {
			public void onClick() {
				if (Building.meetsRequirements(Game.playerClan, Resources.buildingLibrary.get("PEN"))) {
					System.out.println("Requirements met!");
					Game.buildingToBeBuilt = Resources.buildingLibrary.get("PEN");
					windows.get(BUILDING).closeWindow();
				}
				else {
					System.out.println("not yet...");
				}
			}
		});
		windows.get(BUILDING).buttons.get(1).setHoverMessage(Resources.buildingLibrary.get("HOUSE").info());
		windows.get(BUILDING).buttons.get(1).setClickListener(new ClickListener() {
			public void onClick() {
				System.out.println("clicked!");
				if (Building.meetsRequirements(Game.playerClan, Resources.buildingLibrary.get("HOUSE"))) {
					System.out.println("Requirements met!");
					Game.buildingToBeBuilt = Resources.buildingLibrary.get("HOUSE");
					windows.get(BUILDING).closeWindow();
				}
				else {
					System.out.println("not yet...");
				}
			}
		});
		windows.get(BUILDING).buttons.get(2).setHoverMessage(Resources.buildingLibrary.get("BARRACKS").info());
		windows.get(BUILDING).buttons.get(2).setClickListener(new ClickListener() {
			public void onClick() {
				System.out.println("clicked!");
				if (Building.meetsRequirements(Game.playerClan, Resources.buildingLibrary.get("BARRACKS"))) {
					System.out.println("Requirements met!");
					Game.buildingToBeBuilt = Resources.buildingLibrary.get("BARRACKS");
					windows.get(BUILDING).closeWindow();
				}
				else {
					System.out.println("not yet...");
				}
			}
		});

	}
	
	/*
	 * Update is used for the console menu-- information about the current game state is retrieved here and printed to
	 * the menu.
	 */
	public static void update() {
		int meatCount 		= Game.playerClan.clanStockpile.items.get(Resources.itemLibrary.get("MEAT")) == null ? 0 : 
							  Game.playerClan.clanStockpile.items.get(Resources.itemLibrary.get("MEAT"));
		int berryCount 		= Game.playerClan.clanStockpile.items.get(Resources.itemLibrary.get("BERRY")) == null ? 0 :
						 	  Game.playerClan.clanStockpile.items.get(Resources.itemLibrary.get("BERRY"));
		int woodCount 		= Game.playerClan.clanStockpile.items.get(Resources.itemLibrary.get("WOOD")) == null ? 0 :
							  Game.playerClan.clanStockpile.items.get(Resources.itemLibrary.get("WOOD"));
		int leatherCount 	= Game.playerClan.clanStockpile.items.get(Resources.itemLibrary.get("LEATHER")) == null ? 0 :
							  Game.playerClan.clanStockpile.items.get(Resources.itemLibrary.get("LEATHER"));
		
		windows.get(INFO).messageBoxes.get(0).setMessage("Unit Count: " 	+ Game.playerClan.units.size());
		windows.get(INFO).messageBoxes.get(1).setMessage("Meat Count: " 	+ meatCount);
		windows.get(INFO).messageBoxes.get(2).setMessage("Berry Count: " 	+ berryCount);
		windows.get(INFO).messageBoxes.get(3).setMessage("Wood Count: " 	+ woodCount);
		windows.get(INFO).messageBoxes.get(4).setMessage("Leather Count: " 	+ leatherCount);
		windows.get(INFO).messageBoxes.get(5).setMessage("FPS: " + Utilities.frameRate);

		if (Game.selectedEntity != null) {
			consoleMessage = "";
			consoleMessage += "Selected entity: " + Game.selectedEntity.toString() + "\n";
			consoleMessage += "Inventory: \n" + Game.selectedEntity.inventory.printInventory();
		}
		
		constructConsoleMessage();
		windows.get(CONSOLE).messageBoxes.get(0).setMessage(finalMessage);
	}
	
	private static void constructConsoleMessage() {
		int lineCountMax = 5;
		String[] tempTokens = consoleMessage.split("\\n");
				
		if (tempTokens.length > lineCountMax) {
			finalMessage = "";
			for (int i = tempTokens.length - lineCountMax; i < tempTokens.length; i++) {
				finalMessage += tempTokens[i] + "\n";
			}
		} 
		else {
			finalMessage = consoleMessage;
		}
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
		open(w, true);
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
				open(topWindow, true);
			}
		}
	}
	
	private static void open(Window w, boolean stealContext) {
		for (MessageBox mw : w.messageBoxes) {
			mw.show = true;
		}
		for (Button b : w.buttons) {
			if (b.messageBoxes.size() != 0)
				b.messageBoxes.get(0).show = true;
			b.show = true;
		}
		w.show = true;
		
		if (stealContext)
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
