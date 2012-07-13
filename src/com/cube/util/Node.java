package com.cube.util;

import java.util.ArrayList;
import java.util.HashMap;

public class Node  {
	private int dimLimit = 16;
	
	public ArrayList<Node> children;
	public String name;
	public String data;
	
	/** Node()
	 * 
	 * Constructor for a single node. Instantiates a new arraylist of children nodes.
	 */
	public Node() {
		children = new ArrayList<Node>();
	}
	
	public Node(String _name) {
		name = _name;
		children = new ArrayList<Node>();
	}
	
	public Node(String _name, String _data) {
		data = _data;
		name = _name;
		children = new ArrayList<Node>();
	}
	
	/** readString()
	 * 
	 * @return The data corresponding to this element. If the XML element tag does not
	 * resolve to any data (in the case of root elements, for example, which contain children
	 * elements but no data themselves), the method will return null
	 */
	public String readString() {
		return data;
	}
	
	/** readInt()
	 * 
	 * @return The data corresponding to this element converted to an integer. If the XML element
	 * tag does not resolve to any data (in the case of root elements, for example, which contain 
	 * children elements but no data themselves), the method will return null
	 */
	public int readInt() {
		return Integer.parseInt(data);
	}
	
	/** readFloat()
	 * 
	 * @return The data corresponding to this element converted to a float. If the XML element
	 * tag does not resolve to any data (in the case of root elements, for example, which contain 
	 * children elements but no data themselves), the method will return null
	 */
	public float readFloat() {
		return Float.parseFloat(data);
	}
	
	/** readStringArray() {
	 * 
	 * @return The data corresponding to this element converted to an array of strings. If the XML 
	 * element tag does not resolve to any data (in the case of root elements, for example, which 
	 * contain children elements but no data themselves), the method will return null
	 */
	public String[] readStringArray() {

		String[] ret = data.split("\\s");
		return ret;
	}
	
	/** readIntArray()
	 * 
	 * @return The data corresponding to this element converted to an array of integers. If the XML 
	 * element tag does not resolve to any data (in the case of root elements, for example, which 
	 * contain children elements but no data themselves), the method will return null
	 */
	public int[] readIntArray() {
		int idx = 0;
		int ret[] = new int[dimLimit];
		String[] tokens = data.split("\\s");
		
		for (String token : tokens) {
			ret[idx++] = Integer.parseInt(token);
		}
		return ret;
	}
	
	/** readFloatArray()
	 * 
	 * @return The data corresponding to this element converted to an array of floats. If the XML 
	 * element tag does not resolve to any data (in the case of root elements, for example, which 
	 * contain children elements but no data themselves), the method will return null
	 */
	public float[] readFloatArray() {
		int idx = 0;
		float ret[] = new float[dimLimit];
		String[] tokens = data.split("\\s");
		
		for (String token : tokens) {
			ret[idx++] = Float.parseFloat(token);
		}
		return ret;
	}

	/** readBoolean()
	 * 
	 * @return The data corresponding to this element converted to a boolean. If the XML 
	 * element tag does not resolve to any data (in the case of root elements, for example, which 
	 * contain children elements but no data themselves), the method will return null
	 */
	public boolean readBoolean() {
		if (data.equals("1") || data.toUpperCase().equals("TRUE"))
			return true;
		else
			return false;
	}
}
