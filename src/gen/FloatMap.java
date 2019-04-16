package gen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FloatMap {

	/*
	 * Complex wrapper class for a simple 2-dimensional array of floats Minimum and
	 * maximum values allow for range adjustment and other forms of modification
	 * Blending occurs in many methods in this class
	 */

	// range of float values
	private int size;
	private float min, max;

	// Float values stored
	private float[][] map;

	// FloatMap constructor, requires a min and max float value and a size
	public FloatMap(float min, float max, int size) {
		this.min = min;
		this.max = max;
		this.size = size;

		this.map = new float[size][size];
	}

	// Sets all float data
	public void setMap(float[][] map) {
		this.map = map;
	}

	// Sets specific float value at position
	public void set(int x, int y, float value) {
		this.map[x][y] = value;
	}

	// Returns the entire value map
	public float[][] getMap() {
		return this.map;
	}

	// Returns a float value at a specific point
	public float get(int x, int y) {
		return this.map[x][y];
	}

	// Returns a float value at a specific point using the Point declaration
	public float get(Point p) {
		return this.map[p.getX()][p.getY()];
	}

	// Returns minimum value
	public float getMinimum() {
		return this.min;
	}

	// Returns maximum value
	public float getMaximum() {
		return this.max;
	}

	// Returns size of the float map
	public int getSize() {
		return this.size;
	}

	// Sets the minimum and maximum values without adjusting
	public void setRange(float nmin, float nmax) {
		this.min = nmin;
		this.max = nmax;
	}

	// Adjusts range of float values on exact scale
	public void adjustRange(float nmin, float nmax) {
		float[][] scaled = new float[size][size];
		// Loop through all values
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				// Rescale value
				scaled[x][y] = (float) ((float) ((nmax - nmin) * (float) (map[x][y] - min)) / (float) (max - min))
						+ (float) nmin;
			}
		}

		// Set globals
		this.map = scaled;
		this.min = nmin;
		this.max = nmax;
	}

	// Inverts the float map
	/*
	 * Example: if the min is 0.0 and the max is 1.0 a value of 0.4 would become 0.6
	 * a value of 0.15 would be 0.85, etc
	 */
	public void invert() {
		float[][] invert = new float[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				invert[x][y] = (float) (max - map[x][y]);
			}
		}

		this.map = invert;
	}

	// Blends two float maps by multiplying the values
	public void blend(FloatMap bmap) {
		float[][] blend = new float[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				blend[x][y] = (this.get(x, y) * bmap.get(x, y));
			}
		}

		this.map = blend;
	}

	/*
	 * public void weightedBlend(FloatMap bmap, float w1, float w2) { float[][]
	 * blend = new float[size][size]; for(int x = 0; x < size; x++) { for(int y = 0;
	 * y < size; y++) { blend[x][y] = (this.get(x, y) * w1) + (bmap.get(x, y) * w2);
	 * } }
	 * 
	 * this.map = blend; }
	 */

	public void blendEmpty(FloatMap bmap) {
		float[][] blend = new float[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (bmap.get(x, y) == 0.0F && this.get(x, y) == 0.0F) {
					blend[x][y] = 0.0F;
				} else if (bmap.get(x, y) > 0.0F && this.get(x, y) == 0.0F) {
					blend[x][y] = bmap.get(x, y);
				} else if (bmap.get(x, y) == 0.0F && this.get(x, y) > 0.0F) {
					blend[x][y] = this.get(x, y);
				} else {
					blend[x][y] = (this.get(x, y) * bmap.get(x, y));
				}
			}
		}

		this.map = blend;
	}

	public void blendAdditive(FloatMap bmap) {
		float[][] blend = new float[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (bmap.get(x, y) == 0.0F) {
					blend[x][y] = this.get(x, y);
				} else if (this.get(x, y) == 0.0F) {
					blend[x][y] = bmap.get(x, y);
				} else {
					blend[x][y] = (this.get(x, y) * bmap.get(x, y));
				}
			}
		}

		this.map = blend;
	}

	// Add a modifier (mod) to value if within range of [min, max]
	public void addModifier(float mod, float min, float max) {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (map[x][y] >= min && map[x][y] <= max) {
					map[x][y] += mod;
				}
			}
		}
	}

	public void normalize(int radius, int spacing, float tolerance) throws Exception {
		// int sec = (spacing / radius);
		ArrayList<ArrayList<Point>> circles = new ArrayList<ArrayList<Point>>();
		for (int x1 = spacing; x1 < size; x1 += spacing) {
			for (int y1 = spacing; y1 < size; y1 += spacing) {
				if (x1 > size || y1 > size) {
					continue;
				}
				Point c = new Point(x1, y1);
				ArrayList<Point> circle = new ArrayList<Point>();
				circle.add(c);
				for (int x = (x1 - radius); x < (x1 + radius); x++) {
					for (int y = (y1 - radius); y < (y1 + radius); y++) {
						if (x > (size - 1) || y > (size - 1) || x < 0 || y < 0) {
							continue;
						}
						Point c2 = new Point(x, y);
						if (c2.distance(c) <= (float) radius) {
							circle.add(c2);
						}
					}
				}
				// System.out.println("normalize cfind: (" + circle.get(0).getX() + "," +
				// circle.get(0).getY());
				circles.add(circle);
			}
		}

		for (ArrayList<Point> circle : circles) {
			// System.out.println("normalize: c=(" + circle.get(0).getX() + ", " +
			// circle.get(0).getY() + "), r=" + radius);

			float average = 0.0F;
			for (Point p1 : circle) {
				average += get(p1.getX(), p1.getY());
			}

			average /= (float) circle.size();
			for (Point p : circle) {
				float val = get(p.getX(), p.getY());
				float distance = p.distance(circle.get(0));
				float mod = 1.0F - (distance / (float) radius);
				float adj = tolerance;
				if (val > average) {
					if (adj > (val - tolerance)) {
						adj = (val - tolerance);
					}

					adj = (float) (Math.abs(adj * mod));
				} else if (val < average) {
					if (adj > (val + tolerance)) {
						adj = (val + tolerance);
					}

					adj = (float) (Math.abs(adj * mod));
				}

				if (!(adj >= 0.0F && adj <= 1.0F)) {
					System.out.println("adj OOR @ " + p);
					System.out.println("val=" + val + ", dist=" + distance + ", mod=" + mod + ", adj=" + adj);
					throw new Exception();
				}

				set(p.getX(), p.getY(), adj);
			}
		}
	}

	public BufferedImage getGreyscaleImage() {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int rgb = (int) (255 * map[x][y]);
				image.setRGB(x, y, new Color(rgb, rgb, rgb).getRGB());
			}
		}

		return image;
	}

	public BufferedImage getRGBImage() {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				float val = this.get(x, y);
				Color c = Color.PINK;
				if (val >= 0.85F) {
					c = Color.RED;
				} else if (val < 0.85F && val >= 0.7F) {
					c = Color.ORANGE;
				} else if (val < 0.7F && val >= 0.55F) {
					c = Color.YELLOW;
				} else if (val < 0.55F && val >= 0.35F) {
					c = Color.GREEN;
				} else if (val < 0.35F && val >= 0.15F) {
					c = Color.BLUE;
				} else if (val < 0.15F && val >= 0.05F) {
					c = Color.DARK_GRAY;
				} else if (val < 0.05F) {
					c = Color.BLACK;
				}

				image.setRGB(x, y, c.getRGB());
			}
		}

		return image;
	}

	public static FloatMap weightedBlend(FloatMap fmap, FloatMap bmap, float w1, float w2) {
		float[][] blend = new float[bmap.getSize()][bmap.getSize()];
		for (int x = 0; x < bmap.getSize(); x++) {
			for (int y = 0; y < bmap.getSize(); y++) {
				blend[x][y] = (fmap.get(x, y) * w1) + (bmap.get(x, y) * w2);
			}
		}

		FloatMap blendmp = new FloatMap(fmap.getMinimum(), fmap.getMaximum(), fmap.getSize());
		blendmp.setMap(blend);
		return blendmp;
	}

	// Blend without modifying one map, maps must be same size, same min and max
	public static FloatMap blend(FloatMap fmp1, FloatMap fmp2) {
		int size = fmp1.getSize();
		float[][] blend = new float[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				blend[x][y] = (fmp1.get(x, y) * fmp2.get(x, y));
			}
		}

		FloatMap fmap = new FloatMap(fmp1.getMinimum(), fmp1.getMaximum(), size);
		fmap.setMap(blend);
		return fmap;
	}

	// Force all values within range, loses accuracy
	public void pruneValues() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (get(x, y) > max) {
					set(x, y, max);
				} else if (get(x, y) < min) {
					set(x, y, min);
				}
			}
		}
	}

}
