package ji.querybuilder.builder_impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import ji.common.structures.DictionaryValue;
import ji.common.structures.Tuple2;
import ji.querybuilder.DbInstance;
import ji.querybuilder.Escape;
import ji.querybuilder.builders.InsertBuilder;
import ji.querybuilder.structures.SubSelect;

public class InsertBuilderImpl implements InsertBuilder {

	// TODO with
	private final Connection connection;
	private final DbInstance instance;
	private final String table;
	private final String alias;
	private final List<Tuple2<String, String>> values;
	private SubSelect select;
	private List<String> columns;
	
	private final List<Tuple2<String, SubSelect>> withs;
	

	public InsertBuilderImpl(Connection connection, DbInstance instance, String table, String alias) {
		this(connection, instance, table, alias, new LinkedList<>());
	}
	
	public InsertBuilderImpl(
			Connection connection, DbInstance instance, String table, String alias,
			List<Tuple2<String, SubSelect>> withs) {
		this.connection = connection;
		this.instance = instance;
		this.table = table;
		this.alias = alias;
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

	@Override
	public String getSql() {
		return instance.createSql(this, false);
	}
	
	@Override
	public String createSql() {
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
		String sql = createSql();
		try (PreparedStatement stat = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stat.executeUpdate();
			try(ResultSet rs = stat.getGeneratedKeys();){
				if (rs.next()) {
					return new DictionaryValue(rs.getObject(1));
				}
				return new DictionaryValue(-1); // if no key generated - can be valid state
			}
		}
	}

}
