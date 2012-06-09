package com.cube.core;

public class Item {
	
	public int ID;
	public float weight;
	public String name;
	public boolean equipable;
	
	public Item(String n, float w) {
		name = n;
		weight = w;
	}

	public Item() {
		ID = -1;
		weight = -1;
		name = "";
		equipable = false;
	}
}