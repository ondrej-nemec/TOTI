package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ThrowingFunction;
import ji.common.structures.Tuple2;
import ji.database.support.DatabaseRow;
import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.share.ParametrizedSql;
import ji.querybuilder.builder_impl.share.SelectExecute;
import ji.querybuilder.builder_impl.share.SelectImpl;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.structures.SubSelect;

public class SelectBuilderImpl extends SelectImpl<SelectBuilderImpl> implements SelectBuilder, ParametrizedSql, SelectExecute {

	private final Connection connection;
	private final DbInstance instance;
	
	private final List<Tuple2<String, SubSelect>> withs;
	
	public SelectBuilderImpl(Connection connection, DbInstance instance, String[] select) {
		this(connection, instance, select, new LinkedList<>());
	}
	
	public SelectBuilderImpl(
			Connection connection, DbInstance instance, String[] select,
			List<Tuple2<String, SubSelect>> withs) {
		super(instance);
		this.connection = connection;
		this.instance = instance;
		this.withs = withs;
		for (String s : select) {
			select(s);
		}
	}
	
	public List<Tuple2<String, SubSelect>> getWiths() {
		return withs;
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
	protected SelectBuilderImpl getThis() {
		return this;
	}

	@Override
	public DictionaryValue fetchSingle() throws SQLException {
		return fetchSingle(connection, createSql(), getParameters());
	}

	@Override
	public DatabaseRow fetchRow() throws SQLException {
		return fetchRow(connection, createSql(), getParameters());
	}

	@Override
	public <T> List<T> fetchAll(ThrowingFunction<DatabaseRow, T, SQLException> function) throws SQLException {
		return fetchAll(connection, createSql(), getParameters(), function);
	}

	@Override
	public <K, V> Map<K, V> fetchAll(ThrowingFunction<DatabaseRow, K, SQLException> key,
		ThrowingFunction<DatabaseRow, V, SQLException> value) throws SQLException {
		return fetchAll(connection, createSql(), getParameters(), key, value);
	}

}
