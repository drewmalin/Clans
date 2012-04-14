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
	public boolean show = true;
	
	//Font attributes
	private String fontName;
	private int fontSize;
	private Color color;
	private UnicodeFont unicodeFont;
	public String message;
	private int maxWidth;
	private int offset;
	
	/* Function to create a message box. The x and y coordinates are offsets from the top left of
	 * the enclosing menu window (calculated in the menu window itself), w is the maximum number of
	 * characters per line, h is the height of each line in pixels.
	 */
	@SuppressWarnings("unchecked")
	public MessageBox(int _x, int _y, int w, int h, String _fontName, int _fontSize, Color _color) {
		try {
			x 			= _x;
			y 			= _y;
			maxWidth 	= w;
			fontName 	= _fontName;
			fontSize 	= _fontSize;
			color 		= _color;
			offset 		= h;
			
			if (fontName.contains("resources/fonts/"))
				unicodeFont = new UnicodeFont(fontName, fontSize, false, false);
			else {
				java.awt.Font awtFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, 300);
				unicodeFont = new UnicodeFont(awtFont, fontSize, false, false);
			}
			
			unicodeFont.getEffects().add(new ColorEffect(color));
			unicodeFont.addAsciiGlyphs();
			unicodeFont.loadGlyphs();
		} catch (SlickException e) {
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
	
	/* Function to print a string to a message box. Given the maximum number of characters per line
	 * (maxWidth), keep printing substrings of length < maxWidth until the entire string has printed.
	 * 
	 * If the total message length is less than or equal to maxWidth, print it on one line and return.
	 * Otherwise, while the working message length remains larger than the maxWidth, print a substring
	 * of the message up to the last whitespace character, saving the remainder of the string back in
	 * message. Once a substring is created that is less than maxWidth in length, print it and return.
	 */
	public void print() {
		String msg = message;
		int tempEnd;
		int i = 0;

		if (msg.length() <= maxWidth) {
			unicodeFont.drawString(x, y, msg);
		}
		else {
			for (; msg.length() > maxWidth; i++) {
				tempEnd = msg.lastIndexOf(" ", maxWidth);
				unicodeFont.drawString(x, y + (offset * i), msg.substring(0, tempEnd));
				msg = msg.substring(tempEnd+1);
			}
			unicodeFont.drawString(x, y + (offset * i), msg);
		}
	}
	
	public void prettyPrint() {
		
		if (show) {
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glEnable(GL11.GL_TEXTURE_2D);	
	
				print();

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
	}
}
