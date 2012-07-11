package com.cube.core;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import com.cube.util.Texture;
import com.cube.util.ShaderManager;

public class Unit extends Entity {
	
	public ArrayList<String> roles;
			
	public Unit(String role, Clan c) {
		
		roles = new ArrayList<String>();
		
		roles.add(role);
		
		clanRef = c;
		
		show = true;

		selectionRingRotation = 0;
		
		position.x = c.position.x + 5;
		position.z = c.position.z + 5;
		
		maxHealth = curHealth = 100;
		
		max_v = .2f;
	}
	
	public void update(int timeElapsed) {
		super.update(timeElapsed);
	}
	
	@Override
	public ArrayList<String> getRoles() {
		return roles;
	}
	
	@Override
	public void draw() {
		GL11.glPushMatrix();
			GL11.glLoadIdentity();
			
			GL11.glTranslated(position.x, position.y + Resources.map.getHeight((float)position.x, (float)position.z), position.z);
			GL11.glRotatef(rotation[0], 1, 0, 0);
			GL11.glRotatef(rotation[1], 0, 1, 0);
			GL11.glRotatef(rotation[2], 0, 0, 1);
			GL11.glScalef(scale[0], scale[1], scale[2]);

			if (Graphics.colorPicking) {
				GL11.glColor3ub((byte)colorID[0], (byte)colorID[1], (byte)colorID[2]);
				Resources.modelLibrary.get(model).drawOBJ();
			}
			else {
				GL11.glColor3f(color[0], color[1], color[2]);
				
				//Bind shaders
//				Graphics.shaderManager.bindShader(ShaderManager.HEMISPHERE);
				if(texture == null) {
					Resources.modelLibrary.get(model).draw();
				}else{
					Resources.modelLibrary.get(model).draw(Resources.textureLibrary.get(texture));
				}
				
				//Unbind shaders
//				Graphics.shaderManager.unbindShader(ShaderManager.HEMISPHERE);
				
				inventory.draw(this);
				
				if (this == Game.selectedEntity)
					drawSelectionRing();
			}
		GL11.glPopMatrix();
	}
}
