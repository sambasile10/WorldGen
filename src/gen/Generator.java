package gen;

import java.util.Random;

public class Generator implements Runnable {

	/*
	 * Generation options include the following
	 * 
	 * - World size (1024 through 2^12) - World seed
	 * 
	 * - Randomness - affects float randomness AND octaves AND persistence
	 * 
	 * ALL OTHER WORLD OPTIONS - warmer vs colder - taller or more flat - inland
	 * lakes and extreme mountains - external islands? - feature points - polar
	 * center for biome spread - height center for height spread
	 * 
	 * !!!ALL GET DERIVED FROM THE SEED!!!!
	 */

	//Generation constants
	private final int HEIGHT_OCTAVES = 10, MOISTURE_OCTAVES = 12;
	
	//Specific world variables
	private int size, seed;
	private float modHeight, modMoisture, randomness, modc;
	
	//UI variables
	private String status;
	private double progress;

	// Global Random object
	private Random random;
	
	//Stored world
	private World world;
	
	//Raw world output file path
	private String worldOutputPath;

	//Generator constructor, takes in the world size and world seed
	public Generator(int size, int seed) {
		this.size = size;
		this.seed = seed;
		this.random = new Random(seed);
		this.deriveSeed();
		this.status = "Preparing";
		this.progress = 0.0D;
	}

	/* Derives generation options from seed
	 * 
	 * Generation options are random the random seeded by the input seed so the
	 * generation always remains constant
	 */
	
	public void deriveSeed() {
		//Get randomness, height and moisture modifiers from a random seeded by the world seed
		this.randomness = 0.05F + random.nextFloat() * (0.3F - 0.05F);
		this.modHeight = randomModifier(0.0F, 0.8F);
		this.modMoisture = randomModifier(0.0F, 0.8F);
		
		//Set default no-flag options
		this.modc = 1.0F;
	}
	
	//Set a GenerationFlag to modify generation
	public void setFlag(GenerationFlag flag) {
		//Pangea selected, adjust mask size
		if(flag == GenerationFlag.PANGEA) {
			this.modc = 0.5F;
		}
		
		System.out.println("Flag set: " + flag.getFlagName());
	}

	//The start method for the generation thread
	@Override
	public void run() {
		//Output modifiers to console
		System.out.println("generation seed: " + seed);
		System.out.println("randomness=" + randomness);
		System.out.println("modHeight=" + modHeight + ", modMoisture=" + modMoisture);
		
		//Call generation function
		generate();
	}
	
