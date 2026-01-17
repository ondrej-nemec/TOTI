package toti.lib.database.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.Functions;
import toti.lib.database.querybuilder.builder_impl.share.ParametrizedSql;
import toti.lib.database.querybuilder.builder_impl.share.SingleExecute;
import toti.lib.database.querybuilder.builders.DeleteBuilder;
import toti.lib.database.querybuilder.enums.Join;
import toti.lib.database.querybuilder.enums.Where;
import toti.lib.database.querybuilder.structures.Joining;
import toti.lib.database.querybuilder.structures.SubSelect;
import toti.lib.common.structures.Tuple2;

public class DeleteBuilderImpl implements DeleteBuilder, SingleExecute, ParametrizedSql {

	// TODO with
	private final Connection connection;
	private final DbInstance instance;
	private final String table;
	private final String alias;
	private final List<Tuple2<String, Where>> wheres;
	private final List<Joining> joins;
	private final Map<String, String> parameters;
	
	private final List<Tuple2<String, SubSelect>> withs;

	public DeleteBuilderImpl(Connection connection, DbInstance instance, String table, String alias) {
		this(connection, instance, table, alias, new LinkedList<>());
	}
	
	public DeleteBuilderImpl(
			Connection connection, DbInstance instance, String table, String alias,
			List<Tuple2<String, SubSelect>> withs) {
		this.connection = connection;
		this.instance = instance;
		this.table = table;
		this.alias = alias;
		this.parameters = new HashMap<>();
		this.wheres = new LinkedList<>();
		this.joins = new LinkedList<>();
		this.withs = withs;
	}
	
	public List<Tuple2<String, SubSelect>> getWiths() {
		return withs;
	}
	
	public String getTable() {
		return table;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public List<Tuple2<String, Where>> getWheres() {
		return wheres;
	}
	
	public List<Joining> getJoins() {
		return joins;
	}

	@Override
	public String getSql() {
		return instance.createSql(this, false);
	}

	@Override
	public String createSql() {
		return parse(instance.createSql(this, true), parameters);
	}

	@Override
	public DeleteBuilder where(Function<Functions, String> where, Where join) {
		this.wheres.add(new Tuple2<>(where.apply(instance), join));
		return this;
	}

	@Override
	public DeleteBuilder addParameter(String name, Object value) {
		parameters.put(name, instance.getEscape().escape(value));
		return this;
	}

	@Override
	public DeleteBuilder _join(SubSelect builder, String alias, Join join, Function<Functions, String> on) {
		this.joins.add(new Joining(builder, alias, join, on.apply(instance)));
		return this;
	}

	@Override
	public int execute() throws SQLException {
		return execute(connection, createSql(), parameters);
	}

}
