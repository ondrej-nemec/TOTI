package ji.migration.migrations;

import ji.querybuilder.QueryBuilder;

public interface SingleMigration {
	
	void migrate(String name, QueryBuilder builder) throws Exception;

}
