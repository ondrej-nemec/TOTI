package ji.migration;

import java.util.List;

import ji.querybuilder.QueryBuilder;

public interface MigrationProcess {

	void process(List<String> filesToMigrate, SingleMigrationTool tool, QueryBuilder builder) throws Exception;
	
}
