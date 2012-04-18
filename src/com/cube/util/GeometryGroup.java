package com.cube.util;

import java.util.ArrayList;

public class GeometryGroup {

	public static final int STATIC = 0;
	public static final int GAME_ANIMATED = 1;
	public static final int PROCEDURAL_ANIMATED = 2;
	public int status;
	public ArrayList<PolyFace> f;
	
	public GeometryGroup() {
		status = STATIC;
		f = new ArrayList<PolyFace>();
	}
}
