package ji.migration.migrations;

import java.sql.SQLException;

import ji.migration.Migration;
import ji.querybuilder.QueryBuilder;

public class Java__in_classpath implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.select("Foward");
	}

	@Override
	public void revert(QueryBuilder builder) throws SQLException {
		builder.select("Revert");
	}

}
