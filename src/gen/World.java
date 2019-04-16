package gen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class World {

	/*
	 * Contains all data for a World from the Generator class
	 * Responsible for writing out a biome RGB image
	 */
	
	//FloatMaps for height and moisture values
	private FloatMap heightMap, moistureMap;
	
	//2-dimensional biome array of the world
	private Biome[][] tileMap;

	//World size and world seed
	private int size, seed;

	//World constructor, defined with size and seed
	public World(int size, int seed) {
		this.size = size;
		this.seed = seed;
		this.tileMap = new Biome[size][size];
	}

	//Write out a PNG image of the biome array
	//String fname = file name of the outputted file
	//File will be written to the current working directory
	public void writeImage(String fName) {
		//Create a new image
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {

				// Debug any missing tiles
				if (tileMap[x][y] == null) {
					System.out.println("no biome @ (" + x + ", " + y + "), val=(" + heightMap.get(x, y) + ", "
							+ moistureMap.get(x, y));
				}

				//Set the RGB of the current pixel to the matching biome color
				image.setRGB(x, y, tileMap[x][y].getColor().getRGB());
			}
		}

		try {
			//Write created image to the disk in the PNG format
			ImageIO.write(image, "png", new File(fName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Calculates out the biomes if the height and moisture FloatMaps have data
	public void calculateBiomes() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (Biome b : Biome.values()) {
					for (Range r : b.getRange()) {
						if (r.inRange(heightMap.get(x, y), moistureMap.get(x, y))) {
							this.getBiomeMap()[x][y] = b;
						}
					}
				}
			}
		}
	}

	//Returns height map
	public FloatMap getHeightMap() {
		return heightMap;
	}

	//Returns moisture map
	public FloatMap getMoistureMap() {
		return moistureMap;
	}

	//Returns biome map
	public Biome[][] getBiomeMap() {
		return tileMap;
	}

	//Returns the biome at a certain x and y
	public Biome getBiomeAt(int x, int y) {
		return tileMap[x][y];
	}

	//Returns the biome at a certain point (x, y)
	public Biome getBiomeAt(Point p) {
		return tileMap[p.getX()][p.getY()];
	}

	//Returns the world size
	public int getSize() {
		return size;
	}

	//Returns the world seed
	public int getSeed() {
		return seed;
	}

	//Set the height FloatMap
	public void setHeightMap(FloatMap mp) {
		this.heightMap = mp;
	}

	//Set the moisture FloatMap
	public void setMoistureMap(FloatMap mp) {
		this.moistureMap = mp;
	}

}
