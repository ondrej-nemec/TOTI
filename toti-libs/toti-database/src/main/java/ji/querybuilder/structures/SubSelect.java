package ji.querybuilder.structures;

public interface SubSelect {
	
	String getSql();
	
	String createSql();
	
	default boolean wrap() {
		return true;
	}

}
