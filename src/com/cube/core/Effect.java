package com.cube.core;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;

public class Effect {
	public int particleCount;
	private ArrayList<Particle> particles;
	private Random randGenerator;
	
	public float position[];
	public float color[];
	public float scale[];
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
			
			p.xMax = randGenerator.nextFloat() * 1;
			p.yMax = randGenerator.nextFloat() * 6;
			p.zMax = randGenerator.nextFloat() * 1;
			
			p.xPos = position[0];
			p.yPos = position[1];
			p.zPos = position[2];
			
			p.xMov = (randGenerator.nextFloat() * max) - (max/2);
			p.yMov = (randGenerator.nextFloat() * max);
			p.zMov = (randGenerator.nextFloat() * max) - (max/2);
			
			p.R = color[0];
			p.G = color[1];
			p.B = color[2];
			
			p.xScale = scale[0];
			p.yScale = scale[1];
			p.zScale = scale[2];

			
			p.acceleration = randGenerator.nextFloat() % 10;
			p.deceleration = 0.0025f;
			particles.add(p);
		}
	}
	
	public void updateParticles() {
		for (Particle p : particles) {
			
			if (Math.abs(p.xPos - position[0]) < p.xMax)
				p.xPos += p.xMov * .1;
			p.yPos += p.yMov * .4;
			if (Math.abs(p.zPos - position[2]) < p.zMax)
				p.zPos += p.zMov * .1;

			
			p.deceleration += deceleration;
						
			p.R += 0.01;
			p.G += 0.01;
			p.B += 0.01;

			float[] tempPos = new float[3];
			float[] pos = new float[3];
			tempPos[0] = p.xPos;
			tempPos[1] = p.yPos;
			tempPos[2] = p.zPos;
			pos[0] = position[0];
			pos[1] = position[1];
			pos[2] = position[2];
			if (tempPos[1] > p.yMax) {
				p.xPos = position[0];
				p.yPos = position[1];
				p.zPos = position[2];
				
				p.R = color[0];
				p.G = color[1];
				p.B = color[2];
				
				p.xScale = scale[0];
				p.yScale = scale[1];
				p.zScale = scale[2];

				p.acceleration = randGenerator.nextFloat() % 10;
				p.deceleration = 0.0025f;
			}
		}
	}
	
	public void drawParticles() {

		for (Particle p : particles) {
			GL11.glPushMatrix();
			GL11.glColor3f(p.R, p.G, p.B);
			GL11.glTranslatef(p.xPos, p.yPos, p.zPos);
			GL11.glScalef(p.xScale, p.yScale, p.zScale);
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
