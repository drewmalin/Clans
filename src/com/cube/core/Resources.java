package com.cube.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;



import com.cube.states.NeutralState;
import com.cube.util.FileLogger;
import com.cube.util.Node;
import com.cube.util.OBJParser;
import com.cube.util.Texture;
import com.cube.util.TextureLoader;
import com.cube.util.GeometryGroup;
import com.cube.util.XMLParser;
import com.cube.util.XMLWriter;


public class Resources {
	
	private static FileInputStream fstream;
	private static DataInputStream in;
	private static BufferedReader br;
	private static float[] lastColorID;
	public static Map map;
	
	/* Unique instance data structures - each unique instance of these
	 * classes is individually instantiated.
	 */
	public static ArrayList<Light> 	lights;
	public static ArrayList<Clan> 	clans;
	public static ArrayList<Entity> entities;
	
	/* Library instance data structures - only one instance of these classes
	 * is persisted (for lookup later)
	 */
	public static HashMap<String, Item> 	itemLibrary;
	public static HashMap<String, Model> 	modelLibrary;
	public static HashMap<String, Building> buildingLibrary;
	public static HashMap<String, Texture> 	textureLibrary;

	public static TextureLoader texLoader;
	public static HashMap<String, Entity> pickingHashMap;
	
	public static Texture selectionRing;

	public static void initialize() {
		
		entities 	= new ArrayList<Entity>();
		lights 		= new ArrayList<Light>();
		clans 		= new ArrayList<Clan>();
		texLoader 	= new TextureLoader();
		
		pickingHashMap 	= new HashMap<String, Entity>();
		itemLibrary 	= new HashMap<String, Item>();
		buildingLibrary = new HashMap<String, Building>();
		modelLibrary 	= new HashMap<String, Model>();
		textureLibrary  = new HashMap<String, Texture>();
		
		map = new Map(100);
		
		loadDefaults();
		
		FileLogger.logger.log(Level.INFO, "Resources initialized");
	}
	
