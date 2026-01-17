package toti.lib.database.migration;

import java.util.List;

import toti.lib.database.migration.migrations.MigrationInternal;
import toti.lib.common.structures.SortedMap;

public interface MigrationProcess {

	List<MigrationInternal> getFilesToMigrate(
		SortedMap<String, MigrationInternal> filesMigrations,
		List<String> savedMirations
	) throws Exception;
	
}
