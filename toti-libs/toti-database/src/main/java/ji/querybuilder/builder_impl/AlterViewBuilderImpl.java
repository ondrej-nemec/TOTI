package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;

import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.ParametrizedSql;
import ji.querybuilder.builder_impl.share.SelectImpl;
import ji.querybuilder.builder_impl.share.SingleExecute;
import ji.querybuilder.builders.AlterViewBuilder;

public class AlterViewBuilderImpl extends SelectImpl<AlterViewBuilderImpl> implements AlterViewBuilder, SingleExecute, ParametrizedSql {

	// TODO with
	private final Connection connection;
	private final DbInstance instance;

	private final String view;
	
	public AlterViewBuilderImpl(Connection connection, DbInstance instance, String view) {
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
	protected AlterViewBuilderImpl getThis() {
		return this;
	}

	@Override
	public int execute() throws SQLException {
		return execute(connection, createSql(), getParameters());
	}

}
