//package migration.path_files_res;

import java.sql.SQLException;

import migration.Migration;
import querybuilder.QueryBuilder;

public class M_D_1__java_dir implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.createTable("dir_java").execute();
	}

	@Override
	public void revert(QueryBuilder builder) throws SQLException {}

}
