package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.SingleExecute;
import ji.querybuilder.builders.DeleteViewBuilder;

public class DeleteViewBuilderImpl implements DeleteViewBuilder, SingleExecute {

	private final Connection connection;
	private final DbInstance instance;
	private final String view;
	
	public DeleteViewBuilderImpl(Connection connection, DbInstance instance, String view) {
		this.connection = connection;
		this.instance = instance;
		this.view = view;
	}

	public String getView() {
		return view;
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
