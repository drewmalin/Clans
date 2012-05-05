package com.cube.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector3d;

public class Utilities {
	/*
	 * Method to neatly print the contents of a float array.
	 */
	public static String printArray(float[] array) {
		String ret = "(";
		
		for (float a : array) {
			ret += (a + ", ");
		}
		
		ret = ret.substring(0, ret.lastIndexOf(","));
		return ret + ")";
	}
	
	/*
	 * Method to neatly print the contents of a Vector3d.
	 */
	public static String printVector(Vector3d position) {
		return "(" + position.x + ", " + position.y + ", " + position.z + ")";
	}
	
	/*
	 * Method to neatly print the contents of an ArrayList.
	 */
	public static String printArrayList(ArrayList<Integer> types) {
		String ret = "(";
		for (Integer i : types) {
			ret += (i + ", " );
		}
		ret = ret.substring(0, ret.lastIndexOf(","));
		return ret + ")";
	}
	/*
	 * Method to test if the first collection, primary, contains any of the objects in the
	 * second collection, test.
	 */
	public static boolean containsAny(ArrayList<Integer>  targets, ArrayList<Integer> types) {
		for (Integer i : types) {
			if (targets.contains(i))
				return true;
		}
		return false;
	}
}