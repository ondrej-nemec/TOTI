package ji.migration;

import java.util.List;

import ji.migration.migrations.MigrationInternal;
import toti.common.structures.SortedMap;

public interface MigrationProcess {

	List<MigrationInternal> getFilesToMigrate(
		SortedMap<String, MigrationInternal> filesMigrations,
		List<String> savedMirations
	) throws Exception;
	
}
