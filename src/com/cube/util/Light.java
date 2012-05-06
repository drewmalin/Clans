package com.cube.util;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;
import javax.vecmath.Vector4d;

public abstract class Light {

	protected Vector3f position; //Position of the light
	protected Vector4f ambient; //Ambient component color
	protected Vector4f diffuse; //Diffuse component color
	protected Vector4f specular; //Specular component color
	
	public Light() {
		//Default position is at the Origin
		position = new Vector3f(0.0f, 0.0f, 0.0f);
		//Default color is white, full alpha
		ambient = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		diffuse = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		specular = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	//Generic function for lights to 
	public abstract void doLogic();
	
	
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
//---------------------------GETTERS AND SETTERS----------------------------------
//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = new Vector3f(position);
	}
	
	public void setPosition(Vector3d position) {
		this.position = new Vector3f(position);
	}
	
	public void setPosition(float x, float y, float z) {
		this.position = new Vector3f(x, y, z);
	}
	
	public Vector4f getAmbient() {
		return ambient;
	}
	
	public void setAmbient(Vector4f ambient) {
		this.ambient = new Vector4f(ambient);
	}
	
	public void setAmbient(Vector4d ambient) {
		this.ambient = new Vector4f(ambient);
	}
	
	public void setAmbient(float r, float g, float b, float a) {
		this.ambient = new Vector4f(r, g, b, a);
	}
	
	public Vector4f getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = new Vector4f(diffuse);
	}
	
	public void setDiffuse(Vector4d diffuse) {
		this.diffuse = new Vector4f(diffuse);
	}
	
	public void setDiffuse(float r, float g, float b, float a) {
		this.diffuse = new Vector4f(r, g, b, a);
	}
	
	public Vector4f getSpecular() {
		return specular;
	}
	
	public void setSpecular(Vector4f specular) {
		this.specular = new Vector4f(specular);
	}
	
	public void setSpecular(Vector4d specular) {
		this.specular = new Vector4f(specular);
	}
	
	public void setSpecular(float r, float g, float b, float a) {
		this.specular = new Vector4f(r, g, b, a);
	}
	
}
