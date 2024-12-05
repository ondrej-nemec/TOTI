package ji.querybuilder.instances;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ji.common.functions.Implode;
import ji.common.structures.ObjectBuilder;
import ji.common.structures.Tuple2;
import ji.querybuilder.DbInstance;
import ji.querybuilder.builder_impl.AlterTableBuilderImpl;
import ji.querybuilder.builder_impl.AlterViewBuilderImpl;
import ji.querybuilder.builder_impl.CreateIndexBuilderImpl;
import ji.querybuilder.builder_impl.CreateTableBuilderImpl;
import ji.querybuilder.builder_impl.CreateViewBuilderImpl;
import ji.querybuilder.builder_impl.DeleteBuilderImpl;
import ji.querybuilder.builder_impl.DeleteIndexBuilderImpl;
import ji.querybuilder.builder_impl.DeleteTableBuilderImpl;
import ji.querybuilder.builder_impl.DeleteViewBuilderImpl;
import ji.querybuilder.builder_impl.InsertBuilderImpl;
import ji.querybuilder.builder_impl.MultipleSelectBuilderImpl;
import ji.querybuilder.builder_impl.SelectBuilderImpl;
import ji.querybuilder.builder_impl.UpdateBuilderImpl;
import ji.querybuilder.builder_impl.share.SelectImpl;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.Join;
import ji.querybuilder.enums.OnAction;
import ji.querybuilder.enums.SelectJoin;
import ji.querybuilder.enums.Where;
import ji.querybuilder.structures.Column;
import ji.querybuilder.structures.ForeignKey;
import ji.querybuilder.structures.Joining;
import ji.querybuilder.structures.SubSelect;

public class SqLiteQueryBuilder implements DbInstance {

	@Override
	public String concat(String param, String... params) {
		StringBuilder builder = new StringBuilder("CONCAT(");
		builder.append(param);
		for (String p : params) {
			builder.append(", ");
			builder.append(p);
		}
		builder.append(")");
		return builder.toString();
	}
	
	@Override
	public String trim(String param) {
		return String.format("TRIM(%s)", param);
	}

	@Override
	public String cast(String param, ColumnType type) {
		return String.format("CAST(%s AS %s)", param, toString(type));
	}

	@Override
	public String groupConcat(String param, String delimeter) {
		return String.format("STRING_AGG(%s, '%s')", param, delimeter);
	}
	
	@Override
	public String max(String param) {
		return String.format("MAX(%s)", param);
	}
	
	@Override
	public String min(String param) {
		return String.format("MIN(%s)", param);
	}
	
	@Override
	public String avg(String param) {
		return String.format("AVG(%s)", param);
	}
	
	@Override
	public String sum(String param) {
		return String.format("SUM(%s)", param);
	}
	
	@Override
	public String count(String param) {
		return String.format("COUNT(%s)", param);
	}
	
	@Override
	public String lower(String param) {
		return String.format("LOWER(%s)", param);
	}
	
	@Override
	public String upper(String param) {
		return String.format("UPPER(%s)", param);
	}
	
	/*************/

	@Override
	public String createSql(DeleteIndexBuilderImpl deleteIndex) {
		return "DROP INDEX " + deleteIndex.getIndexName();
	}

