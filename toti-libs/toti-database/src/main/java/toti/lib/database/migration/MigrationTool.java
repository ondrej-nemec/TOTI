package toti.lib.database.migration;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;

import toti.lib.database.migration.migrations.JavaMigrationFile;
import toti.lib.database.migration.migrations.MigrationInternal;
import toti.lib.database.migration.migrations.SqlMigrationFile;
import toti.lib.database.querybuilder.QueryBuilder;
import toti.lib.database.querybuilder.enums.ColumnSetting;
import toti.lib.database.querybuilder.enums.ColumnType;
import toti.lib.common.functions.FileExtension;
import toti.lib.common.functions.FilesList;
import toti.lib.common.functions.compiling.Compiler;
import toti.lib.common.structures.SortedMap;

public class MigrationTool {

	private static final String SEPARATOR = "__";
	private final static String MIGRATION_TABLE = "migrations";
	
	private final Logger logger;
	private final QueryBuilder builder;
	private final List<String> folders;
	
	public MigrationTool(List<String> folders, QueryBuilder builder, Logger logger) {
		this.logger = logger;
		this.folders = folders;
		this.builder = builder;
	}

	public void migrate() throws Exception {
		process(folders, false, builder, (filesMigrations, savedMirations)->{
			return getFowardMigrations(filesMigrations, savedMirations);
		});
	}
	
	protected List<MigrationInternal> getFowardMigrations(
		SortedMap<String, MigrationInternal> filesMigrations,
		List<String> savedMirations
	) throws MigrationException {
		List<MigrationInternal> toMigrate = new LinkedList<>();
		int index = 0;
		for (String savedMigration : savedMirations) {
			MigrationInternal migration = null;
			while(migration == null && index < filesMigrations.size()) {
				MigrationInternal step = filesMigrations.getByIndex(index++);
				if (step.isAlways()) {
					toMigrate.add(step);
				} else {
					migration = step;
				}
			}
			if (migration == null) {
				throw new MigrationException("Migration already processed, but file is missing: " + savedMigration);
			}
			if (!savedMigration.equals(migration.getId())) {
				throw new MigrationException(
					"Gap in migration (module=" + migration.getModule() + "), expected " + savedMigration + ", but was " + migration.getId()
				);
			}
		}
		for (int i = index; i < filesMigrations.size(); i++) {
			toMigrate.add(filesMigrations.getByIndex(i));
		}
		return toMigrate;
	}
	
// TODO dobre vymyslet
/*
	public void revert() throws Exception {
		process(folders, true, builder, (filesToMigrate, single, builder)->{
			for (int i = filesToMigrate.size(); i > 0; i--) {
				single.transaction(filesToMigrate.get(i-1), builder, true);
			}
		});
	}
	
	public void revert(String id) throws Exception {
		process(folders, true, builder, (filesToMigrate, single, builder)->{
			for (int i = filesToMigrate.size(); i > 0; i--) {
    			if (new IdSeparator(filesToMigrate.get(i-1), SEPARATOR).getId().equals(id)) {
    				return;
    			}
    			single.transaction(filesToMigrate.get(i-1), builder, true);
    		}
		});
	}
	
	public void revert(int steps) throws Exception {
		process(folders, true, builder, (filesToMigrate, single, builder)->{
			for (int i = filesToMigrate.size(); filesToMigrate.size() - i < steps; i--) {
    			single.transaction(filesToMigrate.get(i-1), builder, true);
    		}
		});
	}
*/
	// TODO test
	protected void process(List<String> folders, boolean isRevert, QueryBuilder builder, MigrationProcess process) throws Exception {
		for (String folder : folders) {
			FilesList filesList = FilesList.get(folder, false);
			List<MigrationInternal> toMigrate = getFowardMigrations(
				processFiles(
					folder, filesList.getFiles(), Thread.currentThread().getContextClassLoader()
				),
				selectMigrations(builder, folder)
			);
			for (MigrationInternal migrationFile : toMigrate) {
				transaction(migrationFile, builder, isRevert);
			}
		}
	}

