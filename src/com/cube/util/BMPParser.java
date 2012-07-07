/*
 *  BMPparser.java
 *  Project Disconnect
 *
 *  Created by Drew Malin on 12/3/2011.
 *  Copyright 2011 Drew Malin. All rights reserved.
 *
 */
package com.cube.util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BMPParser {
	
	private InputStream is;
	private ByteArrayOutputStream output;
	
	public int pixels[][][];
	public int width;
	public int height;
	
	public BMPParser(String filename) throws IOException {
		try {
			is = this.getClass().getResourceAsStream(filename);
			output = new ByteArrayOutputStream();
			 
	        for (int read = is.read(); read >= 0; read = is.read())
		        output.write(read);
			
			pixels = parse(output);
			
		} catch (Exception e) {
			System.err.println("Error loading map: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			is.close();
			output.close();
		}
	}
	
	public void printPixels() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				System.out.print("(" + pixels[i][j][0] + ", ");
				System.out.print(pixels[i][j][1] + ", ");
				System.out.print(pixels[i][j][2] + ") ");
			}
			System.out.println("\n");
		}
	}
	
	public int[][][] parse(ByteArrayOutputStream raf) throws IOException {
		
		byte buffer[] = raf.toByteArray();
		
		int filesize, pixelAddr, header, junk, depth, compression;
		
		int counter = 0;

		if (buffer[0] == 'B' && buffer[1] == 'M') {
			
			filesize 	= readInt(buffer, 2, 4);
			junk 		= readInt(buffer, 6, 4);
			pixelAddr 	= readInt(buffer, 10, 4);
			header 		= readInt(buffer, 14, 4);	
			
			if (header != 40) {
				System.out.println("Error: Incompatible bitmap image given.");
				return null;
			}

			width 	= readInt(buffer, 18, 4);
			height 	= readInt(buffer, 22, 4);
			junk	= readInt(buffer, 26, 2);
			depth 	= readInt(buffer, 28, 4);
			
			
			if (depth != 24) {
				System.out.println("Error: Image depth is not 24.");
				return null;
			}

			compression = readInt(buffer, 32, 4);
			
			if (compression != 0) {
				System.out.println("Error: Image is compressed.");
				return null;
			}
			
			//--------------- Image parsing -----------------------//
			
			// pixels read from left to right, bottom to top
			
			int pixels[][][] = new int[width][height][3];

			counter = pixelAddr;

			for (int j = 0; j < height; j++) {
				for (int i = width - 1; i >= 0; i--) {
					for (int color = 0; color < 3; color++) {
						pixels[i][j][color] = readInt(buffer, counter++, 1);

					}
				}
			}
			
			return pixels;
		}
		
		else {
			System.out.println("Error: File provided is not a bitmap file.");
			return null;
		}
	}

	public int readInt(byte[] buf, int startIdx, int count) {
		byte cbuf[] = new byte[4];
		
		for (int i = 0; i < count; i++) {
			
			cbuf[i] = buf[startIdx + i];
		}
		
		return binToInt(cbuf, count);
	}

	private int binToInt(byte[] cbuf, int size) {
		int intVal = 0;
		
		
		switch (size) {
		case 4:
			intVal = (int) ((cbuf[3] << 24) |
				            (cbuf[2] << 16) |
				            (cbuf[1] << 8)  |
				            (cbuf[0]));
			break;
		case 3:
			intVal = (int) ((cbuf[2] << 16) |
					        (cbuf[1] << 8)  |
					        (cbuf[0]));
			break;
		case 2:
			intVal = (int) ((cbuf[1] << 8) |
					        (cbuf[0]));
			break;
		case 1:
			intVal = (int) (cbuf[0]) & 0xff;
			break;
		}
		
		return intVal;

	}
}