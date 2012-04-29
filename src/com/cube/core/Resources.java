package com.cube.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import javax.vecmath.Vector3d;



import com.cube.states.NeutralState;
import com.cube.util.FileLogger;
import com.cube.util.OBJParser;
import com.cube.util.Texture;
import com.cube.util.TextureLoader;
import com.cube.util.GeometryGroup;


public class Resources {
	
	private static FileInputStream fstream;
	private static DataInputStream in;
	private static BufferedReader br;
	private static float[] lastColorID;
	public static Map map;
	public static ArrayList<Light> lights;
	public static ArrayList<Clan> clans;
	public static ArrayList<Entity> entities;
	public static ArrayList<Texture> textures;
	
	public static ArrayList<Object> objectLibrary;
	public static HashMap<String, Item> itemLibrary;

	public static TextureLoader texLoader;
	public static HashMap<String, Entity> pickingHashMap;
	
	public static Texture selectionRing;

	public static void initialize() {
		
		entities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		clans = new ArrayList<Clan>();
		textures = new ArrayList<Texture>();
		texLoader = new TextureLoader();
		
		pickingHashMap = new HashMap<String, Entity>();

		map = new Map(100);

		FileLogger.logger.log(Level.INFO, "Resources initialized");
		objectLibrary = new ArrayList<Object>();
	}
	
