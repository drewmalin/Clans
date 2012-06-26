package com.cube.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

/** LightManager Class:
 * 
 * This class has the responsibility of maintaining Static and Dynamic Lights
 * in the environment. Lights are stored in lists and sent to the GPU via textures.
 * The reason we separate lights into Static and Dynamic lists, is so that we don't
 * need to unnecessarily process Static light data unless we add or remove Static lights.
 * Where dynamic lights will have a component that changes, whether it is position or
 * light color, or both.
 * 
 * @author Jack Ramey
 *
 */

public class LightManager {
	
	private int staticLightTextureID = 0;
	private int dynamicLightTextureID = 0;
	
	private ArrayList<StaticLight> staticLights;
	private ArrayList<DynamicLight> dynamicLights;
	
	public LightManager() {
		staticLights = new ArrayList<StaticLight>();
		dynamicLights = new ArrayList<DynamicLight>();
		
		staticLightTextureID = createTextureID(); //Create a texture ID for the static light texture
		dynamicLightTextureID = createTextureID(); //Create a texture ID for the dynamic light texture
	}

	//Add a static light to the Static Light list
	public void addLight(StaticLight light) {
		staticLights.add(light);
		//Recalculate the texture after we add a light
		calculateStaticLightTexture();
	}

	//Add a dynamic light to the Dynamic Light list
	public void addLight(DynamicLight light) {
		dynamicLights.add(light);
		//Recalculate the texture after we add a light
		calculateDynamicLightTexture();
	}
	
	public void removeLight(StaticLight light) {
		staticLights.remove(light);
		//Recalculate the texture after we remove a light
		calculateStaticLightTexture();
	}
	
	public void removeLight(DynamicLight light) {
		dynamicLights.remove(light);
		//Recalculate the texture after we remove a light
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

//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
//--------------------------------UTILITIES---------------------------------------
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
	
    protected IntBuffer createIntBuffer(int size) {
        ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
        temp.order(ByteOrder.nativeOrder());

        return temp.asIntBuffer();
      }    

    private int createTextureID() 
    { 
       IntBuffer tmp = createIntBuffer(1); 
       GL11.glGenTextures(tmp); 
       return tmp.get(0);
    } 
}
