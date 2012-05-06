package com.cube.util;

import java.util.ArrayList;

public class LightManager {
	
	private ArrayList<StaticLight> staticLights;
	private ArrayList<DynamicLight> dynamicLights;
	
	public LightManager() {
		staticLights = new ArrayList<StaticLight>();
		dynamicLights = new ArrayList<DynamicLight>();
	}

	public void addLight(StaticLight light) {
		staticLights.add(light);
	}

	public void addLight(DynamicLight light) {
		dynamicLights.add(light);
	}
}