	public static void loadLevel(String file) {
		////////////////////***<temporary location>***/////////////////////
		try {
			selectionRing = texLoader.getTexture("ring.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		////////////////////**</temporary location>***////////////////////

		try {
			//sound = new Sound();
			fstream = new FileInputStream(file);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			parse(br);
			for (Clan c : clans) {
				c.process();
			}
			generateWorld();
			in.close();
			//if (sound.soundCount() > 0) sound.create();
		} catch (Exception e) {
			System.err.println("Error loading level: " + e.getMessage());
			e.printStackTrace();
		}
		//Graphics.loadCamera();
	}
	
	public static void parse(BufferedReader br) throws IOException {
		String strLine;
		
		while ((strLine = br.readLine()) != null) {
			
			// Check for comments
			if (strLine.contains("<!--")) {
				while (!strLine.contains("-->")) {
					strLine = br.readLine();
				}
				continue;
			}
			// Check for blank lines
			if (strLine.isEmpty()) {
				continue;
			}

			if (strLine.equals("<clan>")) {
				Clan clan = new Clan();
				while (!(strLine = br.readLine().trim()).equals("</clan>")) {
					if (readElementName(strLine).equals("id")) {
						clan.id = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("meat")) {
						clan.meatCount = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("berry")) {
						clan.berryCount = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("farmer")) {
						clan.farmerCount = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("hunter")) {
						clan.hunterCount = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("builder")) {
						clan.builderCount = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("warrior")) {
						clan.warriorCount = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("color")) {
						clan.color = parseFloatArray(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("position")) {
						clan.position = parseVector3d(readElementValue(strLine));
					}
				}
				clans.add(clan);
			}
			if (strLine.equals("<map>")) {
				while (!(strLine = br.readLine().trim()).equals("</map>")) {
					if (readElementName(strLine).equals("file")) {
						map.file = readElementValue(strLine);
					}
					if (readElementName(strLine).equals("scale")) {
						map.scale = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("colorID")) {
						map.colorID = parseIntArray(readElementValue(strLine));
					}
				}
				map.initialize();
			}
			if (strLine.equals("<effect>")) {
				Effect effect = new Effect();
				while (!(strLine = br.readLine().trim()).equals("</effect>")) {
					if (readElementName(strLine).equals("position")) {
						effect.position = parseFloatArray(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("scale")) {
						effect.scale = parseFloatArray(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("count")) {
						effect.particleCount = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("color")) {
						effect.color = parseFloatArray(readElementValue(strLine));
					}
				}
				effect.initializeParticles();
				Physics.effects.add(effect);
			}
			/* Objects in the world */
			if (strLine.equals("<entity>")) {
				Entity entity = new Entity();
				while (!(strLine = br.readLine().trim()).equals("</entity>")) {
					if (readElementName(strLine).equals("id")) {
						entity.objectID = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("position")) {
						entity.position = parseVector3d(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("scale")) {
						entity.scale = Float.parseFloat(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("rotation")) {
						entity.rotation = parseFloatArray(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("type")) {
						entity.type = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("targets")) {
						parseList(entity, readElementValue(strLine));
					}
					if (readElementName(strLine).equals("maxvelocity")) {
						entity.max_v = Float.parseFloat(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("resources")) {
						entity.inventory.setCap(Integer.parseInt(readElementValue(strLine)));
						entity.inventory.fill();
					}
				}
				entity.currentState = NeutralState.getState();
				entity.startState();
				entities.add(entity);
			}
			/* Textures */
			if (strLine.equals("<texture>")) {
				while (!(strLine = br.readLine().trim()).equals("</texture>")) {
					if (readElementName(strLine).equals("file")) {
						textures.add(texLoader.getTexture(readElementValue(strLine)));
					}
				}
			}
			/* Add models to the model library */
			if (strLine.equals("<lib>")) {
				Object object = new Object();
				while (!(strLine = br.readLine().trim()).equals("</lib>")) {
					if (readElementName(strLine).equals("id")) {
						object.id = Integer.parseInt(readElementValue(strLine));
					}
					if (readElementName(strLine).equals("file")) {
						object.file = readElementValue(strLine);
					}
				}
				loadLocalFile(object.file, object);
				objectLibrary.add(object);
			}
		}
	}
	
	private static void parseList(Entity entity, String line) {
		String[] tokens = line.split("\\s");
		
		for (String token : tokens) {
			entity.targets.add(Integer.parseInt(token));
		}
	}

	public static void loadLocalFile(String filename, Object we) {
		try {
			OBJParser parser = new OBJParser(filename);
			we.vertexArray = parser.v;
			we.vertexNormalArray = parser.vn;
			we.textureArray = parser.t;
			for(GeometryGroup gg : parser.ggs) {
				we.geoGroups.add(gg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readElementName(String s) {
		return s.substring(s.indexOf('<')+1, s.indexOf('>'));
	}
	public static String readElementValue(String s) {
		return s.substring(s.indexOf('>')+1, s.lastIndexOf('<'));
	}
	
	/* Function to read in 3 space-separated floats (e.g. 8.1 3.7 9.9) and store them in a float array
	 * (e.g. ar[3] = {8.1, 3.7, 9.9}
	 */
	public static float[] parseFloatArray(String s) {
		int idx = 0;
		float ret[] = new float[3];
		String[] tokens = s.split("\\s");

		for (String token : tokens) {
			ret[idx++] = Float.parseFloat(token);
		}
		return ret;
	}
	
	/* Function to read in 3 space-separated ints (e.g. 8 3 9) and store them in an int array
	 * (e.g. ar[3] = {8, 3, 9}
	 */
	public static int[] parseIntArray(String s) {
		int idx = 0;
		int ret[] = new int[3];
		String[] tokens = s.split("\\s");
		
		for (String token : tokens) {
			ret[idx++] = Integer.parseInt(token);
		}
		return ret;
	}
	
	/*
	 * Function to read in 3 space-separated doubles (e.g. 4.1 5.6 2.8) and store them in a Vector3d object
	 * (e.g. Vector3d = (4.1, 5.6, 2.8)
	 */
	public static Vector3d parseVector3d(String s) {
		Vector3d ret = new Vector3d();
		String[] tokens = s.split("\\s");

		ret.x = Double.parseDouble(tokens[0]);
		ret.y = Double.parseDouble(tokens[1]);
		ret.z = Double.parseDouble(tokens[2]);

		return ret;
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
			entity.objectID = 8;
			entity.position = new Vector3d(((Physics.generator.nextDouble() * 2) - 1) * (map.width/2), 0, ((Physics.generator.nextDouble() * 2) - 1) * (map.height/2));
			entity.rotation = new float[] {0, Physics.generator.nextFloat() * 360, 0};
			entity.type = Entity.GATHERABLE;
			entity.scale = (Physics.generator.nextFloat() + .25f) * .1f;
			entities.add(entity);
		}
		
		for (int i = 0; i < lollyTreeCount; i++) {
			Entity entity = new Entity();
			entity.objectID = 6;
			entity.position = new Vector3d(((Physics.generator.nextDouble() * 2) - 1) * (map.width/2), 0, ((Physics.generator.nextDouble() * 2) - 1) * (map.height/2));
			entity.rotation = new float[] {0, Physics.generator.nextFloat() * 360, 0};
			entity.type = Entity.GATHERABLE;
			entity.scale = (Physics.generator.nextFloat() + .25f) * .1f;
			entities.add(entity);
		}
		
		for (int i = 0; i < berryBushCount; i++) {
			Entity entity = new Entity();
			entity.objectID = 11;
			entity.position = new Vector3d(((Physics.generator.nextDouble() * 2) - 1) * (map.width/2), 0, ((Physics.generator.nextDouble() * 2) - 1) * (map.height/2));
			entity.rotation = new float[] {0, Physics.generator.nextFloat() * 360, 0};
			entity.type = Entity.EDIBLE;
			entity.scale = (Physics.generator.nextFloat() + .25f) * .1f;
			entities.add(entity);
		}
	}
}
