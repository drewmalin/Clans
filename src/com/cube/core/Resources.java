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
	public static Object[] objectLibrary;
	public static TextureLoader texLoader;
	public static HashMap<float[], Entity> pickingHashMap;

	public static void initialize() {
		
		entities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		clans = new ArrayList<Clan>();
		textures = new ArrayList<Texture>();
		texLoader = new TextureLoader();
		
		pickingHashMap = new HashMap<float[], Entity>();

		map = new Map(100);
		
		FileLogger.logger.log(Level.INFO, "Resources initialized");
	}
	
	public static void loadLevel(String file) {
		////////////////////***<temporary location>***/////////////////////
		/*
		try{
			textures.add(texLoader.getTexture("brookstoneFTW.png"));
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}catch (IOException e)
		{
			FileLogger.logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		*/
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
			if (strLine.equals("<info>")) {
				while (!(strLine = br.readLine().trim()).equals("</info>")) {
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("libcount")) {
						int libcount = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
						objectLibrary = new Object[libcount];
					}
				}
			}
			if (strLine.equals("<clan>")) {
				Clan clan = new Clan();
				while (!(strLine = br.readLine().trim()).equals("</clan>")) {
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("id")) {
						clan.id = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("meat")) {
						clan.meatCount = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("berry")) {
						clan.berryCount = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("farmer")) {
						clan.farmerCount = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("hunter")) {
						clan.hunterCount = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("builder")) {
						clan.builderCount = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("warrior")) {
						clan.warriorCount = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("color")) {
						clan.color = parseFloatArray(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("position")) {
						clan.position = parseFloatArray(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
				}
				clans.add(clan);
			}
			if (strLine.equals("<map>")) {
				while (!(strLine = br.readLine().trim()).equals("</map>")) {
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("file")) {
						map.file = strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<'));
						//loadLocalFile(map.file, map);
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("scale")) {
						map.scale = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("colorID")) {
						map.colorID = parseIntArray(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
				}
			}
			if (strLine.equals("<effect>")) {
				Effect effect = new Effect();
				while (!(strLine = br.readLine().trim()).equals("</effect>")) {
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("position")) {
						effect.position = parseFloatArray(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("scale")) {
						effect.scale = parseFloatArray(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("count")) {
						effect.particleCount = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("color")) {
						effect.color = parseFloatArray(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
				}
				effect.initializeParticles();
				Physics.effects.add(effect);
			}
			/* Objects in the world */
			if (strLine.equals("<entity>")) {
				Entity entity = new Entity();
				while (!(strLine = br.readLine().trim()).equals("</entity>")) {
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("id")) {
						entity.objectID = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("position")) {
						entity.position = parseVector3d(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("scale")) {
						entity.scale = Float.parseFloat(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("rotation")) {
						entity.rotation = parseFloatArray(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("type")) {
						entity.type = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("resources")) {
						entity.inventory.setCap(Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<'))));
						entity.inventory.fill();
					}
				}
				entities.add(entity);
			}
			/* Textures */
			if (strLine.equals("<texture>")) {
				while (!(strLine = br.readLine().trim()).equals("</texture>")) {
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("file")) {
						textures.add(texLoader.getTexture(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<'))));
					}
				}
			}
			/* Add models to the model library */
			if (strLine.equals("<lib>")) {
				Object object = new Object();
				while (!(strLine = br.readLine().trim()).equals("</lib>")) {
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("id")) {
						object.id = Integer.parseInt(strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<')));
					}
					if (strLine.substring(strLine.indexOf('<')+1,strLine.indexOf('>')).equals("file")) {
						object.file = strLine.substring(strLine.indexOf('>')+1,strLine.lastIndexOf('<'));
					}
				}
				loadLocalFile(object.file, object);
				objectLibrary[object.id] = object;
			}
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
	
	public static Vector3d parseVector3d(String s) {
		Vector3d ret = new Vector3d();
		String[] tokens = s.split("\\s");

		ret.x = Double.parseDouble(tokens[0]);
		ret.y = Double.parseDouble(tokens[1]);
		ret.z = Double.parseDouble(tokens[2]);

		return ret;
	}
	
	public static float[] getNextColorID() {
		if (lastColorID == null) {
			lastColorID = new float[3];
			lastColorID[0] = lastColorID[1] = lastColorID[2] = 0;
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
}