	/*
	 * Generation function, generates the world and outputs to the 'world' variable
	 * as well as writing a file to the running directory of the world
	 * 
	 * Also hooks into the progress bar to allow for UI updates
	 */
	public void generate() {
		/*
		 * Generate height map
		 */
		
		// Generate height simplex noise seed
		int heightNoiseSeed = random.nextInt();
		System.out.println("seeding height with " + heightNoiseSeed);
		
		//Derive persistence(s) from seed
		float heightNoisePersistence = 0.62F + random.nextFloat() * (0.74F - 0.62F);
		float moistureNoisePersistence = 0.53F + random.nextFloat() * (0.68F - 0.53F);
		System.out.println("using " + HEIGHT_OCTAVES + " octaves and " + heightNoisePersistence + " persistence");
		
		//Update status and progress
		this.status = "generating height map...";
		this.progress = 5.0D;
		
		//Generate height simplex noise
		FloatMap heightNoise = MaskGenerator.getSimplexNoise(size, heightNoiseSeed, HEIGHT_OCTAVES, heightNoisePersistence);
		heightNoise.adjustRange(0, 1);
		heightNoise.invert();
		
		//Generate height noise mask (circular using global modc radius variable)
		FloatMap heightMask = MaskGenerator.getCircularMask(size, modc);
		heightMask.adjustRange(0, 1);
		
		//Update status and progress
		this.status = "masking height noise...";
		this.progress = 25.0D;
		
		// Generate secondary extremity mask
		FloatMap extremityHeightMask = MaskGenerator.getExtremityMask(size, seed);
		
		// Blend primary and secondary height masks
		FloatMap heightMap = FloatMap.weightedBlend(heightMask, extremityHeightMask, 0.75F, 0.25F);
		
		//BETA - blend with additive mask
		//heightMap.blend(MaskGenerator.getAdditiveMask(size, seed));
		
		// Blend mask and noise to create height map
		heightMap.blend(heightNoise);
		
		// Add height modifier
		heightMap.addModifier(modHeight, 0.0F, 1.0F);
		heightMap.adjustRange(0, 1);
		heightMap.pruneValues();
		
		/*
		 * Generate moisture map
		 */
		
		//Find polar center coordnates
		Point polarCenter = new Point(randomIntegerWithinRange((size/2), 0.5F),
				randomIntegerWithinRange((size/2), 0.5F));
		
		//Update status and progress
		this.status = "generating moisture noise...";
		this.progress = 50.0D;
		
		// Generate moisture simplex noise seed
		int moistureNoiseSeed = random.nextInt();
		System.out.println("seeding moisture noise with " + moistureNoiseSeed);
		System.out.println("using " + MOISTURE_OCTAVES + " octaves and " + moistureNoisePersistence + " persistence");
		
		//Generate moisture simplex noise
		FloatMap moistureNoise = MaskGenerator.getSimplexNoise(size, moistureNoiseSeed, MOISTURE_OCTAVES, moistureNoisePersistence);
		moistureNoise.adjustRange(0, 1);
		moistureNoise.invert();
		
		//Update status and progress
		this.status = "masking moisture map...";
		this.progress = 75.0D;
		
		// Generate moisture circular mask
		FloatMap moistureMask = MaskGenerator.getDefinedCircularMask(size, polarCenter, (int)(size * 0.8F), modc, 1.0F);
		
		// Blend moisture noise and mask
		FloatMap moistureMap = FloatMap.blend(moistureNoise, moistureMask);
		
		//Add moisture modifier
		moistureMap.addModifier(modMoisture, 0.0F, 1.0F);
		moistureMap.adjustRange(0, 1);
		moistureMap.pruneValues();
		
		/*
		 * Build world from height and moisture maps
		 */

		this.world = new World(size, seed);
		world.setHeightMap(heightMap);
		world.setMoistureMap(moistureMap);
		
		//Update status and progress
		this.status = "assigning biomes...";
		this.progress = 88.0D;

		// Determine biomes
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (Biome b : Biome.values()) {
					for (Range r : b.getRange()) {
						//Find the valid height and moisture range matches to determine the biome of this tile
						if (r.inRange(heightMap.get(x, y), moistureMap.get(x, y))) {
							world.getBiomeMap()[x][y] = b;
						}
					}
				}
			}
		}
		
		//Update status and progress
		this.status = "finalizing...";
		this.progress = 95.0D;

		/*
		 *  Clean up extraneous biome tiles
		 *  
		 *  A tile is considered extraneous if it is isolated surrounded
		 *  by water on all 4 sides
		 */
		for (int x = 1; x < size - 1; x++) {
			for (int y = 1; y < size - 1; y++) {
				Point p = new Point(x, y), p1 = new Point(x - 1, y), p2 = new Point(x + 1, y), p3 = new Point(x, y - 1),
						p4 = new Point(x, y + 1);
				if (world.getBiomeAt(p) != Biome.WATER) {
					//True if a tile is surrounded by water on all 4 sides
					if (world.getBiomeAt(p1) == Biome.WATER && world.getBiomeAt(p2) == Biome.WATER
							&& world.getBiomeAt(p3) == Biome.WATER && world.getBiomeAt(p4) == Biome.WATER) {
						//If surrounded, set to water as well
						world.getBiomeMap()[x][y] = Biome.WATER;
					}
				}
			}
		}
		
		//Write out image of the generated world to the current running directory
		this.worldOutputPath = "mapgen-" + seed + ".png";
		world.writeImage(this.worldOutputPath);
		
		//Update final status and progress
		this.status = "done";
		this.progress = 100.0D;
	}
	
	//Returns the generated world
	public World getWorld() {
		return this.world;
	}

	/*
	 * Utility functions
	 */

	//Returns a random modifier from (+/- randomness of the median) multiplied by the adjustment variable
	private float randomModifier(float median, float adjustment) {
		return (median - (randomness * adjustment))
				+ random.nextFloat() * (median - (median - (randomness * adjustment)));
	}

	//Returns a random integer within (+/- randomness%) of the median
	private int randomIntegerWithinRange(int median, float randomness) {
		int rmin = (int) ((float) median * (1.0F - randomness)), rmax = (int) ((float) median * (1.0F + randomness));
		return random.nextInt(rmax + 1 - rmin) + rmin;
	}
	
	//Returns the world output path
	public String getWorldOutputPath() {
		return this.worldOutputPath;
	}
	
	/*
	 * UI functions
	 */
	
	//Returns progress bar value
	public double getProgress() {
		return this.progress;
	}
	
	//Returns progress bar string
	public String getStatus() {
		return this.status;
	}

}
