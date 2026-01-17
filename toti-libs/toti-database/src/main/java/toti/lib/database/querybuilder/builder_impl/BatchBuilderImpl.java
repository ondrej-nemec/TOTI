package toti.lib.database.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import toti.lib.database.base.wrappers.StatementWrapper;
import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.builder_impl.share.ParametrizedSql;
import toti.lib.database.querybuilder.builders.BatchBuilder;

public class BatchBuilderImpl implements BatchBuilder, ParametrizedSql {
	
	private final List<Builder> batches;
	private final Connection connection;
	private final DbInstance instance;
	
	private final Map<String, String> parameters;

	public BatchBuilderImpl(Connection connection, DbInstance instance) {
		this.batches = new LinkedList<>();
		this.parameters = new HashMap<>();
		this.connection = connection;
		this.instance = instance;
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
		parameters.put(name, instance.getEscape().escape(value));
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
				for (String sql : b.createSqls()) {
					String query = parse(sql, parameters);
					if (stat instanceof StatementWrapper) {
						StatementWrapper w = StatementWrapper.class.cast(stat);
						w.getProfiler().builderQuery(w.ID, b.getSql(), sql, parameters);
					}
					stat.addBatch(query);
				}
			}
			stat.executeBatch();
		}
	}

}
