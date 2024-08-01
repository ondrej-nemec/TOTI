import java.sql.SQLException;

import migration.Migration;
import querybuilder.QueryBuilder;

public class Java implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.select("foward");
	}

	@Override
	public void revert(QueryBuilder builder) throws SQLException {
		builder.select("revert");
	}

}
