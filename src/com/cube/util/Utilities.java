package com.cube.util;

import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.vecmath.Vector3d;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;

import com.cube.core.Graphics;

public class Utilities {
	
	private static long currentTime;
	public static int frameRate;
	
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
	 * 
	 * This is primarily meant to be used to test whether a particular entity can interact
	 * with another. The list 'primary' will contain all of the first entity's possible targets
	 * (possible types of entity it can interact with) while the second list, 'test', will
	 * contain all of the types that define the second entity.
	 */
	public static boolean containsAny(ArrayList<Integer>  primary, ArrayList<Integer> test) {
		for (Integer i : test) {
			if (primary.contains(i))
				return true;
		}
		return false;
	}
	
	public static void updateFrameRate() {
		long delta = System.currentTimeMillis() - currentTime;
		if (delta >= 1000) {
			frameRate = Graphics.frameCount;
			Graphics.frameCount = 0;
			currentTime = System.currentTimeMillis();
		}		
	}
	
	public static int createVBOID() {
		 IntBuffer buffer = BufferUtils.createIntBuffer(1);
		 ARBVertexBufferObject.glGenBuffersARB(buffer);
		 return buffer.get(0);
	}
}