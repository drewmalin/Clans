package com.cube.util;

public class Quad {
	public float a;
	public float b;
	public float c;
	public float d;
	
	public Quad(float _a, float _b, float _c, float _d) {
		a = _a;
		b = _b;
		c = _c;
		d = _d;
	}
	
	public String toString() {
		String outString;
		outString = ("A: " + a + ", " +
				     "B: " + b + ", " +
				     "C: " + c + ", " +
				     "D: " + d);
		return outString;
	}
}