	public static void loadDefaults() {
		try {
			modelLibrary.put("default", new Model("res/obj/barman.obj"));
			textureLibrary.put("default", texLoader.getTexture("res/img/brookstoneFTW.png"));
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
	}
	public static void loadEntityLib(String file) {
		XMLParser entityLib = new XMLParser(file);
		
		for (Node entityEl : entityLib.root.children) {
			Entity entity = new Entity();
			
			for (Node el : entityEl.children) {
				if (el.name.equals("name"))
					entity.name = el.readString();
				else if (el.name.equals("model"))
					entity.setModel(el.readString());
				else if (el.name.equals("scale"))
					entity.setScale(el.readFloatArray());
				else if (el.name.equals("type"))
					entity.setTypes(el.readStringArray());
				else if (el.name.equals("targets"))
					entity.setTargets(el.readStringArray());
				else if (el.name.equals("regions"))
					entity.setRegions(el.readStringArray());
				else if (el.name.equals("texture"))
					entity.setTexture(el.readString());
				else if (el.name.equals("popDensity"))
					entity.setPopDensity(el.readFloat());
				else if (el.name.equals("inventoryItem")) {
					String itemName = "";
					int itemCount = 0;
					
					for (Node item : el.children) {
						if (item.name.equals("name"))
							itemName = item.readString();
						else if (item.name.equals("count"))
							itemCount = item.readInt();
					}
					
					entity.inventory.addItems(itemLibrary.get(itemName), itemCount);
				}
			}
			
			//TODO: this needs to be dynamic
			entities.add(entity);
		}
	}
	
	public static void loadItemLib(String file) {
		XMLParser itemLib = new XMLParser(file);
		
		for (Node itemEl : itemLib.root.children) {
			Item item = new Item();
			
			for (Node el : itemEl.children) {
				if (el.name.equals("name"))
					item.name = el.readString();
				else if (el.name.equals("weight"))
					item.weight = el.readFloat();
				else if (el.name.equals("equipable"))
					item.equipable = el.readBoolean();
			}
			
			itemLibrary.put(item.name, item);
		}
	}
	
	public static void loadBuildingLib(String file) {
		XMLParser buildingLib = new XMLParser(file);
		
		for (Node buildingEl : buildingLib.root.children) {
			Building building = new Building();
			
			for (Node el : buildingEl.children) {
				if (el.name.equals("name")) {
					if (buildingLibrary.containsKey(building.name))
						building = buildingLibrary.get(building.name);
					building.name = el.readString();
				}
				else if (el.name.equals("width"))
					building.width = el.readInt();
				else if (el.name.equals("height"))
					building.height = el.readInt();
				else if (el.name.equals("itemPrereq")) {
					for (Node item : el.children) {
						String itemName = "";
						int itemCount = 0;
						
						for (Node itemEl : item.children) {
							if (itemEl.name.equals("name"))
								itemName = itemEl.readString();
							else if (itemEl.name.equals("count"))
								itemCount = itemEl.readInt();
						}
					}
				}
				else if (el.name.equals("buildingPrereq")) {
					for (Node bldg : el.children) {
						if (!buildingLibrary.containsKey(bldg.readString()))
							buildingLibrary.put(bldg.readString(), new Building());
						building.buildingPrereqs.add(buildingLibrary.get(bldg.readString()));
					}
				}
			}
		}
	}
	
	/* Jack: the bodies of the if/else if's need to be assigned to the new shader object,
	 * for example the "id" portion could be changed to:
	 * 
	 * if (el.name.equals("id"))
	 *    shader.id = el.readString();
	 */
	public static void loadShaderLib(String file) {
		XMLParser shaderLib = new XMLParser(file);
		
		for (Node shaderEl : shaderLib.root.children) {
			//Create shader object
			for (Node el : shaderEl.children) {
				if (el.name.equals("id"))
					el.readString();
				else if (el.name.equals("vertexShader"))
					el.readString();
				else if (el.name.equals("fragmentShader"))
					el.readString();
			}
		}
	}
	
	public static void loadSavedGame(String file) {
		XMLParser savedGame = new XMLParser(file);
		
		for (Node gameEl : savedGame.root.children) {
			if (gameEl.name.equals("map")) {
				for (Node mapEl : gameEl.children) {
					if (mapEl.name.equals("file"))
						map.file = mapEl.readString();
					else if (mapEl.name.equals("scale"))
						map.scale = mapEl.readFloat();
					else if (mapEl.name.equals("colorID"))
						map.colorID = mapEl.readIntArray();
				}
				map.initialize();
			}
			else if (gameEl.name.equals("camera")) {
				for (Node camEl : gameEl.children) {
					if (camEl.name.equals("thetaX"))
						Graphics.camera.setThetaX(camEl.readFloat());
					else if (camEl.name.equals("thetaY"))
						Graphics.camera.setThetaY(camEl.readFloat());
					else if (camEl.name.equals("target"))
						Graphics.camera.setTarget(camEl.readFloatArray());
					else if (camEl.name.equals("radius"))
						Graphics.camera.setRadius(camEl.readFloat());
				}
				Graphics.camera.updatePosition();
			}
			else if (gameEl.name.equals("clan")) {
				Game.playerClan = new Clan();
				for (Node clanEl : gameEl.children) {
					if (clanEl.name.equals("meat"))
						Game.playerClan.clanStockpile.addItems(itemLibrary.get("MEAT"), clanEl.readInt());
					else if (clanEl.name.equals("berry"))
						Game.playerClan.clanStockpile.addItems(itemLibrary.get("BERRY"), clanEl.readInt());
					else if (clanEl.name.equals("wood"))
						Game.playerClan.clanStockpile.addItems(itemLibrary.get("WOOD"), clanEl.readInt());
					else if (clanEl.name.equals("farmer"))
						Game.playerClan.farmerCount = clanEl.readInt();
					else if (clanEl.name.equals("hunter"))
						Game.playerClan.hunterCount = clanEl.readInt();
					else if (clanEl.name.equals("warrior"))
						Game.playerClan.warriorCount = clanEl.readInt();
					else if (clanEl.name.equals("builder"))
						Game.playerClan.builderCount = clanEl.readInt();
					else if (clanEl.name.equals("position"))
						Game.playerClan.setPosition(clanEl.readFloatArray());
				}
				clans.add(Game.playerClan);
			}
		}
	}
	
	public static void loadDisplaySettings(String file) {
		XMLParser displaySettings = new XMLParser(file);
		for (Node displayEl : displaySettings.root.children) {
			if (displayEl.name.equals("width"))
				Engine.WIDTH = displayEl.readInt();
			else if (displayEl.name.equals("height"))
				Engine.HEIGHT = displayEl.readInt();
			else if (displayEl.name.equals("vsync"))
				Engine.vSync = displayEl.readBoolean();
			else if (displayEl.name.equals("fullscreen"))
				Engine.fullscreen = displayEl.readBoolean();
			else if (displayEl.name.equals("samples"))
				Engine.MSAA = displayEl.readInt();
			else if (displayEl.name.equals("antialiasing"))
				Engine.AA = displayEl.readBoolean();
		}
		
		Display.destroy();
		Graphics.setupDisplay();
	}
	
	public static void loadLatestSavedGame() {
		loadSavedGame("res/lvl/saved_game.xml");
	}

	public static void saveGame() {
		// Setup root node and xml writer 
		XMLWriter xmlWriter = new XMLWriter("res/lvl/saved_game.xml");
		Node root = new Node();
		
		// Save camera
		Node camNode = new Node();
		camNode.name = "camera";
		camNode.children.add(new Node("radius", Float.toString(Graphics.camera.getRadius())));
		camNode.children.add(new Node("thetaX", Float.toString(Graphics.camera.getThetaX())));
		camNode.children.add(new Node("thetaY", Float.toString(Graphics.camera.getThetaY())));
		camNode.children.add(new Node("target", Graphics.camera.getTargetAsString()));
		
		// Save map
		Node mapNode = new Node();
		mapNode.name = "map";
		mapNode.children.add(new Node("file", map.file));
		mapNode.children.add(new Node("scale", Float.toString(map.scale)));
		mapNode.children.add(new Node("colorID", map.getColorIDAsString()));
		
		// Write top-level nodes to root
		root.children.add(camNode);
		root.children.add(mapNode);
		
		xmlWriter.write(root);
		xmlWriter.close();
	}
	/*
	 * Generate unique color IDs for use with object selection. The color ID of {0, 0, 0} is reserved for a 
	 * null selection (selection does not correspond to any entity)
	 */
	public static float[] getNextColorID() {
		if (lastColorID == null) {
			lastColorID = new float[3];
			
			lastColorID[0] = 1;
			lastColorID[1] = 0;
			lastColorID[2] = 0;
			
			return lastColorID;
		}
		else if (lastColorID[2] == 255) {
			return null;
		}
		else {
			lastColorID[0]++;
			if (lastColorID[0] > 255) {
				lastColorID[0] = 0;
				lastColorID[1]++;
			}
			if (lastColorID[1] > 255) {
				lastColorID[1] = 0;
				lastColorID[2]++;
			}
			return lastColorID;
		}
	}

	/*
	 * Convert the colorID generated above into a string for use as a key in the object selection hashmap (tried putting
	 * the raw array into the hashmap as the key, although the compiler doesn't complain, no value is returned... thus:
	 * strings!).
	 */
	public static String colorIDToStringKey(float[] colorID) {
		return Float.toString(colorID[0]) + Float.toString(colorID[1]) + Float.toString(colorID[2]);
	}
	
	public static void generateWorld() {
		int pineTreeCount = 0;//Physics.generator.nextInt() % map.height/4;
		int lollyTreeCount = 0;//Physics.generator.nextInt() % map.height/4;
		int berryBushCount = 0;//Physics.generator.nextInt() % map.height/4;
		
		if (pineTreeCount < 0) pineTreeCount *= -1;
		if (lollyTreeCount < 0) lollyTreeCount *= -1;
		if (berryBushCount < 0) berryBushCount *= -1;
		
		System.out.println("Drawing " + pineTreeCount + " pine trees");
		System.out.println("Drawing " + lollyTreeCount + " lolly trees");
		System.out.println("Drawing " + berryBushCount + " berry bushes");
		
		for (int i = 0; i < pineTreeCount; i++) {
			Entity entity = new Entity();
			entity.position = new Vector3f(((Physics.generator.nextFloat() * 2) - 1) * (map.width/2), 0, ((Physics.generator.nextFloat() * 2) - 1) * (map.height/2));
			entity.rotation = new float[] {0, Physics.generator.nextFloat() * 360, 0};
			entity.types.add(Type.gatherable);
			float tempScale =  (Physics.generator.nextFloat() + .25f) * .1f;
			entity.scale[0] = tempScale;
			entity.scale[1] = tempScale;
			entity.scale[2] = tempScale;
			entity.inventory.maxWeight = 100;
			entity.inventory.addItems(itemLibrary.get("WOOD"), 100);
			entities.add(entity);
		}
		
		for (int i = 0; i < lollyTreeCount; i++) {
			Entity entity = new Entity();
			entity.position = new Vector3f(((Physics.generator.nextFloat() * 2) - 1) * (map.width/2), 0, ((Physics.generator.nextFloat() * 2) - 1) * (map.height/2));
			entity.rotation = new float[] {0, Physics.generator.nextFloat() * 360, 0};
			entity.types.add(Type.gatherable);
			float tempScale =  (Physics.generator.nextFloat() + .25f) * .1f;
			entity.scale[0] = tempScale;
			entity.scale[1] = tempScale;
			entity.scale[2] = tempScale;			entity.inventory.maxWeight = 100;
			entity.inventory.addItems(itemLibrary.get("WOOD"), 100);
			entities.add(entity);
		}
		
		for (int i = 0; i < berryBushCount; i++) {
			Entity entity = new Entity();
			entity.position = new Vector3f(((Physics.generator.nextFloat() * 2) - 1) * (map.width/2), 0, ((Physics.generator.nextFloat() * 2) - 1) * (map.height/2));
			entity.rotation = new float[] {0, Physics.generator.nextFloat() * 360, 0};
			entity.types.add(Type.edible);
			float tempScale =  (Physics.generator.nextFloat() + .25f) * .1f;
			entity.scale[0] = tempScale;
			entity.scale[1] = tempScale;
			entity.scale[2] = tempScale;			entity.inventory.maxWeight = 30;
			entity.inventory.addItems(itemLibrary.get("BERRY"), 30);
			entities.add(entity);
		}
	}
}
