package toti.lib.database.querybuilder.builders;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.Functions;
import toti.lib.database.querybuilder.builder_impl.DeleteBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.InsertBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.SelectBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.UpdateBuilderImpl;
import toti.lib.database.querybuilder.structures.SubSelect;
import toti.lib.common.structures.Tuple2;

public class WithBuilder {

	private final DbInstance instance;
	private final Connection connection;
	
	private final List<Tuple2<String, SubSelect>> withs;

	public WithBuilder(DbInstance instance, Connection connection, String alias, SubSelect subSelect) {
		this.instance = instance;
		this.connection = connection;
		this.withs = new LinkedList<>();
		_with(alias, subSelect);
	}
	
	public WithBuilder with(String alias, SelectBuilder builder) {
		return _with(alias, builder);
	}
	
	public WithBuilder with(String alias, MultipleSelectBuilder builder) {
		return _with(alias, builder);
	}
	
	private WithBuilder _with(String alias, SubSelect select) {
		this.withs.add(new Tuple2<>(alias, select));
		return this;
	}
	
	public DeleteBuilder delete(String table) {
		return delete(table, null);
	}
	
	public DeleteBuilder delete(String table, String alias) {
		return new DeleteBuilderImpl(connection, instance, table, alias, withs);
	}

	public InsertBuilder insert(String table) {
		return insert(table, null, Optional.empty());
	}
	
	public InsertBuilder insert(String table, String alias) {
		return insert(table, alias, Optional.empty());
	}

	public InsertBuilder insert(String table, Optional<String> idName) {
		return insert(table, null, idName);
	}

	public InsertBuilder insert(String table, String alias, Optional<String> idName) {
		return new InsertBuilderImpl(connection, instance, table, alias, idName, withs);
	}

	public UpdateBuilder update(String table) {
		return update(table, null);
	}

	public UpdateBuilder update(String table, String alias) {
		return new UpdateBuilderImpl(connection, instance, table, alias, withs);
	}

	public SelectBuilder select(Function<Functions, String> select) {
		return select(select.apply(instance));
	}

	public SelectBuilder select(String... select) {
		return new SelectBuilderImpl(connection, instance, select, withs);
	}
}
