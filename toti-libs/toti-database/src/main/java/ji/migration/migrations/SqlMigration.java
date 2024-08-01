package ji.migration.migrations;

import java.io.IOException;
import java.sql.Statement;

import ji.common.functions.InputStreamLoader;
import ji.files.text.Text;
import ji.querybuilder.QueryBuilder;

public class SqlMigration implements SingleMigration {

	private final String path; // where migrations are
	
	private final boolean isRevert;
	
	private final Text text;
	
	public SqlMigration(String path, boolean isRevert, Text text) {
		this.path = path;
		this.isRevert = isRevert;
		this.text = text;
	}

	@Override
	public void migrate(String name, QueryBuilder builder) throws Exception {
		String[] batches = loadContent(path + "/" + name, isRevert).split(";");
		try (Statement stat = builder.getConnection().createStatement()) {
			for (String batch : batches) {
				if (!batch.trim().isEmpty()) {
					stat.addBatch(batch.trim());
				}
			}
			stat.executeBatch();
		}
	}

	private String loadContent(String file, boolean isRevert) throws IOException {
		String sql = text.read((br)->br.asString(), InputStreamLoader.createInputStream(getClass(), file));
		
		String[] mig = sql.split("--- REVERT ---");
		if (isRevert && mig.length > 1) {
			return mig[1];
		} else if (isRevert && mig.length < 2) {
			throw new RuntimeException(String.format("Migration %s has not revert part", file));
		} else if (!isRevert && mig.length == 1) {
			return sql.toString();
		} else if (!isRevert && mig.length > 1) {
			return mig[0];
		}
		return sql.toString();
	}
}
