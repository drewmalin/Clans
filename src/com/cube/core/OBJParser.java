package com.cube.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class OBJParser {

	private FileInputStream fstream;
	private DataInputStream in;
	private BufferedReader br;
	
	public ArrayList<Vertex> v;
	public ArrayList<Vertex> vn;
	public ArrayList<Texture> t;
	public ArrayList<PolyFace> f;
	
	public Vertex vertex;
	public Texture texture;
	public PolyFace polyface;
	
	public float maxX = -1000000;
	public float maxY = -1000000;
	public float maxZ = -1000000;
	public float minX = 1000000;
	public float minY = 1000000;
	public float minZ = 1000000;
	/*
	 * Only process line if it contains data
	 * Strings beginning with # or g are skipped
	 * Strings beginning with a v are vertices
	 * Strings beginning with vt are texture vertices
	 * Strings beginning with vn are normal vertices
	 * Strings beginning with f are polygon faces
	 * Treat anything remaining as junk
	 */
	public OBJParser(String str) {
		v = new ArrayList<Vertex>();
		vn = new ArrayList<Vertex>();
		t = new ArrayList<Texture>();
		f = new ArrayList<PolyFace>();
		
		try {
			fstream = new FileInputStream(str);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			parse(br);
			in.close();
			
		} catch (Exception e) {
			System.err.println("Error loading model: " + e.getMessage());
		}
		
		/*
		System.out.println("Results: " );
		System.out.println("v: " + v.size());
		System.out.println("vn: " + vn.size());
		System.out.println("t: " + t.size());
		System.out.println("f: " + f.size());
		*/
	}
	
	public void updateBoundingBox(Vertex v) {
		
		if (v.x > maxX) maxX = v.x;
		if (v.y > maxY) maxY = v.y;
		if (v.z > maxZ) maxZ = v.z;
		
		if (v.x < minX) minX = v.x;
		if (v.y < minY) minY = v.y;
		if (v.z < minZ) minZ = v.z;
	}
	
	public void parse(BufferedReader br) throws IOException {

		String strLine, flag;
		
		while ((strLine = br.readLine()) != null) {
 
			if (strLine.length() > 1) {
				flag = strLine.substring(0, 2);
				
				if (flag.equals("# ") || flag.equals("g ")) {
					continue;
				}
				else if (flag.equals("v ")) {
					vertex = new Vertex(strLine);
					v.add(vertex);
					updateBoundingBox(vertex);
				}
				else if (flag.equals("vt")) {
					texture = new Texture(strLine);
					t.add(texture);
				}
				else if (flag.equals("vn")) {
					vertex = new Vertex(strLine);
					vn.add(vertex);
				}
				else if (flag.equals("f ")) {
					polyface = new PolyFace();
					
					if (t.size() == 0 && vn.size() == 0) {
						polyface.addVertex(strLine);
					}
					else if (t.size() == 0) {
						polyface.addVertexAndNormal(strLine);
					}
					else if (vn.size() == 0) {
						polyface.addVertexAndTexture(strLine);
					}
					else {
						polyface.addVertexAndTextureAndNormal(strLine);
					}
					polyface.calcNormals();
					f.add(polyface);
				}
				else {
					continue;
				}
			}
		}
	}
}