package com.cube.core;

import java.util.ArrayList;

import com.cube.states.NeutralState;

public class Clan {
	
	public static ArrayList<Unit> units;
	public int id;
	public int farmer;
	public int builder;
	public int warrior;
	public int hunter;
	public int berry;
	public int meat;
	public float[] color;
	
	public Clan() {
		units = new ArrayList<Unit>();
		farmer = 0;
		builder = 0;
		warrior = 0;
		hunter = 0;
		berry = 0;
		meat = 0;
	}
	
	public void draw() {
		for (Unit u : units) {
			if (u.show) {
				u.draw();
			}
		}
	}
	
	public void update() {
		for (Unit u : units) {
			if (u.show) {
				u.update();
			}
		}
	}
	
	public void process() {
		Unit u;
		
		for (int i = 0; i < farmer; i++) {
			u = new Unit(1);
			units.add(u);
		}
		for (int i = 0; i < builder; i++) {
			u = new Unit(1);
			units.add(u);
		}
		for (int i = 0; i < warrior; i++) {
			u = new Unit(1);
			units.add(u);
		}
		for (int i = 0; i < hunter; i++) {
			u = new Unit(1);
			u.currentState = NeutralState.getState();
			u.startState();
			units.add(u);
		}
	}
}
