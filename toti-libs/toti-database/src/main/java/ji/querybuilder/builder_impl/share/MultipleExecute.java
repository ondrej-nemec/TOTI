package ji.querybuilder.builder_impl.share;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import ji.database.wrappers.StatementWrapper;
import toti.lib.common.structures.ThrowingConsumer;

public interface MultipleExecute {

	default void execute(Connection connection, List<String> queries, Map<String, String> parameters) throws SQLException {
		execute(connection, queries, parameters, new StatementCallback() {});
	}
	
	default void execute(
		Connection connection, List<String> queries, Map<String, String> parameters,
		StatementCallback callback
	) throws SQLException {
		try (Statement stat = connection.createStatement();) {
			for (String query : queries) {
				if (stat instanceof StatementWrapper) {
					StatementWrapper w = StatementWrapper.class.cast(stat);
					w.getProfiler().builderQuery(w.ID, query, query, parameters);
				}
				ThrowingConsumer<Statement, SQLException> after = callback.afterStatement(query);
				if (after != null) {
					stat.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
					after.accept(stat);
				} else {
					stat.execute(query);
				}
			}
		}
	}
	
	interface StatementCallback {
		
		default ThrowingConsumer<Statement, SQLException> afterStatement(String query) {
			return null;
		}
		
	}
	
}
