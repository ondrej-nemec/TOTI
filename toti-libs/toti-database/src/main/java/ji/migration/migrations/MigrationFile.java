package ji.migration.migrations;

import ji.querybuilder.QueryBuilder;

public interface MigrationFile {

	void migrate(QueryBuilder builder, boolean isRevert) throws Exception;
	
}
