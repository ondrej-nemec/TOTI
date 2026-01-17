package toti.lib.database.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;

import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.builder_impl.share.ParametrizedSql;
import toti.lib.database.querybuilder.builder_impl.share.SelectImpl;
import toti.lib.database.querybuilder.builder_impl.share.SingleExecute;
import toti.lib.database.querybuilder.builders.CreateViewBuilder;

public class CreateViewBuilderImpl extends SelectImpl<CreateViewBuilderImpl> implements CreateViewBuilder, SingleExecute, ParametrizedSql {

	// TODO with
	private final Connection connection;
	private final DbInstance instance;

	private final String view;
	
	public CreateViewBuilderImpl(Connection connection, DbInstance instance, String view) {
		super(instance);
		this.connection = connection;
		this.instance = instance;
		this.view = view;
	}
	
	public String getView() {
		return view;
	}

	@Override
	public String getSql() {
		return instance.createSql(this, false);
	}

	@Override
	public String createSql() {
		return parse(instance.createSql(this, true), getParameters());
	}

	@Override
	protected CreateViewBuilderImpl getThis() {
		return this;
	}

	@Override
	public int execute() throws SQLException {
		return execute(connection, createSql(), getParameters());
	}

}
