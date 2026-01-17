package toti.lib.database.migration.migrations;

import toti.lib.database.querybuilder.QueryBuilder;

public interface MigrationFile {

	void migrate(QueryBuilder builder, boolean isRevert) throws Exception;
	
}
