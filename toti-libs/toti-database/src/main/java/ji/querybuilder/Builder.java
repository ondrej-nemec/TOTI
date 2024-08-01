package ji.querybuilder;

public interface Builder {

	String getSql();

	default String createSql() {
		return getSql();
	}
	
}
