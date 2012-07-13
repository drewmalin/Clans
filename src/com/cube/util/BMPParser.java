/*
 *  BMPparser.java
 *  Project Disconnect
 *
 *  Created by Drew Malin on 12/3/2011.
 *  Copyright 2011 Drew Malin. All rights reserved.
 *
 */
package com.cube.util;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BMPParser {
	
	private RandomAccessFile raf;
	public int pixels[][][];
	public int width;
	public int height;
	
	public BMPParser(String filename) throws IOException {
		try {
			
			raf = new RandomAccessFile(filename, "r");
			pixels = parse(raf);
			
		} catch (Exception e) {
			System.err.println("Error loading map: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			raf.close();
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
	//------- FOR USE WITH RANDOMACCESSFILE -------//
	public int[][][] parse(RandomAccessFile raf) throws IOException {
		byte cbuf[] = new byte[4];
		int filesize, pixelAddr, header, junk, depth, compression;
		
		cbuf[0] = raf.readByte(); //Read the first two characters. If they are 'B' and 'M', then this is a valid BMP
		cbuf[1] = raf.readByte();
		if (cbuf[0] == 'B' && cbuf[1] == 'M') {
			
			filesize = 	readInt(raf, 4); 	//Next 4 bytes: filesize
			junk = 		readInt(raf, 4);	//Next 4 bytes: junk
			pixelAddr = readInt(raf, 4);	//Next 4 bytes: pixel data start address
			header = 	readInt(raf, 4); 	//Next 4 bytes: header (width/height/compression data)
			
			if (header != 40) {
				System.out.println("Error: Incompatible bitmap image given.");
				return null;
			}
			
			width = 	readInt(raf, 4); 	//Next 4 bytes: image width
			height = 	readInt(raf, 4);	//Next 4 bytes: image height
			junk = 		readInt(raf, 2);	//Next 2 bytes: junk
			depth = 	readInt(raf, 4);	//Next 4 bytes: color depth;
			
			if (depth != 24) {
				System.out.println("Error: Image depth is not 24.");
				return null;
			}
			
			compression = readInt(raf, 4);	//Next 4 bytes: compression
			
			if (compression != 0) {
				System.out.println("Error: Image is compressed.");
				return null;
			}
			
			//--------------- Image parsing -----------------------//
			
			// pixels read from left to right, bottom to top
			
			int pixels[][][] = new int[width][height][3];
			raf.seek((long)pixelAddr);
			/*
			for (int i = width-1; i >= 0; i--) {
				for (int j = 0; j < height; j++) {
					for (int color = 0; color < 3; color++) {
						pixels[i][j][color] = readInt(raf, 1);
					}
				}
			}*/
			for (int j = 0; j < height; j++) {
				for (int i = width - 1; i >= 0; i--) {
					for (int color = 0; color < 3; color++) {
						pixels[i][j][color] = readInt(raf, 1);
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

	public int readInt(RandomAccessFile raf, int num) throws IOException {
		byte cbuf[] = new byte[4];
		for (int i = 0; i < num; i++) {
			cbuf[i] = raf.readByte();
		}
		
		return binToInt(cbuf, num);
	}
}