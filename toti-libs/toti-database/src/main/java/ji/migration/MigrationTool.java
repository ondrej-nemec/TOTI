package ji.migration;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.files.text.Text;
import ji.migration.migrations.JavaMigration;
import ji.migration.migrations.SqlMigration;
import ji.migration.resources.IdSeparator;
import ji.migration.resources.MigrationFiles;
import ji.migration.resources.MigrationPreparation;
import ji.querybuilder.QueryBuilder;

public class MigrationTool {
	
	private final static String MIGRATION_TABLE = "migrations";
	private final static String ALLWAYS_ID = "ALWAYS";
	private static final String SEPARATOR = "__";
	
	private final Logger logger;
	private final QueryBuilder builder;
	private final List<String> folders;
	
	public MigrationTool(List<String> folders, QueryBuilder builder, Logger logger) {
		this.logger = logger;
		this.folders = folders;
		this.builder = builder;
	}

	public void migrate() throws Exception {
		process(folders, false, builder, (filesToMigrate, single, builder)->{
			for (int i = 0; i < filesToMigrate.size(); i++) {
    			single.transaction(filesToMigrate.get(i), builder, false);
    		}
		});
	}
	
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

	private void process(List<String> folders, boolean isRevert, QueryBuilder builder, MigrationProcess process) throws Exception {
		for (String folder : folders) {
			MigrationFiles files = new MigrationFiles(folder);
			MigrationPreparation prep = new MigrationPreparation(MIGRATION_TABLE, SEPARATOR);
			List<String> filesToMigrate = prep.getFilesToMigrate(folder, files.getFiles(), isRevert, builder);
			URL[] urls = new URL[]{files.getDir()};
			
			// each migration
			try (URLClassLoader loader = new URLClassLoader(urls);) {
	    		SqlMigration sql = new SqlMigration(folder, isRevert, Text.get());
	    		JavaMigration java = new JavaMigration(folder, loader, isRevert/*, files.isInClasspath()*/);
				SingleMigrationTool tool = new SingleMigrationTool(folder, MIGRATION_TABLE, ALLWAYS_ID, SEPARATOR, java, sql, logger);
	    		process.process(filesToMigrate, tool, builder);
	    	}
		}
	}

}
