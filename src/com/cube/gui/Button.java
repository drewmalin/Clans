package com.cube.gui;

import org.lwjgl.input.Mouse;

import com.cube.core.Engine;
import com.cube.gui.Canvas;
import com.cube.gui.ClickListener;

public class Button extends Canvas {

	public String name;
	public boolean hovering;
	public boolean selected;
	public float[] baseColor;
	public float[] hoverColor;
	public float[] selectedColor;
	MessageBox hoverMessageBox;
	
	private ClickListener clickListener;
	
	public Button() {
		baseColor = new float[]{0, 0, 0};
		hoverColor = new float[]{0, 0, 0};
		selectedColor = new float[]{0, 0, 0};
	}

	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		baseColor[0] = color[0];
		baseColor[1] = color[1];
		baseColor[2] = color[2];
		hoverColor[0] = color[0] + .1f;
		hoverColor[1] = color[1] + .1f;
		hoverColor[2] = color[2] + .1f;
		selectedColor[0] = color[0] + .2f;
		selectedColor[1] = color[1] + .2f;
		selectedColor[2] = color[2] + .2f;
	}

	public void checkHover(int mouseX, int mouseY) {

		if ((mouseX >= x && mouseX <= (x + width)) &&
			(mouseY >= y && mouseY <= (y + height))) {
			color[0] = hoverColor[0];
			color[1] = hoverColor[1];
			color[2] = hoverColor[2];
			
			hovering = true;
		}

		else {
			color[0] = baseColor[0];
			color[1] = baseColor[1];
			color[2] = baseColor[2];
			
			hovering = false;
		}
		
		if (selected) {
			color[0] = selectedColor[0];
			color[1] = selectedColor[1];
			color[2] = selectedColor[2];
		}
	}
	
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
			hoverMessageBox.prettyPrint(true);
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
}
