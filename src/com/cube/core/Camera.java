package com.cube.core;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float position[];
	private float target[];
	private float angle[];
	private float up[];
	private float radius;
	private float height;
	private float theta;
	private float thetaX;
	private float thetaY;
	private float direction[];
	                      
	private static float ZOOM_IN = 2;
	private static float ZOOM_OUT = 60;
	private static float ZOOM_SPEED = 0.5f;
	
	public Camera() {
		position 		= new float[3];
		target 			= new float[3];
		angle 			= new float[3];
		up 				= new float[3];
		direction	 	= new float[3];
		radius 			= 0f;
		height 			= 0f;
		theta 			= 0f;
		thetaX 			= 0f;
		thetaY 			= 0f;
	}

	/*
	 * The camera is initially supplied with a target (x,y,z), an 'up' vector, a radius (straight-line
	 * distance from the target to the camera position), and a rotation angle (angle between 0 and 360
	 * that defines the camera's rotation about the target. As the actual position of the camera is 
	 * determined programatically using these inputs, we need to calculate that position.
	 */
	public void updatePosition() {
		float x, y, z;

		direction[0] = (float) (Math.sin(thetaY / 180 * Math.PI) * (Math.cos(thetaX / 180 * Math.PI)));
		direction[1] = (float) (Math.sin(thetaX / 180 * Math.PI));
		direction[2] = (float) (Math.cos(thetaY / 180 * Math.PI) * (Math.cos(thetaX / 180 * Math.PI)));
			
		x = (float) (target[0] + radius * direction[0]);
		y = (float) (target[1] + radius * direction[1]);
		z = (float) (target[2] + radius * direction[2]);
		
		setPosition(x, y, z);

	}
	
	public void setTheta(float t) {
		theta = t;
	}
	
	public void setThetaX(float t) {
		thetaX = t;
	}
	
	public void changeThetaX(float t) {
		thetaX += t;
		
		thetaX = (thetaX > 90 ? 90 : thetaX < 0 ? 0 : thetaX);
	}
	
	public void changeThetaY(float t) {
		thetaY += t;
	}
	
	public void setThetaY(float t) {
		thetaY = t;
	}
	
	public void modifyRadius(float r) {
		radius += r * ZOOM_SPEED;
		if (radius < ZOOM_IN || radius > ZOOM_OUT)
			radius -= r * ZOOM_SPEED;
	}
	public void modifyTheta(float t) {
		theta += t;
		if (theta < -1.5 || theta > 1.5)
			theta -= t;

	}
	public void setRadius(float r) {
		radius = r;
	}
	
	public void setHeight(float h) {
		height = h;
	}
	
	public void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}
	
	public void setTarget(float x, float y, float z) {
		target[0] = x;
		target[1] = y;
		target[2] = z;
	}
	
	public void setAngle(float x, float y, float z) {
		angle[0] = x;
		angle[1] = y;
		angle[2] = z;
	}

	public void setUp(float x, float y, float z) {
		up[0] = x;
		up[1] = y;
		up[2] = z;
	}
	
	public float getPosition(int index) {
		assert ((index >= 0) && index <3);
		return position[index];
	}
	
	public float getTarget(int index) {
		assert ((index >= 0) && index <3);
		return target[index];
	}
	
	public float getAngle(int index) {
		assert ((index >= 0) && index <3);
		return angle[index];
	}
	
	public float getUp(int index) {
		assert ((index >= 0) && index <3);
		return up[index];
	}

	public void changePositionX(float delta) {
		position[0] += delta;
	}
	
	public void changePositionY(float delta) {
		position[1] += delta;
	}
	
	public void changePositionZ(float delta) {
		position[2] += delta;
	}

	public void setPositionY(float val) {
		position[1] = val;
	}

	public void changeTargetZ(float d) {
		target[2] = d;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public float getHeight() {
		return height;
	}
	public float getTheta() {
		return theta;
	}

	
	public void panForward(float x) {
		target[0] += direction[0] * x;
		target[2] += direction[2] * x;

		position[0] += direction[0] * x;
		position[2] += direction[2] * x;
	}
	
	public void panRight(float x) {
		Vector3f 	newVec 		= new Vector3f(0f, 0f, 0f);
		Vector3f 	up 			= new Vector3f(0f, 1f, 0f);
		Vector3f 	targetVec 	= new Vector3f(direction[0], direction[1], direction[2]);
		
		Vector3f.cross(targetVec, up, newVec);

		target[0] += newVec.x * x;
		target[2] += newVec.z * x;

		position[0] += newVec.x * x;
		position[2] += newVec.z * x;
	}
}
