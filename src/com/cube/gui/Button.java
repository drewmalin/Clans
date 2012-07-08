package com.cube.gui;

import org.lwjgl.input.Mouse;

import com.cube.core.Engine;
import com.cube.gui.Canvas;
import com.cube.gui.ClickListener;

public class Button extends Canvas {

	public String name;
	public boolean selected;
	
	private ClickListener clickListener;
	
	public Button() {}
	
	public void setHoverMessage(String msg) {
		String[] tok = msg.split("\n");
		hoverMessageBox.message = msg;
		hoverMessageBox.skipProcessing = true;
		hoverMessageBox.lineWidth = hoverMessageBox.maxWidth;
		hoverMessageBox.lineCount = tok.length;
	}
	
	public void openHoverMessageBox() {
		if (!hoverMessageBox.message.isEmpty()) {
			hoverMessageBox.x = Mouse.getX() + 20;
			hoverMessageBox.y = Engine.HEIGHT - Mouse.getY() + 20;
			hoverMessageBox.show = true;
			hoverMessageBox.prettyPrint();
		}
	}
	
	public void closeHoverMessageBox() {
		hoverMessageBox.show = false;
	}
	
	public void setClickListener( ClickListener cl) {
		clickListener = cl;
	}
	
	public void onClick() {
		if (clickListener != null)
			clickListener.onClick();
		else
			System.out.println("button clicked!");
	}
	
	public void draw() {
		super.draw();
		drawMessages();
	}
}
