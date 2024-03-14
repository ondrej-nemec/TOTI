package toti.ui.backend.grid;

public class Sort {

	private String name;
	private boolean isDesc;
	
	public Sort() {}

	public Sort(String name, boolean isDesc) {
		this.name = name;
		this.isDesc = isDesc;
	}

	public String getName() {
		return name;
	}

	public boolean isDesc() {
		return isDesc;
	}

	@Override
	public String toString() {
		return "Sort [name=" + name + ", isDesc=" + isDesc + "]";
	}

}
