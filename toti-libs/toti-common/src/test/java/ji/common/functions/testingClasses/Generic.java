package ji.common.functions.testingClasses;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ji.common.structures.MapInit;

public class Generic {

	private List<Map<String, String>> generic;
	
	public Generic() {}

	public Generic(boolean init) {
		this.generic = Arrays.asList(
			new MapInit<String, String>()
			.append("a", "b")
			.toMap()
		);
	}
	
	@Override 
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}
	
	@Override
	public String toString() {
		return String.format("Generic: %s", generic);
	}
	
}
