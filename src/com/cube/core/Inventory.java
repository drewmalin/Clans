package com.cube.core;

public class Inventory {
	
	private int capacity;
	private int itemCount;
	
	public Inventory() {
		capacity = itemCount = 0;
	}
	
	public boolean isFull() {
		return (itemCount == capacity);
	}
	
	public void addItem() {
		itemCount++;
	}
	
	public void setCap(int c) {
		capacity = c;
	}
}