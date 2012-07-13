package com.cube.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class XMLWriter {
	
	String filename;
	BufferedWriter out;
	
	public XMLWriter(String name) {
		filename = name;
		try {
			out = new BufferedWriter(new FileWriter(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(Node root) {
		try {
			for (Node child : root.children) {
				if (child.data != null) {
					out.write("<" + child.name + ">");
					out.write(child.data);
					out.write("</" + child.name + ">\n");
				}
				else if (!child.children.isEmpty()) {
					out.write("<" + child.name + ">\n");
					write(child);
					out.write("</" + child.name + ">\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
