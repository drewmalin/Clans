package com.cube.core;

import java.util.ArrayList;

import com.cube.states.NeutralState;

public class Clan {
	
	public static ArrayList<Unit> units;
	public int id;
	public int farmerCount;
	public int builderCount;
	public int warriorCount;
	public int hunterCount;
	public int berryCount;
	public int meatCount;
	public float[] color;
	public float[] position;

	public static int FARMER = 10;
	public static int BUILDER = 11;
	public static int WARRIOR = 12;
	public static int HUNTER = 13;
	
	public Clan() {
		units = new ArrayList<Unit>();
		position = new float[3];
		farmerCount = 0;
		builderCount = 0;
		warriorCount = 0;
		hunterCount = 0;
		berryCount = 0;
		meatCount = 0;
	}
	
	public void draw() {
		for (Unit u : units) {
			if (u.show) {
				u.draw();
			}
		}
	}
	
	public void update(int timeElapsed) {
		for (Unit u : units) {
			if (u.show) {
				u.update(timeElapsed);
			}
		}
	}
	
	public void process() {
		Unit u;
		
		for (int i = 0; i < farmerCount; i++) {
			u = new Unit(FARMER, 1, this);
			u.inventory = 0;
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
		for (int i = 0; i < builderCount; i++) {
			u = new Unit(BUILDER, 1, this);
			u.inventory = 0;
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
		for (int i = 0; i < warriorCount; i++) {
			u = new Unit(WARRIOR, 1, this);
			u.inventory = 0;
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
		for (int i = 0; i < hunterCount; i++) {
			u = new Unit(HUNTER, 1, this);
			u.inventory = 0;
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
	}
}
