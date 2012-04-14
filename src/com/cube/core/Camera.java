package com.cube.core;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float position[];
	private float target[];
	private float up[];
	private float direction[];

	private float radius;
	private float thetaX;
	private float thetaY;
	                      
	private static float ZOOM_IN 	= 2;
	private static float ZOOM_OUT 	= 60;
	private static float ZOOM_SPEED = 0.5f;
	
	public Camera() {
		position 		= new float[3];
		target 			= new float[3];
		up 				= new float[3];
		direction	 	= new float[3];
		
		radius 			= 0f;
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
	
	/*
	 * The camera's translational movement is controlled through panning. This method ensures that the
	 * camera's target pans across the world in lock-step with the camera itself, to avoid any sort of
	 * angular change in the camera's direction. Note: negating the panForward parameter causes the
	 * camera to pan backwards.
	 */
	public void panForward(float x) {
		target[0] += direction[0] * x;
		target[2] += direction[2] * x;

		position[0] += direction[0] * x;
		position[2] += direction[2] * x;
	}
	
	/*
	 * Similar to panning forward, panRight maintains movement equality with the camera's target. Panning
	 * left and right necessitates determining the vector to the left and right of the camera, so the cross
	 * product of the up vector and the current direction is taken. Note: negating the panRight parameter
	 * causes the camera to pan left.
	 */
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
	
	/*
	 * ThetaX represents the camera's rotation about its local x-axis. 
	 */
	public void setThetaX(float t) {
		thetaX = t;
	}
	
	/*
	 * ThetaY represents the camera's rotation about its local y-axis.
	 */
	public void setThetaY(float t) {
		thetaY = t;
	}
	
	/*
	 * The value of thetaX is changed slowly by the user's input. The saved value is capped at 90 (or 89.99...)
	 * so that the camera does not flip about its y-axis at thetaX values greater than 90.
	 */
	public void changeThetaX(float t) {
		thetaX += t;
		
		thetaX = (thetaX > 90 ? 89.99f : thetaX < 0 ? 0 : thetaX);
	}
	
	/*
	 * The value of thetaY is changed slowly by the user's input. The value is always kept between 0 and 360 for
	 * debugging purposes (as it's not immediately easy to translate 3214 degrees into anything meaningful).
	 */
	public void changeThetaY(float t) {
		thetaY += t;
		
		thetaY = (thetaY > 360 ? thetaY - 360 : thetaY < 0 ? thetaY + 360 : thetaY);

	}
	
	/*
	 * The camera's radius is the straight-line distance from the camera position to the camera's target. Scrolling
	 * in and out increases and decreases this radius. The ZOOM_SPEED modifier controls the value of each incremental
	 * change to the radius, which ZOOM_IN and ZOOM_OUT serve as minimum and maximum zoom values.
	 */
	public void modifyRadius(float r) {
		radius += r * ZOOM_SPEED;
		if (radius < ZOOM_IN || radius > ZOOM_OUT)
			radius -= r * ZOOM_SPEED;
	}
	
	/*
	 * Sets the initial radius value. For use in camera initialization.
	 */
	public void setRadius(float r) {
		radius = r;
	}
	
	/*
	 * Sets the final position of the camera in world coordinates. For use internally to the Camera class within the
	 * updatePosition method.
	 */
	private void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}
	
	/*
	 * Sets the initial target of the camera (the point in world coordinates towards which the camera will be directed).
	 * For use in camera initialization.
	 */
	public void setTarget(float x, float y, float z) {
		target[0] = x;
		target[1] = y;
		target[2] = z;
	}

	/*
	 * Sets the vector that will be used to signify 'up' for the camera. This has the effect of setting the orientation
	 * of the camera. For use primarily in camera initialization.
	 */
	public void setUp(float x, float y, float z) {
		up[0] = x;
		up[1] = y;
		up[2] = z;
	}
	
	/*
	 * Returns the world position of the camera.
	 */
	public float getPosition(int index) {
		assert ((index >= 0) && index <3);
		return position[index];
	}
	
	/*
	 * Returns the world position of the camera's target.
	 */
	public float getTarget(int index) {
		assert ((index >= 0) && index <3);
		return target[index];
	}
	
	/*
	 * Returns the up value for the camera's orientation. For use in camera rendering.
	 */
	public float getUp(int index) {
		assert ((index >= 0) && index <3);
		return up[index];
	}
}
