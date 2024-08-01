package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ji.database.wrappers.StatementWrapper;
import ji.querybuilder.Builder;
import ji.querybuilder.DbInstance;
import ji.querybuilder.Escape;
import ji.querybuilder.builder_impl.share.ParametrizedSql;
import ji.querybuilder.builders.BatchBuilder;

public class BatchBuilderImpl implements BatchBuilder, ParametrizedSql {
	
	private final List<Builder> batches;
	private final Connection connection;
	
	private final Map<String, String> parameters;

	public BatchBuilderImpl(Connection connection, DbInstance instance) {
		this.batches = new LinkedList<>();
		this.parameters = new HashMap<>();
		this.connection = connection;
	}

	@Override
	public String getSql() {
		return prepate(b->b.getSql());
	}

	@Override
	public String createSql() {
		return parse(prepate(b->b.createSql()), parameters);
	}
	
	private String prepate(Function<Builder, String> createSql) {
		StringBuilder query = new StringBuilder();
		batches.forEach((batch)->{
			if (!query.isEmpty()) {
				query.append(" ");
			}
			query.append(createSql.apply(batch));
			query.append(";");
		});
		return query.toString();
	}

	@Override
	public BatchBuilder addParameter(String name, Object value) {
		parameters.put(name, Escape.escape(value));
		return this;
	}

	@Override
	public BatchBuilder addBatch(Builder batch) {
		batches.add(batch);
		return this;
	}

	@Override
	public void execute() throws SQLException {
		try (Statement stat = connection.createStatement();) {
			for (Builder b : batches) {
				String query = parse(b.createSql(), parameters);
				if (stat instanceof StatementWrapper) {
					StatementWrapper w = StatementWrapper.class.cast(stat);
					w.getProfiler().builderQuery(w.ID, b.getSql(), query, parameters);
				}
				stat.addBatch(query);
			}
			stat.executeBatch();
		}
	}

}
