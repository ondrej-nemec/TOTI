package ji.migration;

import java.sql.SQLException;

import ji.querybuilder.QueryBuilder;

public interface Migration {
	
	void migrate(QueryBuilder builder) throws SQLException;
	
	default void revert(QueryBuilder builder) throws SQLException {}

}
