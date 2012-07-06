package com.cube.gui;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.cube.core.Graphics;
import com.cube.core.Resources;
import com.cube.util.ShaderManager;


public class Canvas {
	public 	int 					x;
	public 	int 					y;
	public 	int						width;
	public 	int						height;
	public 	boolean 				show;
	public 	float[]					color;
	public 	ArrayList<MessageBox> 	messageBoxes;
	public	boolean					active;
	public	int						id;
	public 	String 					backgroundImageFile;
	
	public Canvas(int _x, int _y, int w, int h, boolean _show) {
		x = _x;
		y = _y;
		width = w;
		height = h;
		show = _show;
		color = new float[]{0, 0, 0, 0};
		messageBoxes = new ArrayList<MessageBox>();
		active = false;
	}
	
	public Canvas() {
		color = new float[]{0, 0, 0, 0};
		messageBoxes = new ArrayList<MessageBox>();
	}
	
	public void setMessage(int idx, String str) {
		messageBoxes.get(idx).setMessage(str);
	}

	public void drawMessages() {
		for (MessageBox mb : messageBoxes) {
			mb.prettyPrint(false);
		}
	}
	
	public void setBackgroundImage(String filename) {
		
		backgroundImageFile = filename.substring(filename.lastIndexOf("/")+1, filename.indexOf("."));

		try {
			Resources.textureLibrary.put(backgroundImageFile, Resources.texLoader.getTexture(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void drawBackground() {
		
		if (backgroundImageFile != null) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			Resources.textureLibrary.get(backgroundImageFile).bind();
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glColor4f(1f, 1f, 1f, 1f);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(x, y);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex2f(x, y + height);
				GL11.glTexCoord2f(1, 1);
				GL11.glVertex2f(x + width, y + height);
				GL11.glTexCoord2f(1, 0);
				GL11.glVertex2f(x + width, y);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		else {
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
	
	public void setColor(float[] colorArr) {	
		color[0] = colorArr[0];
		color[1] = colorArr[1];
		color[2] = colorArr[2];
		color[3] = colorArr[3];
	}
}
