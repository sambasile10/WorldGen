package runtime;

public enum WorldSize {
	
	TINY("Tiny (512)", 0, 512), SMALL("Small (1024)", 1, 1024), STANDARD("Standard (2048)", 2, 2048),
		LARGE("Large (4096)", 3, 4096), MASSIVE("Massive (8192)", 4, 8192);
	
	private int id, size;
	private String name;
	
	WorldSize(String name, int id, int size) {
		this.id = id;
		this.name = name;
		this.size = size;
	}
	
	String getTypeName() {
		return this.name;
	}
	
	public int getSize() {
		return this.size;
	}
	
	int getID() {
		return this.id;
	}
}
