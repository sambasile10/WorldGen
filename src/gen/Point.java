package gen;

import java.util.Random;

public class Point {
	
	/*
	 * Simple object that defines an integer coordnate
	 */
	
	//x and y values
	private int x, y;
	
	//Point constructor, takes in integers x and y
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	//Returns x value
	public int getX() {
		return x;
	}

	//Sets new X value
	public void setX(int x) {
		this.x = x;
	}

	//Returns y value
	public int getY() {
		return y;
	}

	//Sets new Y value
	public void setY(int y) {
		this.y = y;
	}
	
	//Returns the distance between two points
	public float distance(Point p2) {
		//Use hypotenuse length between two points
		return (float)(Math.hypot(Math.abs((p2.getY() - y)), Math.abs((p2.getX() - x))));
	}
	
	//Returns a random point within [distance] of the current point with min and max bounds for both x and y axis
	public Point getRandomPointWithinDistance(Random rnd, float distance, int minBound, int maxBound) {
		Point point = new Point(0, 0);
		boolean r = true;
		while(r) {
			//Get a random vector
			float vector = rnd.nextFloat() * 355.0F;
			
			//Get point [distance] length along line from this point
			int x = (int) ((float)this.getX() + ((distance / 2.0F) * (float)Math.cos(Math.toRadians((double)vector))));
			int y = (int) ((float)this.getY() + ((distance / 2.0F) * (float)Math.sin(Math.toRadians((double)vector))));
			System.out.println("min=" + minBound + ", max=" + maxBound);
			System.out.println("rnd-point(" + x + ", " + y + ")");
			
			//Check bounds
			if(!(x < minBound || y < minBound || x > maxBound || y > maxBound)) {
				System.out.println("found point");
				point = new Point(x, y);
				r = false;
				break;
			}
		}
		
		return point;
	}
	
	//Returns a random point within min and max bounds on the x and y
	//Accessed statically, no relation to any other Point objects
	public static Point getRandomPointInRange(Random rnd, int minBound, int maxBound) {
		int x = rnd.nextInt((maxBound - minBound) + 1) + minBound;
		int y = rnd.nextInt((maxBound - minBound) + 1) + minBound;
		return new Point(x, y);
	}
	
	//Custom string return for a Point object
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
