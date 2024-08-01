package ji.migration.resources;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import ji.migration.MigrationException;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;

public class MigrationPreparation {
	
	private final String migrationTable;
	
	private final String separator;
	
	public MigrationPreparation(String migrationTable, String idSeparator) {
		this.migrationTable = migrationTable;
		this.separator = idSeparator;
	}

	public List<String> getFilesToMigrate(String module, List<String> loadedFiles, boolean isRevert, QueryBuilder builder) throws SQLException, MigrationException {
		// validace
		List<String> migrated = selectMigrations(builder, module);
		if (migrated.size() > loadedFiles.size()) {
			throw new MigrationException();
		}
		
		int indexOfLastMigrated = indexOfLastMigrated(loadedFiles, migrated);
		// no migration in db - no to revert, all for migrate
		if (indexOfLastMigrated == -1) {
			return (isRevert ? new LinkedList<>() : loadedFiles);
		}
		// all migrated - no for migrate, all for revert
		if (indexOfLastMigrated + 1 == loadedFiles.size()) {
			return (isRevert ? loadedFiles : new LinkedList<>());
		}
		if (isRevert) {
			return loadedFiles.subList(0, indexOfLastMigrated + 1);
		} else {
			return loadedFiles.subList(indexOfLastMigrated + 1, loadedFiles.size());
		}
	}
	
	private int indexOfLastMigrated(List<String> loadedFiles, List<String> migrated) throws MigrationException {
		int index = -1;
		for (int i = 0; i < migrated.size(); i++) {
			String fileId = new IdSeparator(loadedFiles.get(i), separator).getId();
			if (!migrated.get(i).equals(fileId)) {
				throw new MigrationException("Last migrated id: " + fileId + ", migrated: " + migrated);
			}
			index++;
		}
		return index;
	}
	
	private List<String> selectMigrations(QueryBuilder builder, String module) throws SQLException {
		try {
			return builder
				.select("id")
				.from(migrationTable)
				.where("module = :module")
				.addParameter(":module", module)
				.orderBy("datetime, id")
				.fetchAll((row)->{
					return row.getValue("id").toString();
				});
		} catch (Exception ignored) {
			builder.rollback();
			builder
				.createTable(migrationTable)
				.addColumn("module", ColumnType.string(400), ColumnSetting.NULL)
				.addColumn("id", ColumnType.string(100), ColumnSetting.NOT_NULL)
				.addColumn("description", ColumnType.string(100), ColumnSetting.NOT_NULL)
				.addColumn("datetime", ColumnType.string(100), ColumnSetting.NOT_NULL)
				.setPrimaryKey("id", "module")
				.execute();
		}
		return new LinkedList<>();
	}

}