	@Override
	public String createSql(CreateIndexBuilderImpl createIndex) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE INDEX ");
		sql.append(createIndex.getIndexName());
		sql.append(" ON ");
		sql.append(createIndex.getTable());
		sql.append("(");
		String[] columns = createIndex.getColumns();
		for (int i = 0; i < columns.length; i++) {
			if (i > 0) {
				sql.append(", ");
			}
			sql.append(columns[i]);
		}
		sql.append(")");
		return sql.toString();
	}

	@Override
	public String createSql(DeleteTableBuilderImpl deleteTable) {
		return "DROP TABLE " + deleteTable.getTable();
	}

	@Override
	public String createSql(DeleteViewBuilderImpl deleteView) {
		return "DROP VIEW " + deleteView.getView();
	}

	@Override
	public String createSql(InsertBuilderImpl insert, boolean create) {
		StringBuilder sql = new StringBuilder();
		createWith(insert.getWiths(), sql, create);
		sql.append("INSERT INTO " + getWithAlias(insert.getTable(), insert.getAlias()) + " ");
		if (insert.getValues().isEmpty()) {
			// insert from select
			sql.append("(");
			sql.append(Implode.implode(", ", insert.getColumns()));
			sql.append(") ");
			sql.append(create ? insert.getSelect().createSql() : insert.getSelect().getSql());
		} else {
			StringBuilder columns = new StringBuilder();
			StringBuilder values = new StringBuilder();
			insert.getValues().forEach((val)->{
				if (!columns.toString().isEmpty()) {
					columns.append(", ");
					values.append(", ");
				} else {
					columns.append("(");
					values.append("(");
				}
				columns.append(val._1());
				values.append(val._2());
			});
			columns.append(")");
			values.append(")");
			
			sql.append(columns);
			sql.append(" VALUES ");
			sql.append(values);
		}
		return sql.toString();
	}

	@Override
	public String createSql(UpdateBuilderImpl updateBuilder, boolean create) {
		StringBuilder sql = new StringBuilder();
		createWith(updateBuilder.getWiths(), sql, create);
		sql.append("UPDATE ");
		sql.append(getWithAlias(updateBuilder.getTable(), updateBuilder.getAlias()));
		ObjectBuilder<Boolean> firstSet = new ObjectBuilder<>(true);
		updateBuilder.getSets().forEach(set->{
			if (firstSet.get()) {
				firstSet.set(false);
				sql.append(" SET ");
			} else {
				sql.append(", ");
			}
			sql.append(set);
		});
		LinkedList<Tuple2<String, Where>> wheres = new LinkedList<>(updateBuilder.getWheres());
		ObjectBuilder<Boolean> firstJoin = new ObjectBuilder<>(true);
		updateBuilder.getJoins().forEach(join->{
			if (firstJoin.get()) {
				firstJoin.set(false);
				sql.append(" FROM ");
				sql.append(getWithAlias(
					create ? join.getBuilder().createSql() : join.getBuilder().getSql(),
					join.getAlias()
				));
				wheres.addFirst(new Tuple2<>(join.getOn(), null));
			} else {
				createJoin(join, sql, create);
			}
		});
		createWhere(wheres, sql, create);
		return sql.toString();
	}

	@Override
	public String createSql(DeleteBuilderImpl delete, boolean create) {
		StringBuilder sql = new StringBuilder();
		createWith(delete.getWiths(), sql, create);
		sql.append("DELETE FROM " + delete.getTable());
		if (!delete.getJoins().isEmpty()) {
			sql.append(" WHERE ROWID IN (");
			sql.append("SELECT " + (delete.getAlias() == null ? delete.getTable() : delete.getAlias()) + ".ROWID");
			sql.append(" FROM " + getWithAlias(delete.getTable(), delete.getAlias()));
			delete.getJoins().forEach(join->{
				createJoin(join, sql, create);
			});
			createWhere(delete.getWheres(), sql, create);
			sql.append(")");
		} else {
			createWhere(delete.getWheres(), sql, create);
		}
		return sql.toString();
	}

	@Override
	public String createSql(SelectBuilderImpl select, boolean create) {
		StringBuilder sql = new StringBuilder();
		createWith(select.getWiths(), sql, create);
		createPlainSelect(select, sql, create);
		return sql.toString();
	}

	@Override
	public String createSql(MultipleSelectBuilderImpl multipleSelect, boolean create) {
		StringBuilder sql = new StringBuilder();
		iterateList(
			sql, multipleSelect.getSelects(),
			i->"", i->" " + toString(i._2()) + " ",  i->create ? i._1().createSql() : i._1().getSql()
		);
		iterateList(
			sql, multipleSelect.getOrderBy(),
			i->" ORDER BY ", i->", ", i->i
		);
		return sql.toString();
	}

	@Override
	public String createSql(CreateViewBuilderImpl createView, boolean create) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE VIEW " + createView.getView() + " AS ");
		createPlainSelect(createView, sql, create);
		return sql.toString();
	}

	@Override
	public String createSql(AlterViewBuilderImpl alterView, boolean create) {
		StringBuilder sql = new StringBuilder();
		sql.append("DROP VIEW " + alterView.getView() + "; ");
		sql.append("CREATE VIEW " + alterView.getView() + " AS ");
		createPlainSelect(alterView, sql, create);
		return sql.toString();
	}

	@Override
	public String createSql(CreateTableBuilderImpl createTable) {
		StringBuilder sql = new StringBuilder();
		
		StringBuilder appendix = new StringBuilder();
		
		sql.append("CREATE TABLE ");
		sql.append(createTable.getTable());
		sql.append(" (");
		
		iterateList(
			sql, createTable.getColumns(),
			i->"", i->", ", c->getColumn(c, x->appendix.append(", " + x))
		);
		
		sql.append(appendix.toString());
		if (createTable.getPrimaryKey() != null) {
			sql.append(String.format(", PRIMARY KEY (%s)", Implode.implode(", ", createTable.getPrimaryKey())));
		}
		createAddForeignKey(createTable.getForeignKeys(), s->sql.append(s), ", ");
		
		sql.append(")");
		return sql.toString();
	}

	@Override
	public String createSql(AlterTableBuilderImpl alterTable) {		
		StringBuilder result = new StringBuilder();
		Supplier<String> alterTablePrefix = ()->"ALTER TABLE " + alterTable.getTable() + " ";
		
		iterateList(
			sql->result.append(sql + ";"), alterTable.getAddColumns(),
			i->alterTablePrefix.get(), i->alterTablePrefix.get(),
			c->"ADD COLUMN " + getColumn(c, null)
		);
		
		iterateList(
			sql->result.append(sql + ";"), alterTable.getDeleteColumns(),
			i->alterTablePrefix.get(), i->alterTablePrefix.get(),
			c->"DROP COLUMN " + c.getName()
		);
		iterateList(
			sql->result.append(sql + ";"), alterTable.getRenameColumns(),
			i->alterTablePrefix.get(), i->alterTablePrefix.get(),
			c->String.format("RENAME COLUMN %s TO %s", c.getOldName(), c.getNewName())
		);
		if (alterTable.getNewName() != null) {
			result.append(alterTablePrefix.get() + "RENAME TO " + alterTable.getNewName() + ";");
		}
		//	createAddForeignKey(alterTable.getAddForeignKeys(), sql->rows.add(sql), "ADD ");
		/*
		
		iterateList(
			sql->result.append(sql + ";"), alterTable.getDeleteForeignKeys(),
			i->alterTablePrefix.get(), i->alterTablePrefix.get(),
			fk->"DROP CONSTRAINT " + fk.getColumn()
		);
		iterateList(
			sql->result.append(sql + ";"), alterTable.getModifyColumnsType(),
			i->alterTablePrefix.get(), i->alterTablePrefix.get(),c->{
				return "MODIFY COLUMN " + c.getName() + " " + toString(c.getType());
			}
		);
		iterateList(
			sql->result.append(sql + ";"), alterTable.getModifyUnique(),
			i->alterTablePrefix.get(), i->alterTablePrefix.get(), c->{
				String key = (alterTable.getTable() + "_" + c.getName() + "_key").toLowerCase();
				if (new DictionaryValue(c.getValue().getValue()).getBoolean()) {
					return "ADD CONSTRAINT " + key + " UNIQUE (" + c.getName() + ")";
				} else {
					return "DROP CONSTRAINT " + key; //  + " UNIQUE (" + c.getName() + ")"
				}
			}
		);
		iterateList(
			sql->result.append(sql + ";"), alterTable.getModifyDefault(),
			i->alterTablePrefix.get(), i->alterTablePrefix.get(), c->{
				if (c.getValue().isClear()) {
					return "ALTER COLUMN " + c.getName() + " DROP DEFAULT";
				} else {
					return "ALTER COLUMN " + c.getName() + " SET DEFAULT " + c.getValue().getValue();
				}
			}
		);
		iterateList(
			sql->result.append(sql + ";"), alterTable.getModifyNullable(),
			i->alterTablePrefix.get(), i->alterTablePrefix.get(), c->{
				if (new DictionaryValue(c.getValue().getValue()).getBoolean()) {
					return "ALTER COLUMN " + c.getName() + " SET NOT NULL";
				} else {
					return "ALTER COLUMN " + c.getName() + " DROP NOT NULL";
				}
			}
		);*/
		RuntimeException notSupported = new RuntimeException("Not supported operation");
		if (!alterTable.getAddForeignKeys().isEmpty()) {
			throw notSupported;
		}
		if (!alterTable.getDeleteForeignKeys().isEmpty()) {
			throw notSupported;
		}
		if (!alterTable.getModifyColumnsType().isEmpty()) {
			throw notSupported;
		}
		if (!alterTable.getModifyDefault().isEmpty()) {
			throw notSupported;
		}
		if (!alterTable.getModifyNullable().isEmpty()) {
			throw notSupported;
		}
		if (!alterTable.getModifyUnique().isEmpty()) {
			throw notSupported;
		}
		return result.toString();
	}
	
	/****************************/

	protected String toString(ColumnType type) {
		switch (type.getType()) {
			case STRING:
				return String.format("VARCHAR(%s)", type.getSize());
			case CHAR:
				return String.format("CHAR(%s)", type.getSize());
			case DATETIME: return "TIMESTAMP";
			default: return type.getType().toString();
		}
	}

	protected String toString(ColumnSetting settings) {
		switch (settings) {
			//case AUTO_INCREMENT: return "SERIAL";
			case UNIQUE: return "UNIQUE";
			case NOT_NULL: return "NOT NULL";
			case NULL: return "NULL";
			// never happends: case PRIMARY_KEY: return "";
			default: return "";
		}
	}

	protected String toString(OnAction action) {
		switch (action) {
			case RESTRICT: return "RESTRICT";
			case CASCADE: return "CASCADE";
			case SET_NULL: return "SET NULL";
			case NO_ACTION: return "NO ACTION";
			case SET_DEFAULT: return "SET DEFAULT";
			default: throw new RuntimeException("Not implemented action: " + action);
		}
	}

	protected String toString(Join join) {
		switch(join) {
			case FULL_OUTER_JOIN: throw new RuntimeException("Full Outer Join is not supported by mysql");
			case INNER_JOIN: return "JOIN";
			case LEFT_OUTER_JOIN: return "LEFT JOIN";
			case RIGHT_OUTER_JOIN: return "RIGHT JOIN";
			default: throw new RuntimeException("Not implemented join: " + join);
		}
	}

	protected String toString(SelectJoin join) {
		switch(join) {
			case UNION_ALL: return "UNION ALL";
			default: return join.toString();
		}
	}

	/****************************/
	
	private <T> void iterateList(
			Consumer<String> add, List<T> list,
			Function<T, String> onFirst, Function<T, String> onOthers, Function<T, String> otherwise) {
		ObjectBuilder<Boolean> first = new ObjectBuilder<>(true);
		list.forEach(item->{
			if (first.get()) {
				first.set(false);
				add.accept(onFirst.apply(item) + otherwise.apply(item));
			} else {
				add.accept(onOthers.apply(item) + otherwise.apply(item));
			}
		});
	}
	
	private <T> void iterateList(
			StringBuilder sql, List<T> list,
			Function<T, String> onFirst, Function<T, String> onOthers, Function<T, String> otherwise) {
		iterateList(s->sql.append(s), list, onFirst, onOthers, otherwise);
	}
	
	private String getWithAlias(String table, String alias) {
		StringBuilder result = new StringBuilder();
		result.append(table);
		if (alias != null) {
			result.append(" AS " + alias);
		}
		return result.toString();
	}
	
	private String getColumn(Column column, Consumer<String> onConstaint) {
		StringBuilder result = new StringBuilder();
		result.append(column.getName());
		result.append(" ");
		result.append(toString(column.getType()));
		if (column.getValue().isSet()) {
			result.append(" DEFAULT ");
			result.append(column.getValue().getValue());
		} else if (column.getValue().isClear()) {
			// remove default
			// alter not suported
		}
		
		for (ColumnSetting settings : column.getSettings()) {
			if (settings == ColumnSetting.AUTO_INCREMENT) {
				// ignore
			} else if (settings == ColumnSetting.PRIMARY_KEY && onConstaint != null) {
				onConstaint.accept(String.format("PRIMARY KEY (%s)", column.getName()));
			} else {
				result.append(" ");
				result.append(toString(settings));
			}
			
		}
		return result.toString();
	}
	
	private void createAddForeignKey(List<ForeignKey> keys, Consumer<String> add, String prefix) {
		iterateList(
			add, keys,
			i->"", i->"", i->"" + String.format(
				prefix + "CONSTRAINT FK_%s FOREIGN KEY (%s) REFERENCES %s(%s)%s%s",
				i.getColumn(), i.getColumn(),
				i.getReferedTable(), i.getReferedColumn(),
				i.getOnDelete() == null ? "" : " ON DELETE " + toString(i.getOnDelete()),
				i.getOnUpdate() == null ? "" : " ON UPDATE " + toString(i.getOnUpdate())
			)
		);
	}
	
	private void createPlainSelect(SelectImpl<?> builder, StringBuilder sql, boolean create) {
		iterateList(
			sql, builder.getSelects(),
			i->"SELECT ", i->", ", i->i
		);
		if (builder.getFrom() != null) {
			sql.append(" FROM ");
			sql.append(getWithAlias(
				String.format(
					builder.getFrom()._1().wrap() ? "(%s)" : "%s",
					create ? builder.getFrom()._1().createSql() : builder.getFrom()._1().getSql()
				),
				builder.getFrom()._2()
			));
		}
		builder.getJoins().forEach(join->{
			createJoin(join, sql, create);
		});
		createWhere(builder.getWheres(), sql, create);
		iterateList(
			sql, builder.getGroupBy(),
			i->" GROUP BY ", i->", ", i->i
		);
		iterateList(
			sql, builder.getHaving(),
			i->" HAVING ", i->" AND ", i->i
		);
		iterateList(
			sql, builder.getOrderBy(),
			i->" ORDER BY ", i->", ", i->i
		);
		if (builder.getLimit() != null) {
			sql.append(" LIMIT " + builder.getLimit());
		}
		if (builder.getOffset() != null) {
			sql.append(" OFFSET " + builder.getOffset());
		}
	}
	
	private void createWith(List<Tuple2<String, SubSelect>> withs, StringBuilder sql, boolean create) {
		/*withs.forEach((with)->{
			String subquery = create ? with._2().createSql() : with._2().getSql();
			boolean isRecursive = subquery.contains(" " + with._1() + " ") || subquery.endsWith(" " + with._1());
			sql.append(String.format(
				"WITH" + (isRecursive ? " recursive" : "") + " %s AS (%s)",
				with._1(), subquery
			));
		});*/
		iterateList(
			sql,
			withs,
			(item)->"WITH",
			(with)->",",
			(with)->{
				String subquery = create ? with._2().createSql() : with._2().getSql();
				boolean isRecursive = subquery.contains(" " + with._1() + " ") || subquery.endsWith(" " + with._1());
				return String.format(
					(isRecursive ? " recursive" : "") + " %s AS (%s)", with._1(), subquery
				);
			}
		);
	}
	
	private void createWhere(List<Tuple2<String, Where>> wheres, StringBuilder sql, boolean create) {
		iterateList(
			sql, wheres,
			w->" WHERE ", w->" " + w._2().toString() + " ", w->"(" + w._1() + ")"
		);
	}
	
	private void createJoin(Joining join, StringBuilder sql, boolean create) {
		sql.append(" ");
		sql.append(toString(join.getJoin()));
		sql.append(" ");
		sql.append(getWithAlias(
			String.format(
				join.getBuilder().wrap() ? "(%s)" : "%s",
				create ? join.getBuilder().createSql() : join.getBuilder().getSql()
			),
			join.getAlias()
		));
		sql.append(" ON " + join.getOn());
	}
	
}
