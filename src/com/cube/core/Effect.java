package com.cube.core;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;

public class Effect {
	public int particleCount;
	private ArrayList<Particle> particles;
	private Random randGenerator;
	
	//private float xPosition, yPosition, zPosition;
	public float position[];
	public float color[];
	public float scale[];
	//private float R, G, B;
	//private float scaleX, scaleY, scaleZ;
	private float deceleration;
	
	public Effect() {
		randGenerator = new Random();
		particles = new ArrayList<Particle>();
	}
	
	public void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}
	
	public void setColor(float red, float green, float blue) {
		color[0] = red;
		color[1] = green;
		color[2] = blue;
	}
	
	public void setScale(float x, float y, float z) {
		scale[0] = x;
		scale[1] = y;
		scale[2] = z;
	}
	
	public void setDeceleration(float d) {
		deceleration = d;
	}
	
	public void setParticleCount(int count) {
		particleCount = count;
	}
	
	public void initializeParticles() {
		float max = .5f;
		for (int i = 0; i < particleCount; i++) {
			Particle p = new Particle();
			
			p.maximum[0] = randGenerator.nextFloat() * 1;
			p.maximum[1] = randGenerator.nextFloat() * 6;
			p.maximum[2] = randGenerator.nextFloat() * 1;
			
			p.position[0] = position[0];
			p.position[1] = position[1];
			p.position[2] = position[2];
			
			p.movement[0] = (randGenerator.nextFloat() * max) - (max/2);
			p.movement[1] = (randGenerator.nextFloat() * max);
			p.movement[2] = (randGenerator.nextFloat() * max) - (max/2);
			
		
			p.color[0] = color[0];
			p.color[1] = color[1];
			p.color[2] = color[2];
			
			p.scale[0] = scale[0];
			p.scale[1] = scale[1];
			p.scale[2] = scale[2];

			
			p.acceleration = randGenerator.nextFloat() % 10;
			p.deceleration = 0.0025f;
			particles.add(p);
		}
	}
	
	public void updateParticles() {
		for (Particle p : particles) {
			
			if (Math.abs(p.position[0] - position[0]) < p.maximum[0])
				p.position[0] += p.movement[0] * .1;
			p.position[1] += p.movement[1] * .4;
			if (Math.abs(p.position[2] - position[2]) < p.maximum[2])
				p.position[2] += p.movement[2] * .1;

			
			p.deceleration += deceleration;
						
			p.color[0] += 0.01;
			p.color[1] += 0.01;
			p.color[2] += 0.01;

			float[] tempPos = new float[3];
			float[] pos = new float[3];
			tempPos[0] = p.position[0];
			tempPos[1] = p.position[1];
			tempPos[2] = p.position[2];
			pos[0] = position[0];
			pos[1] = position[1];
			pos[2] = position[2];
			if (tempPos[1] > p.maximum[1]) {
				p.position[0] = position[0];
				p.position[1] = position[1];
				p.position[2] = position[2];
				
				
				p.color[0] = color[0];
				p.color[1] = color[1];
				p.color[2] = color[2];
				
				p.scale[0] = scale[0];
				p.scale[1] = scale[1];
				p.scale[2] = scale[2];

				p.acceleration = randGenerator.nextFloat() % 10;
				p.deceleration = 0.0025f;
			}
		}
	}
	
	public void drawParticles() {

		for (Particle p : particles) {
			GL11.glPushMatrix();
			GL11.glColor3f(p.color[0], p.color[1], p.color[2]);
			GL11.glTranslatef(p.position[0], p.position[1], p.position[2]);
			GL11.glScalef(p.scale[0], p.scale[1], p.scale[2]);
			GL11.glBegin(GL11.GL_QUADS);
				//bot
				GL11.glVertex3f(0f, 0f, 0f);
				GL11.glVertex3f(1f, 0f, 0f);
				GL11.glVertex3f(1f, 0f, -1f);
				GL11.glVertex3f(0f, 0f, -1f);
				//top
				GL11.glVertex3f(0f, 1f, 0f);
				GL11.glVertex3f(1f, 1f, 0f);
				GL11.glVertex3f(1f, 1f, -1f);
				GL11.glVertex3f(0f, 1f, -1f);
				//front
				GL11.glVertex3f(0f, 0f, 0f);
				GL11.glVertex3f(1f, 0f, 0f);
				GL11.glVertex3f(1f, 1f, 0f);
				GL11.glVertex3f(0f, 1f, 0f);
				//back
				GL11.glVertex3f(0f, 0f, -1f);
				GL11.glVertex3f(1f, 0f, -1f);
				GL11.glVertex3f(1f, 1f, -1f);
				GL11.glVertex3f(0f, 1f, -1f);
				//right
				GL11.glVertex3f(1f, 0f, 0f);
				GL11.glVertex3f(1f, 0f, -1f);
				GL11.glVertex3f(1f, 1f, -1f);
				GL11.glVertex3f(1f, 1f, 0f);
				//left
				GL11.glVertex3f(0f, 0f, 0f);
				GL11.glVertex3f(0f, 0f, -1f);
				GL11.glVertex3f(0f, 1f, -1f);
				GL11.glVertex3f(0f, 1f, 0f);
			GL11.glEnd();
			GL11.glPopMatrix();
		}
	}
}
