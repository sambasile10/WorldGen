package gen;

public enum GenerationFlag {
	
	/*
	 * Generation flags affect how the generator functions
	 * Currently only Pangea is implemented
	 * 
	 * Pangea forces the radius of the island to increase by a factor of 2
	 * causing land to cover the entire world without any ocean (hence the name)
	 */
	
	//Constant generation flags
	PANGEA(0, "Pangea"), POLAR(1, "Polar Ice Cap"), LAKES(2, "Inland Lakes");
	
	//ID and type name of the flag
	private int id;
	private String name;
	
	//Flag constructor
	GenerationFlag(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	//Returns the ID of the flag
	int getFlagID() {
		return this.id;
	}
	
	//Returns the type name of the flag
	String getFlagName() {
		return this.name;
	}

}
