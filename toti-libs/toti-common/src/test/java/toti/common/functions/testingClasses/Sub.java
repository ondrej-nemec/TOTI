package toti.common.functions.testingClasses;

public class Sub {
	private String text1 = "text1";
	private String text2 = "text2";

	@Override 
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}
	
	@Override 
	public String toString() {
		return "Sub: " + text1 + " - " + text2;
	}
}
