package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.SingleExecute;
import ji.querybuilder.builders.DeleteIndexBuilder;

public class DeleteIndexBuilderImpl implements DeleteIndexBuilder, SingleExecute {
	
	private final Connection connection;
	private final DbInstance instance;
	private final String name;

	public DeleteIndexBuilderImpl(Connection connection, DbInstance instance, String name) {
		this.connection = connection;
		this.instance = instance;
		this.name = name;
	}
	
	public String getIndexName() {
		return name;
	}

	@Override
	public String getSql() {
		return instance.createSql(this);
	}

	@Override
	public int execute() throws SQLException {
		return execute(connection, createSql(), new HashMap<>());
	}

}