	// TODO test
	protected void transaction(MigrationInternal migrationFile, QueryBuilder builder, boolean isRevert) throws Exception {
		Connection con = builder.getConnection();
		try {
			con.setAutoCommit(false);
			migrationFile.migrate(builder, isRevert);
			if (!migrationFile.isAlways()) {
				idToDb(
					migrationFile.getId(), migrationFile.getDesc(),
					migrationFile.getModule(), builder, isRevert
				);
			}
			con.commit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}
	}

	// TODO test
	protected void idToDb(String id, String description, String module, QueryBuilder builder, boolean isRevert) throws SQLException {
		if (isRevert) {
			logger.warn("Migration reverted: " + id);
			builder.delete(MIGRATION_TABLE)
				.where("id = :id").addParameter(":id", id)
				.where("module = :module").addParameter(":module", module)
				.execute();
		} else {
			builder
	    		.insert(MIGRATION_TABLE)
	    		.addValue("module", module)
	    		.addValue("id", id)
	    		.addValue("description", description)
	    		//.addValue("datetime", common.functions.DateTime.format("yyyy-MM-dd HH:mm:ss.SSS"))
	    		.addValue("datetime", LocalDateTime.now())
	    		.execute();
		}
	}

	/******************************************/
	
	// TODO test
	protected List<String> selectMigrations(QueryBuilder builder, String module) throws SQLException {
		try {
			return builder.select("id")
			.from(MIGRATION_TABLE)
			.where("module = :module")
			.addParameter(":module", module)
			.orderBy("datetime, id")
			.fetchAll((row)->{
				return row.getValue("id").toString();
			});
		} catch (Exception ignored) {
			builder.rollback();
			builder.createTable(MIGRATION_TABLE)
			.addColumn("module", ColumnType.string(400), ColumnSetting.NOT_NULL)
			.addColumn("id", ColumnType.string(100), ColumnSetting.NOT_NULL)
			.addColumn("description", ColumnType.string(100), ColumnSetting.NOT_NULL)
			.addColumn("datetime", ColumnType.string(100), ColumnSetting.NOT_NULL)
			.setPrimaryKey("id", "module")
			.execute();
		}
		return new LinkedList<>();
	}
	
	/***********************/

	protected SortedMap<String, MigrationInternal> processFiles(String folder, List<String> files, ClassLoader loader) throws MigrationException {
		// JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		Compiler compiler = new Compiler(logger);
		SortedMap<String, MigrationInternal> loadedFiles = new SortedMap<>();
		for (String fileName : files) {
			File file = new File(folder + "/" + fileName);
			FileExtension fe = new FileExtension(fileName);
			
			MigrationInternal migration = createMigration(fe.getName(), folder);
			switch (fe.getExtension()) {
				case "java":
					if (loadedFiles.containsKey(migration.getId())) {
						// if folder contains .class and .java files
						break;
					}
					// compiler.run(null, null, null, file.getPath()); // null - stream where log is written
					Optional<String> res = compiler.compile(file, folder, fileName);
					if (res.isPresent()) {
						throw new MigrationException(res.get());
					}
					// continue with "class" logic
				case "class":
					migration.setFile(new JavaMigrationFile(
						parseJavaPath(folder), fe.getName(), loader
					));;
					break;
				case "sql":
					migration.setFile(new SqlMigrationFile(folder + "/" + fileName));
					break;
				default: break;
			}
			if (migration.getFile() != null) {
				loadedFiles.put(migration.getId(), migration);
			}
		}
		return loadedFiles;
	}

	protected MigrationInternal createMigration(String name, String module) throws MigrationException {
		if (!name.contains(SEPARATOR)) {
			throw new MigrationException(
				"File name is in incorrect format: " + name + ", separator is required: " + SEPARATOR
			);
		}
		String[] aux = new FileExtension(name).getName().split(SEPARATOR);
		if (aux.length == 2) {
			return new MigrationInternal(aux[0], aux[1], module);
		} else {
			throw new MigrationException(
				"File name is in incorrect format: " + name + ", required format: "
				+ String.format("<id>%s<description>", SEPARATOR)
			);
		}
	}
	
	protected String parseJavaPath(String path) {
		return path.replaceAll("\\\\", ".").replaceAll("/", ".");
	}

}
