package com.cube.core;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class Camera {
	
	private Vector3f target;
	private Vector3f up;
	private Vector3f position;
	private Vector2f direction;
	
	private float radius;
	private float thetaX;
	private float thetaY;
	                  	
	private static float ZOOM_IN 	= 2;
	private static float ZOOM_OUT 	= 400;
	private static float ZOOM_SPEED = 0.5f;
	
	public Camera() {
		target 			= new Vector3f(0, 0, 0);
		up		 		= new Vector3f(0, 0, 0);
		position 		= new Vector3f(0, 0, 0);
		direction 		= new Vector2f(0, 0);
		
		
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
		
		position.x = (float) (Math.sin(thetaY / 180 * Math.PI) * (Math.cos(thetaX / 180 * Math.PI)));
		position.y = (float) (Math.sin(thetaX / 180 * Math.PI));
		position.z = (float) (Math.cos(thetaY / 180 * Math.PI) * (Math.cos(thetaX / 180 * Math.PI)));		
		position.normalize();
		
		direction.x = position.x;
		direction.y = position.z;
		direction.normalize();
		
		position.scale(radius);
		position.add(target);
		
		// Make sure the camera can't pass through the map
		float worldPos = Resources.map.getHeight((float)position.x, (float)position.z) + 1;
		if (position.y < worldPos) position.y = worldPos;
		
		setPosition(position.x, position.y, position.z);

	}
	
	/*
	 * The camera's translational movement is controlled through panning. This method ensures that the
	 * camera's target pans across the world in lock-step with the camera itself, to avoid any sort of
	 * angular change in the camera's direction. Note: negating the panForward parameter causes the
	 * camera to pan backwards.
	 */
	public void panForward(float x) {

		target.x += direction.x * x;
		target.z += direction.y * x;

		position.x += direction.x * x;
		position.z += direction.y * x;
	}
	
	/*
	 * Similar to panning forward, panRight maintains movement equality with the camera's target. Panning
	 * left and right necessitates determining the vector to the left and right of the camera, so the cross
	 * product of the up vector and the current direction is taken. Note: negating the panRight parameter
	 * causes the camera to pan left.
	 */
	public void panRight(float x) {
		Vector3f 	newVec 		= new Vector3f(0, 0, 0);
		Vector3f 	up 			= new Vector3f(0, 1, 0);
		Vector3f 	targetVec 	= new Vector3f(direction.x, 0, direction.y);
		
		newVec.cross(targetVec, up);
		
		target.x += newVec.x * x;
		target.z += newVec.z * x;

		position.x += newVec.x * x;
		position.z += newVec.z * x;
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
	private void setPosition(double x, double y, double z) {	
		position.x = (float)x;
		position.y = (float)y;
		position.z = (float)z;
	}
	
	/*
	 * Sets the initial target of the camera (the point in world coordinates towards which the camera will be directed).
	 * For use in camera initialization.
	 */
	public void setTarget(float x, float y, float z) {
		target.x = x;
		target.y = y;
		target.z = z;
	}

	/*
	 * Sets the vector that will be used to signify 'up' for the camera. This has the effect of setting the orientation
	 * of the camera. For use primarily in camera initialization.
	 */
	public void setUp(float x, float y, float z) {
		up.x = x;
		up.y = y;
		up.z = z;
	}
	
	/*
	 * Returns the world position of the camera.
	 */
	public float getPosition(int index) {
		assert ((index >= 0) && index <3);
		switch (index) {
		case 0:
			return position.x;
		case 1:
			return position.y;
		case 2:
			return position.z;
		default:
			return 0;
	}	}
	
	/*
	 * Returns the world position of the camera's target.
	 */
	public float getTarget(int index) {
		assert ((index >= 0) && index <3);
		
		switch (index) {
			case 0:
				return target.x;
			case 1:
				return target.y;
			case 2:
				return target.z;
			default:
				return 0;
		}
	}
	
	/*
	 * Returns the up value for the camera's orientation. For use in camera rendering.
	 */
	public float getUp(int index) {
		assert ((index >= 0) && index <3);
		switch (index) {
			case 0:
				return up.x;
			case 1:
				return up.y;
			case 2:
				return up.z;
			default:
				return 0;	
		}
	}
	public float[] getPosition() {
		float[] ret = new float[3];
		ret[0] = position.x;
		ret[1] = position.y;
		ret[2] = position.z;
		return ret;
	}
}
