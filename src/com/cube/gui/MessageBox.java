package com.cube.gui;
import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;

public class MessageBox {

	//Coordinates, usually relative to top-left of the enclosing menu box
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean show = true;
	
	//Font attributes
	public String fontName;
	public int fontSize;
	private Color color;
	private UnicodeFont unicodeFont;
	public String message;
	public int lineCount;
	public int maxWidth;
	public int lineHeight;
	public int lineWidth;
	public boolean skipProcessing = false;
	
	public MessageBox() {
		color = Color.WHITE;
	}

	@SuppressWarnings("unchecked")
	public void load() {
		maxWidth = width; //TODO: TEMPORARY
		try {
			
			if (fontName.contains("/res/fonts/"))
				unicodeFont = new UnicodeFont(fontName, fontSize, false, false);
			else {
				java.awt.Font awtFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, 300);
				unicodeFont = new UnicodeFont(awtFont, fontSize, false, false);
			}
		
			unicodeFont.getEffects().add(new ColorEffect(color));
			unicodeFont.addAsciiGlyphs();
			unicodeFont.loadGlyphs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void reload() {
		try {
			java.awt.Font awtFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, 300);
			unicodeFont = new UnicodeFont(awtFont, fontSize, false, false);
			unicodeFont.getEffects().add(new ColorEffect(color));
			unicodeFont.addAsciiGlyphs();
			unicodeFont.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void setMessage(String msg) {
		message = msg;
	}
	
	public void setFontColor(String strColor) {
		strColor = strColor.toLowerCase();
		
		if (strColor.equals("white"))
			color = Color.WHITE;
		else if (strColor.equals("white"))
			color = Color.BLACK;
		else if (strColor.equals("blue"))
			color = Color.BLUE;
		else if (strColor.equals("cyan"))
			color = Color.CYAN;
		else if (strColor.equals("dark_gray"))
			color = Color.DARK_GRAY;
		else if (strColor.equals("light_gray"))
			color = Color.LIGHT_GRAY;
		else if (strColor.equals("gray"))
			color = Color.GRAY;
		else if (strColor.equals("green"))
			color = Color.GREEN;
		else if (strColor.equals("magenta"))
			color = Color.MAGENTA;
		else if (strColor.equals("orange"))
			color = Color.ORANGE;
		else if (strColor.equals("pink"))
			color = Color.PINK;
		else if (strColor.equals("red"))
			color = Color.RED;
		else if (strColor.equals("yellow"))
			color = Color.YELLOW;
	}
	/* Function to print a string to a message box. Given the maximum number of characters per line
	 * (maxWidth), keep printing substrings of length < maxWidth until the entire string has printed.
	 * 
	 * If the total message length is less than or equal to maxWidth, print it on one line and return.
	 * Otherwise, while the working message length remains larger than the maxWidth, print a substring
	 * of the message up to the last whitespace character, saving the remainder of the string back in
	 * message. Once a substring is created that is less than maxWidth in length, print it and return.
	 */
	public void print() {
		unicodeFont.drawString(x, y, message);
	}
	
	public void processMessage() {
		int tempEnd;
		String temp;
		lineCount = 0;

		temp = message;
		message = "";
		lineWidth = temp.length();
		
		while (true) {
			if (temp.length() <= maxWidth) {
				message += temp;
				lineCount++;
				break;
			}
			else {
				tempEnd = temp.lastIndexOf(" ", maxWidth);
				if (tempEnd == -1) tempEnd = temp.length() - 1;
				
				message += temp.substring(0, tempEnd) + "\n";
				temp = temp.substring(tempEnd + 1);
				lineCount++;
				lineWidth = maxWidth;
			}
		}
	}
	
	public void prettyPrint(boolean background) {
		
		if (show) {
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
		
			if (!skipProcessing) processMessage();
			
			if (background) {
			
				int charPixelWidth = 6; //temp arbitrary number
				
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glColor4f(.8f, .8f, .8f, 1f);
					GL11.glVertex2f(x, y);
					GL11.glVertex2f(x, y + (lineCount * lineHeight));
					GL11.glVertex2f(x + (lineWidth * charPixelWidth), y + (lineCount * lineHeight));		
					GL11.glVertex2f(x + (lineWidth * charPixelWidth), y);
				GL11.glEnd();
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);	
			print();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			GL11.glPopMatrix();
		}
	}
}
