package gen;

import java.awt.Color;

public enum Biome {
	
	/*
	 * Biomes are the end result of the generation progress
	 * Biomes are determined from a range of height and moisture values (see Range class)
	 */
	
	/*
	 * Constant list of biomes
	 * 
	 * (biome ID, biome name, biome RGB color, range enum value)
	 */
	OCEAN(0, "Ocean", new Color(34, 139, 204), Range.OCEAN),
	WATER(1, "Shallow Water", new Color(169, 242, 236), Range.WATER),
	SWAMP(2, "Swamp", new Color(96, 88, 94), Range.SWAMP),
	DESERT(3, "Desert", new Color(255, 227, 119), Range.DESERT),
	SHRUBLAND(4, "Arid Shrubland", new Color(216, 136, 75), Range.SHRUBLAND),
	GRASSLAND(5, "Grassland", new Color(152, 226, 13), Range.GRASSLAND),
	FOREST(5, "Forest", new Color(72, 119, 31), Range.FOREST),
	TROPICS(6, "Tropical Forest", new Color(170, 232, 46), Range.TROPICAL_FOREST),
	TUNDRA(7, "Tundra", new Color(28, 189, 221), Range.TUNDRA),
	ICE(8, "Ice", new Color(237, 237, 237), Range.ICE),
	ALPINE_FOREST(9, "Alpine Forest", new Color(108, 181, 146), Range.ALPINE_FOREST);
	
	//Variables to build a biome
	int id;
	String name;
	Color color;
	
	//All ranges are constant and defined in the Range class
	Range[] range;
	
	//Biome enum constructor
	Biome(int id, String name, Color color, Range[] range) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.range = range;
	}
	
	//Returns biome ID
	public int getBiomeID() {
		return this.id;
	}
	
	//Returns biome name
	public String getBiomeName() {
		return this.name;
	}
	
	//Returns biome RGB color
	public Color getColor() {
		return this.color;
	}
	
	//Returns biome range value(s)
	public Range[] getRange() {
		return this.range;
	}

}
