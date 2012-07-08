package com.cube.gui;

import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import com.cube.core.Engine;
import com.cube.core.Resources;


public class Canvas {
	public 	int 					x;
	public 	int 					y;
	public 	int						width;
	public 	int						height;
	public 	int						rotation;
	public 	boolean 				show;
	public 	float[]					color;
	public 	float[] 				hoverColor;
	public 	float[]					baseColor;
	
	public 	ArrayList<MessageBox> 	messageBoxes;
	public	boolean					active;
	public 	String 					backgroundImageFile;
	MessageBox hoverMessageBox;
	public boolean hovering = false;
	public boolean hoverColorRequested = false;
	public boolean animated = false;
	public float rotateSpeed = 0;
	
	public Canvas() {
		color 		= new float[]{0, 0, 0, 0};
		hoverColor 	= new float[]{0, 0, 0, 0};
		baseColor 	= new float[]{0, 0, 0, 0};
		hovering = false;
		rotation = 0;
		messageBoxes = new ArrayList<MessageBox>();
	}
	
	public void drawMessages() {
		for (MessageBox mb : messageBoxes) {
			mb.draw();
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
		
		if (animated) rotation += rotateSpeed;
		
		if (backgroundImageFile != null) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			Resources.textureLibrary.get(backgroundImageFile).bind();
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glColor4f(1f, 1f, 1f, 1f);
			GL11.glTranslatef(x, y, 0);
			GL11.glTranslatef(width/2, height/2, 0);
			GL11.glRotatef(rotation, 0, 0, 1);
			GL11.glTranslatef(-width/2, -height/2, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(0, 0);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex2f(0, height);
				GL11.glTexCoord2f(1, 1);
				GL11.glVertex2f(width, height);
				GL11.glTexCoord2f(1, 0);
				GL11.glVertex2f(width, 0);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		else {
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glColor4f(color[0], color[1], color[2], color[3]);
			GL11.glTranslatef(x, y, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(0, 0);
				GL11.glVertex2f(0, height);
				GL11.glVertex2f(width, height);		
				GL11.glVertex2f(width, 0);
			GL11.glEnd();
			GL11.glPopMatrix();
		}
	}
	
	public void checkHover(int mouseX, int mouseY) {

		if ((mouseX >= x && mouseX <= (x + width)) &&
			(mouseY >= y && mouseY <= (y + height))) {
			
			if (hoverColorRequested) {
				color[0] = hoverColor[0];
				color[1] = hoverColor[1];
				color[2] = hoverColor[2];
			}
			
			hovering = true;
		}

		else {
			color[0] = baseColor[0];
			color[1] = baseColor[1];
			color[2] = baseColor[2];
			
			hovering = false;
		}
	}
	
	public void draw() {
		drawBackground();
		if (hovering && hoverMessageBox!= null && !hoverMessageBox.message.isEmpty()) {
			hoverMessageBox.x = Mouse.getX() + 20;
			hoverMessageBox.y = Engine.HEIGHT - Mouse.getY() + 20;
			hoverMessageBox.draw();
		}
	}
	
	public void setColor(float[] colorArr) {	
		color[0] = baseColor[0] = colorArr[0];
		color[1] = baseColor[1] = colorArr[1];
		color[2] = baseColor[2] = colorArr[2];
		color[3] = baseColor[3] = colorArr[3];
		
	}
	
	public void setHovorColor(float[] colorArr) {
		
		hoverColorRequested = true;
		
		hoverColor[0] = colorArr[0];
		hoverColor[1] = colorArr[1];
		hoverColor[2] = colorArr[2];
		hoverColor[3] = colorArr[3];
	}
}
