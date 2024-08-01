package ji.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.apache.logging.log4j.Logger;
import ji.common.functions.FileExtension;
import ji.migration.migrations.JavaMigration;
import ji.migration.migrations.SqlMigration;
import ji.migration.resources.IdSeparator;
import ji.querybuilder.QueryBuilder;

public class SingleMigrationTool {
	
	private final String allwaysId;
	
	private final String migrationTable;
	
	private final String separator;
	
	private final Logger logger;
	
	private final SqlMigration sql;
	
	private final JavaMigration java;
	
	private final String module;
		
	public SingleMigrationTool(
			String module, String migrationTable, String allwaysId, String separator,
			JavaMigration java, SqlMigration sql, Logger logger) {
		this.logger = logger;
		this.migrationTable = migrationTable;
		this.java = java;
		this.sql = sql;
		this.allwaysId = allwaysId;
		this.separator = separator;
		this.module = module;
	}

	public void transaction(String file, QueryBuilder builder, boolean isRevert) throws Exception {
		Connection con = builder.getConnection();
		try {
			con.setAutoCommit(false);
			FileExtension ex = new FileExtension(file);
			switch (ex.getExtension()) {
			case "sql":
				sql.migrate(ex.getName() + ".sql", builder);
				break;
			case "class":
			case "java":
				java.migrate(ex.getName(), builder);
				break;
			}
			IdSeparator id = new IdSeparator(ex.getName(), separator);
			if (!id.getId().contains(allwaysId)) {
				idToDb(id.getId(), id.getDesc(), module, builder, isRevert);
			}
			con.commit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		}
	}
	
	private void idToDb(String id, String description, String module, QueryBuilder builder, boolean isRevert) throws SQLException {
		if (isRevert) {
			logger.warn("Migration reverted: " + id);
			builder.delete(migrationTable)
				.where("id = :id").addParameter(":id", id)
				.where("module = :module").addParameter(":module", module)
				.execute();
		} else {
			builder
	    		.insert(migrationTable)
	    		.addValue("module", module)
	    		.addValue("id", id)
	    		.addValue("description", description)
	    		//.addValue("datetime", common.functions.DateTime.format("yyyy-MM-dd HH:mm:ss.SSS"))
	    		.addValue("datetime", LocalDateTime.now())
	    		.execute();
		}
	}

}
