package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ji.common.structures.DictionaryValue;
import ji.common.structures.ObjectBuilder;
import ji.common.structures.ThrowingConsumer;
import ji.common.structures.Tuple2;
import ji.querybuilder.DbInstance;
import ji.querybuilder.Escape;
import ji.querybuilder.builder_impl.share.MultipleExecute;
import ji.querybuilder.builders.InsertBuilder;
import ji.querybuilder.structures.SubSelect;

public class InsertBuilderImpl implements InsertBuilder, MultipleExecute {

	private final Connection connection;
	private final DbInstance instance;
	private final String table;
	private final String alias;
	private final Optional<String> idName;
	private final List<Tuple2<String, String>> values;
	private SubSelect select;
	private List<String> columns;
	
	private final List<Tuple2<String, SubSelect>> withs;
	

	public InsertBuilderImpl(Connection connection, DbInstance instance, String table, String alias, Optional<String> idName) {
		this(connection, instance, table, alias, idName, new LinkedList<>());
	}
	
	public InsertBuilderImpl(
			Connection connection, DbInstance instance, String table, String alias, Optional<String> idName,
			List<Tuple2<String, SubSelect>> withs) {
		this.connection = connection;
		this.instance = instance;
		this.table = table;
		this.alias = alias;
		this.idName = idName;
		this.values = new LinkedList<>();
		this.withs = withs;
	}
	
	public List<Tuple2<String, SubSelect>> getWiths() {
		return withs;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public String getTable() {
		return table;
	}
	
	public List<Tuple2<String, String>> getValues() {
		return values;
	}
	
	public SubSelect getSelect() {
		return select;
	}
	
	public List<String> getColumns() {
		return columns;
	}

	public Optional<String> getIdName() {
		return idName;
	}

	@Override
	public String getSql() {
		return _toString(getSqls());
	}
	
	@Override
	public String createSql() {
		return _toString(createSqls());
	}
	
	@Override
	public List<String> getSqls() {
		return instance.createSql(this, false);
	}
	
	@Override
	public List<String> createSqls() {
		return instance.createSql(this, true);
	}

	@Override
	public InsertBuilder addValue(String columnName, Object value) {
		if (this.select != null) {
			throw new RuntimeException("Cannot use addValue if fromSelect is used");
		}
		this.values.add(new Tuple2<>(columnName, Escape.escape(value)));
		return this;
	}

	@Override
	public InsertBuilder fromSelect(List<String> columns, SubSelect select) {
		if (!this.values.isEmpty()) {
			throw new RuntimeException("Cannot use fromSelect if value is set");
		}
		this.columns = columns;
		this.select = select;
		return this;
	}

	@Override
	public DictionaryValue execute() throws SQLException {
		ObjectBuilder<DictionaryValue> id = new ObjectBuilder<>(null);
		execute(connection, createSqls(), new HashMap<>(), new StatementCallback() {
			@Override
			public ThrowingConsumer<Statement, SQLException> afterStatement(String query) {
				if (!query.startsWith("INSERT")) {
					return null;
				}
				return (stat)->{
					if (idName.isPresent()) {
						for (Tuple2<String, String> tuple : values) {
							if (tuple._1().equals(idName.get())) {
								id.set(new DictionaryValue(tuple._2()));
								return;
							}
						}
						id.set(new DictionaryValue(null));
					} else {
						try(ResultSet rs = stat.getGeneratedKeys();){
							if (rs.next()) {
								id.set(new DictionaryValue(rs.getObject(1)));
							} else {
								id.set(new DictionaryValue(-1)); // if no key generated - can be valid state
							}
						}
					}
				};
			}
		});
		return id.get();
	}

}
