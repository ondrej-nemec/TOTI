import java.sql.SQLException;

import toti.lib.database.migration.Migration;
import toti.lib.database.querybuilder.QueryBuilder;

public class Java__another_name implements Migration {

	@Override
	public void migrate(QueryBuilder builder) throws SQLException {
		builder.select("Foward");
	}

	@Override
	public void revert(QueryBuilder builder) throws SQLException {
		builder.select("Revert");
	}

}
