package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ThrowingFunction;
import ji.common.structures.Tuple2;
import ji.database.support.DatabaseRow;
import ji.querybuilder.DbInstance;
import ji.querybuilder.Escape;
import ji.querybuilder.Functions;
import ji.querybuilder.builder_impl.share.ParametrizedSql;
import ji.querybuilder.builder_impl.share.SelectExecute;
import ji.querybuilder.builders.MultipleSelectBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.enums.SelectJoin;

public class MultipleSelectBuilderImpl implements MultipleSelectBuilder, ParametrizedSql, SelectExecute {

	private final Connection connection;
	private final DbInstance instance;

	private final Map<String, String> parameters;
	
	private final List<String> orderBy;
	private final List<Tuple2<SelectBuilder, SelectJoin>> selects;
	
	public MultipleSelectBuilderImpl(Connection connection, DbInstance instance, SelectBuilder builder) {
		this.connection = connection;
		this.instance = instance;
		this.parameters = new HashMap<>();
		this.selects = new LinkedList<>();
		this.orderBy = new LinkedList<>();
		
		selects.add(new Tuple2<>(builder, null));
	}
	
	public List<Tuple2<SelectBuilder, SelectJoin>> getSelects() {
		return selects;
	}
	
	public List<String> getOrderBy() {
		return orderBy;
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
	public MultipleSelectBuilder union(SelectBuilder select) {
		this.selects.add(new Tuple2<>(select, SelectJoin.UNION));
		return this;
	}

	@Override
	public MultipleSelectBuilder intersect(SelectBuilder select) {
		this.selects.add(new Tuple2<>(select, SelectJoin.INTERSECT));
		return this;
	}

	@Override
	public MultipleSelectBuilder unionAll(SelectBuilder select) {
		this.selects.add(new Tuple2<>(select, SelectJoin.UNION_ALL));
		return this;
	}

	@Override
	public MultipleSelectBuilder except(SelectBuilder select) {
		this.selects.add(new Tuple2<>(select, SelectJoin.EXCEPT));
		return this;
	}

	@Override
	public MultipleSelectBuilder addParameter(String name, Object value) {
		parameters.put(name, Escape.escape(value));
		return this;
	}

	@Override
	public MultipleSelectBuilder orderBy(Function<Functions, String> orderBy) {
		this.orderBy.add(orderBy.apply(instance));
		return this;
	}

	@Override
	public DictionaryValue fetchSingle() throws SQLException {
		return fetchSingle(connection, createSql(), parameters);
	}

	@Override
	public DatabaseRow fetchRow() throws SQLException {
		return fetchRow(connection, createSql(), parameters);
	}

	@Override
	public <T> List<T> fetchAll(ThrowingFunction<DatabaseRow, T, SQLException> function) throws SQLException {
		return fetchAll(connection, createSql(), parameters, function);
	}

	@Override
	public <K, V> Map<K, V> fetchAll(ThrowingFunction<DatabaseRow, K, SQLException> key,
		ThrowingFunction<DatabaseRow, V, SQLException> value) throws SQLException {
		return fetchAll(connection, createSql(), parameters, key, value);
	}

}
