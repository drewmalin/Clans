package com.cube.gui;

import com.cube.gui.Canvas;
import com.cube.gui.ClickListener;

public class Button extends Canvas {

	public boolean hovering;
	public boolean selected;
	public float[] baseColor;
	public float[] hoverColor;
	public float[] selectedColor;
	
	private ClickListener clickListener;
	
	public Button(int _x, int _y, int w, int h, boolean _show) {
		super(_x, _y, w, h, _show);
		hovering = false;
		selected = false;
		baseColor = new float[3];
		hoverColor = new float[3];
		selectedColor = new float[3];
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
	
	public void setClickListener( ClickListener cl) {
		clickListener = cl;
	}
	
	public void onClick() {
		clickListener.onClick();
	}
}
