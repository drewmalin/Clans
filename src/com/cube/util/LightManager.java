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
		calculateStaticLightTexture();
	}

	public void addLight(DynamicLight light) {
		dynamicLights.add(light);
		calculateDynamicLightTexture();
	}
	
	public void removeLight(StaticLight light) {
		staticLights.remove(light);
		calculateStaticLightTexture();
	}
	
	public void removeLight(DynamicLight light) {
		dynamicLights.remove(light);
		calculateDynamicLightTexture();
	}
	
	public void calculateStaticLightTexture() {
		//TODO: Clear the current texture
		for (StaticLight light: staticLights) {
			//TODO: Add all the static light data to the texture
		}
	}
	
	public void calculateDynamicLightTexture() {
		//TODO: Clear the current texture
		for (DynamicLight light: dynamicLights) {
			//TODO: Add all the dynamic light data to the texture
		}
	}
}
