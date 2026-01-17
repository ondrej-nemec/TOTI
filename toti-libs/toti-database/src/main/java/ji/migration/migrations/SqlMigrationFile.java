package ji.migration.migrations;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import toti.files.text.Text;
import toti.lib.common.functions.InputStreamLoader;
import ji.querybuilder.QueryBuilder;

public class SqlMigrationFile implements MigrationFile {
	
	private final String path;
	
	public SqlMigrationFile(String path) {
		this.path = path;
	}

	@Override
	public void migrate(QueryBuilder builder, boolean isRevert) throws Exception {
		migrate(builder, loadContent(isRevert));
	}
	
	protected void migrate(QueryBuilder builder, String loadedText) throws SQLException {
		// TODO use BatchBuilder
		String[] batches = loadedText.split(";");
		try (Statement stat = builder.getConnection().createStatement()) {
			for (String batch : batches) {
				if (!batch.trim().isEmpty()) {
					stat.addBatch(batch.trim());
				}
			}
			stat.executeBatch();
		}
	}

	protected String loadContent(boolean isRevert) throws IOException {
		String sql = Text.get().read((br)->br.asString(), InputStreamLoader.createInputStream(getClass(), path));
		
		String[] mig = sql.split("--- REVERT ---");
		if (isRevert && mig.length > 1) {
			return mig[1].trim();
		} else if (isRevert && mig.length < 2) {
			throw new RuntimeException(String.format("Migration %s has not revert part", path));
	//	} else if (!isRevert && mig.length == 1) {
	//		return sql.toString().trim();
		} else if (!isRevert && mig.length > 1) {
			return mig[0].trim();
		}
		return sql.toString().trim();
	}

	@Override
	public String toString() {
		return "SqlMigrationFile [path=" + path + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SqlMigrationFile other = (SqlMigrationFile) obj;
		return Objects.equals(path, other.path);
	}

}
