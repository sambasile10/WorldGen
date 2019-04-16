package gen;

public class Range {
	
	/*
	 * Constant biome range(s)
	 * Used to determine which biome a tile should be given a height and moisture value
	 * 
	 * Takes in float arrays of length 4
	 * [0] = minimum height
	 * [1] = maximum height
	 * [2] = minimum moisture
	 * [3] = maximum moisture
	 */
	
	/*
	 * Constant list of valid tile ranges
	 */
	public static final Range[] OCEAN = new Range[] { new Range(0.0F, 0.1F, 0.0F, 1.0F) },
			WATER = new Range[] { new Range(0.0F, 0.1F, 0.0F, 1.0F) },
			SWAMP = new Range[] { new Range(0.0F, 0.1F, 0.65F, 1.0F) },
			DESERT = new Range[] { new Range(0.1F, 0.6F, 0.0F, 0.2F) },
			SHRUBLAND = new Range[] { new Range(0.1F, 0.4F, 0.2F, 0.35F) },
			GRASSLAND = new Range[] { new Range(0.1F, 0.25F, 0.35F, 0.8F), 
					new Range(0.25F, 0.4F, 0.35F, 0.5F),
					new Range(0.4F, 0.6F, 0.2F, 0.5F),
					new Range(0.6F, 0.8F, 0.0F, 0.2F)
			},
			FOREST = new Range[] { new Range(0.25F, 0.4F, 0.5F, 0.8F),
					new Range(0.4F, 0.6F, 0.5F, 1.0F),
					new Range(0.6F, 0.8F, 0.2F, 0.5F)
			},
			TROPICAL_FOREST = new Range[] { new Range(0.1F, 0.4F, 0.8F, 1.0F) },
			ALPINE_FOREST = new Range[] { new Range(0.6F, 0.8F, 0.5F, 1.0F),
					new Range(0.8F, 1.0F, 0.35F, 0.8F)
			},
			TUNDRA = new Range[] { new Range(0.8F, 1.0F, 0.0F, 0.35F) },
			ICE = new Range[] { new Range(0.8F, 1.0F, 0.8F, 1.0F) };
	
	//Min and max bound values
	private float heightMin, heightMax, moistureMin, moistureMax;
	
	//Range constructor, takes in min and max bounds for height and moisture
	public Range(float heightMin, float heightMax, float moistureMin, float moistureMax) {
		this.heightMin = heightMin;
		this.heightMax = heightMax;
		this.moistureMin = moistureMin;
		this.moistureMax = moistureMax;
	}
	
	//Returns minimum height
	public float getMinimumHeight() {
		return this.heightMin;
	}
	
	//Returns maximum height
	public float getMaximumHeight() {
		return this.heightMax;
	}
	
	//Returns minimum moisture
	public float getMinimumMoisture() {
		return this.moistureMin;
	}
	
	//Returns maximum moisture
	public float getMaximumMoisture() {
		return this.moistureMax;
	}
	
	//Returns true if the height and moisture values are within range of the defined
	public boolean inRange(float height, float moisture) {
		return ((heightMin <= height && heightMax >= height) && (moistureMin <= moisture && moistureMax >= moisture));
	}

}
