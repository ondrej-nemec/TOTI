package toti.lib.database.migration;

import java.sql.SQLException;

import toti.lib.database.querybuilder.QueryBuilder;

public interface Migration {
	
	void migrate(QueryBuilder builder) throws SQLException;
	
	default void revert(QueryBuilder builder) throws SQLException {}

}
