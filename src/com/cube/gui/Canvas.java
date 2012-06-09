package com.cube.gui;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;


public class Canvas {
	public 	int 					x;
	public 	int 					y;
	public 	int						width;
	public 	int						height;
	public 	boolean 				show;
	public 	float[]					color;
	public 	ArrayList<MessageBox> 	messageBoxes;
	public	boolean					stealContext;
	public	int						id;
	
	public Canvas(int _x, int _y, int w, int h, boolean _show) {
		x = _x;
		y = _y;
		width = w;
		height = h;
		show = _show;
		color = new float[4];
		messageBoxes = new ArrayList<MessageBox>();
		stealContext = false;
	}
	public void setMessage(int idx, String str) {
		messageBoxes.get(idx).setMessage(str);
	}
	public void createMessageBox(int xOffset, int yOffset, int width, int linePixelHeight, String fontName, int fontSize, Color fontColor) {
		messageBoxes.add(new MessageBox(x + xOffset, y + yOffset, width, linePixelHeight, fontName, fontSize, fontColor));
	}
	public void drawMessages() {
		for (MessageBox mb : messageBoxes) {
			mb.prettyPrint(false);
		}
	}
	
	public void drawBackground() {
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor4f(color[0], color[1], color[2], color[3]);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x, y);
			GL11.glVertex2f(x, y + height);
			GL11.glVertex2f(x + width, y + height);		
			GL11.glVertex2f(x + width, y);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	public void draw() {
		if (show) {
			drawBackground();
			drawMessages();
		}
	}
	public void setColor(float r, float g, float b, float a) {
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = a;
	}
}
