package ji.migration;

import java.util.List;

import ji.common.structures.SortedMap;
import ji.migration.migrations.MigrationInternal;

public interface MigrationProcess {

	List<MigrationInternal> getFilesToMigrate(
		SortedMap<String, MigrationInternal> filesMigrations,
		List<String> savedMirations
	) throws Exception;
	
}
