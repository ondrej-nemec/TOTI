package toti.lib.database.querybuilder.structures.preparedQueries;

public record Sort(String name, boolean isDesc) {

	@Override
	public String toString() {
		return "Sort [name=" + name + ", isDesc=" + isDesc + "]";
	}

}
