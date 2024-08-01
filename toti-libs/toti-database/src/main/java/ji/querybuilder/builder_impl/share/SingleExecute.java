package ji.querybuilder.builder_impl.share;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import ji.database.wrappers.StatementWrapper;

public interface SingleExecute {

	default int execute(Connection connection, String query, Map<String, String> parameters) throws SQLException {
		try (Statement stat = connection.createStatement();) {
			if (stat instanceof StatementWrapper) {
				StatementWrapper w = StatementWrapper.class.cast(stat);
				w.getProfiler().builderQuery(w.ID, query, query, parameters);
			}
			return stat.executeUpdate(query);
		}
	}
	
}
