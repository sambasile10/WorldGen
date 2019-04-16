package gen;

import java.util.ArrayList;
import java.util.Random;
import ext.SimplexNoise;

public class MaskGenerator {

	/*
	 * Generates masks for noise to be blended with
	 * 
	 * ALL FUNCTIONS ARE DECRIPITATED EXCEPT THE CIRCULAR MASKS
	 */

	// Returns a circular mask with a weaker or a stronger dropoff according to the
	// factors
	// modc = multiplier for distance from center
	public static FloatMap getCircularMask(int size, float modc) {
		float[][] mask = new float[size][size];
		int center = (int) (size / 2);
		float max_width = size * 0.5f - 10.0f;

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				float distance = (float) (Math.hypot(Math.abs((center - y) * modc), Math.abs((center - x) * modc)));
				float delta = distance / max_width;
				float gradient = delta * delta;
				float val = 1.0F - gradient;
				if (val < 0.0F) {
					val = 0.0F;
				}
				mask[x][y] = val;
			}
		}

		FloatMap fmap = new FloatMap(0, 1, size);
		fmap.setMap(mask);
		return fmap;
	}

	public static FloatMap getDefinedCircularMask(int size, Point origin, int radius, float modc, float modg) {
		float[][] mask = new float[size][size];
		float max_width = radius - 10.0f;

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				float distance = (float) (Math.hypot(Math.abs((origin.getY() - y) * modc),
						Math.abs((origin.getX() - x) * modc)));
				float delta = distance / max_width;
				float gradient = delta * delta;
				float val = modg - gradient;
				if (val < 0.0F) {
					val = 0.0F;
				}
				mask[x][y] = val;
			}
		}

		FloatMap fmap = new FloatMap(0, 1, size);
		fmap.setMap(mask);
		return fmap;
	}

	// Wraps the fractal mask method for cleaner access
	public static FloatMap getFractalMask(int size, int seed) {
		// Seed a random
		Random random = new Random(seed);
		float randomness = randomModifier(random, 0.2F, 0.1F);
		// Find start and end points for the island
		Point p1 = new Point(randomIntegerWithinRange(random, (size / 2), 0.8F),
				randomIntegerWithinRange(random, (size / 3), 0.5F));
		Point p2 = new Point(randomIntegerWithinRange(random, (size / 2), 0.8F),
				randomIntegerWithinRange(random, (2 * (size / 3)), 0.6F));

		// Find average width, spread, and modifier distance
		int averageWidth = randomIntegerWithinRange(random, (size / 5), 0.6F);
		int averageSpread = ((int) p1.distance(p2) / (averageWidth - (int) ((float) averageWidth * 0.1F)));
		int mdiff = (int) ((float) averageWidth * randomness);
		float modc = randomModifier(random, 1.0F, randomness), modr = randomModifier(random, 1.0F, randomness),
				modg = randomModifier(random, 1.0F, randomness);

		return getFractalMask(size, seed, p1, p2, averageWidth, averageSpread, mdiff, modc, modr, modg);
	}

	// Returns an oval like fractalular mask
	private static FloatMap getFractalMask(int size, int seed, Point p1, Point p2, int averageWidth, int averageSpread,
			int mdiff, float modc, float modr, float modg) {
		float[][] mask = new float[size][size];
		float lineDistance = p1.distance(p2);
		int cmaskNum = (int) ((int) lineDistance / averageSpread) + 1;
		float distSpread = (float) (lineDistance / (float) cmaskNum);
		Random rnd = new Random(seed);
		ArrayList<FloatMap> masks = new ArrayList<FloatMap>();

		for (int i = 0; i < cmaskNum; i++) {
			int lx = (int) ((float) p1.getX()
					+ ((((float) p2.getX() - (float) p1.getX()) / lineDistance) * (float) (distSpread * i)));
			int ly = (int) ((float) p1.getY()
					+ ((((float) p2.getY() - (float) p1.getY()) / lineDistance) * (float) (distSpread * i)));
			int rad = (int) (((float) averageWidth * (1.0F - modr)) + rnd.nextFloat()
					* (((float) averageWidth * (1.0F + modr)) - ((float) averageWidth * (1.0F - modr))));
			float diffmodc = (1.0F - modc) + rnd.nextFloat() * (1.0F - (1.0F - modc));
			float diffmodg = (1.0F - modg) + rnd.nextFloat() * (1.0F - (1.0F - modg));
			lx += rnd.nextInt(mdiff + 1 - (-mdiff)) + -mdiff;
			ly += rnd.nextInt(mdiff + 1 - (-mdiff)) + -mdiff;
			Point c = new Point(lx, ly);
			// System.out.println(c + ", rad=" + rad + ", modc=" + diffmodc + ", distsp=" +
			// (int)(distSpread * i) + ", modg=" + diffmodg + ", i=" + i);
			FloatMap cmk = MaskGenerator.getDefinedCircularMask(size, c, rad, diffmodc, diffmodg);
			cmk.invert();
			masks.add(cmk);
		}

		FloatMap fmap = masks.get(0);
		for (int j = 1; j < cmaskNum; j++) {
			fmap.blend(masks.get(j));
		}

		fmap.invert();
		return fmap;
	}

	public static FloatMap getScatteredMask(int size, int seed, int averageWidth, int islands, float randomness) {
		float[][] mask = new float[size][size];
		FloatMap[] maps = new FloatMap[islands];
		Random rnd = new Random(seed);

		int rmin = (int) ((float) averageWidth * (1.0F - randomness)),
				rmax = (int) ((float) averageWidth * (1.0F + randomness));
		int omin = (int) ((float) size * 0.1F) + averageWidth,
				omax = (int) (size - averageWidth - ((float) size * 0.1F));
		for (int i = 0; i < islands; i++) {
			Point origin = new Point((rnd.nextInt(omax + 1 - omin) + omin), (rnd.nextInt(omax + 1 - omin) + omin));
			int radius = rnd.nextInt(rmax + 1 - rmin) + rmin;
			float diffmodc = (1.0F - randomness) + rnd.nextFloat() * (1.0F - (1.0F - randomness));
			float diffmodg = (1.0F - randomness) + rnd.nextFloat() * (1.0F - (1.0F - randomness));

			FloatMap island = getDefinedCircularMask(size, origin, radius, diffmodc, diffmodg);
			island.invert();
			maps[i] = island;
		}

		FloatMap fmap = maps[0];
		for (int j = 1; j < islands; j++) {
			fmap.blend(maps[j]);
		}

		fmap.invert();
		return fmap;
	}

	// Returns a FloatMap (size by size) of SimplexNoise from a seed and specified
	// octaves
	// Range is -1.0 to 1.0
	public static FloatMap getSimplexNoise(int size, int seed, int octaves, double persistence) {
		SimplexNoise noise = new SimplexNoise(octaves, persistence, seed);

		float[][] map = new float[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				float val = (float) noise.getNoise2D(x, y);
				if (val > 1.0F) {
					val = 1.0F;
				}
				if (val < -1.0F) {
					val = -1.0F;
				}
				map[x][y] = val;
			}
		}

		FloatMap fmap = new FloatMap(-1, 1, size);
		fmap.setMap(map);
		return fmap;
	}
	
	//Returns a simple scattered mask for extremities
	public static FloatMap getExtremityMask(int size, int seed) {
		Random random = new Random(seed);
		float randomness = randomModifier(random, 0.2F, 0.1F);
		int countPositive = (int) ((float)((size / 16) / 16.0F) * randomModifier(random, 1.0F, randomness) * 0.5F);
		int countNegative = (int) ((float)((size / 16) / 16.0F) * randomModifier(random, 1.0F, randomness) * 0.33F);
		
		System.out.println("extremity count positive=" + countPositive + ", negative=" + countNegative);
		System.out.println("extremity average radius=" + (size / 64));
		
		FloatMap positiveMask = MaskGenerator.getScatteredMask(size, seed, (size / 48), countPositive, randomness);
		FloatMap negativeMask = MaskGenerator.getScatteredMask(size, seed, (size / 64), countNegative, randomness);
		negativeMask.invert();
		
		return FloatMap.blend(positiveMask, negativeMask);
	}
	
	//Returns a modified height map with extra land
	public static FloatMap getAdditiveMask(int size, int seed) {
		Random random = new Random(seed);
		float randomness = randomModifier(random, 0.2F, 0.1F);
		int count = (int) ((float)((size / 16) / 16.0F) * randomModifier(random, 1.0F, randomness) * 0.7F);
		
		System.out.println("additive count positive=" + count);
		System.out.println("additive average radius=" + (size / 64));
		
		FloatMap mask = MaskGenerator.getScatteredMask(size, seed, (size / 48), count, randomness);
		mask.addModifier(0.2F, 0.0F, 1.0F);
		mask.adjustRange(0, 1);
		mask.pruneValues();
		return mask;
	}

	private static float randomModifier(Random random, float median, float randomness) {
		return (median - randomness) + random.nextFloat() * (median - (median - randomness));
	}

	private static int randomIntegerWithinRange(Random random, int median, float randomness) {
		int rmin = (int) ((float) median * (1.0F - randomness)), rmax = (int) ((float) median * (1.0F + randomness));
		return random.nextInt(rmax + 1 - rmin) + rmin;
	}

}
