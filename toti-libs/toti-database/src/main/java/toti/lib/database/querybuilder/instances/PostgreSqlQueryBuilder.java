package toti.lib.database.querybuilder.instances;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import toti.lib.common.functions.Implode;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.common.structures.Tuple2;
import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.builder_impl.AlterTableBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.AlterViewBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.CallProcedureBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.CreateIndexBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.CreateTableBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.CreateViewBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.DeleteBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.DeleteIndexBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.DeleteTableBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.DeleteViewBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.InsertBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.MultipleSelectBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.SelectBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.UpdateBuilderImpl;
import toti.lib.database.querybuilder.builder_impl.share.SelectImpl;
import toti.lib.database.querybuilder.enums.ColumnSetting;
import toti.lib.database.querybuilder.enums.ColumnType;
import toti.lib.database.querybuilder.enums.Join;
import toti.lib.database.querybuilder.enums.OnAction;
import toti.lib.database.querybuilder.enums.SelectJoin;
import toti.lib.database.querybuilder.enums.Where;
import toti.lib.database.querybuilder.structures.Column;
import toti.lib.database.querybuilder.structures.DefaultValue;
import toti.lib.database.querybuilder.structures.ForeignKey;
import toti.lib.database.querybuilder.structures.Joining;
import toti.lib.database.querybuilder.structures.SubSelect;
public class PostgreSqlQueryBuilder implements DbInstance {

	@Override
	public String concat(String param1, String param2, String... params) {
		StringBuilder builder = new StringBuilder("CONCAT(");
		builder.append(String.format("%s, %s", param1, param2));
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
	public String groupConcat(String param, String delimeter, String orderBy) {
		return String.format(
			"STRING_AGG(%s, '%s'%s)",
			param, delimeter, orderBy == null ? "" : " ORDER BY " + orderBy
		);
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
	public String createSql(CallProcedureBuilderImpl callProcedure, boolean create) {
		if (callProcedure.isOutput()) {
			throw new RuntimeException("Not supported operation");
		}
		return "CALL "
			 + callProcedure.getProcedure() + "("
			 + Implode.implode(r->{
				if (r.startsWith("'")) {
					return r + "::varchar";
				}
				return r;
			}, ", ", callProcedure.getParameters())
			 + ")";
	}

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
		return "DROP VIEW IF EXISTS " + deleteView.getView();
	}

	@Override
	public List<String> createSql(InsertBuilderImpl insert, boolean create) {
		StringBuilder sql = new StringBuilder();
		createWith(insert.getWiths(), sql, create);
		sql.append(String.format("INSERT INTO %s ", getWithAlias(insert.getTable(), insert.getAlias())));
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
		List<String> result = new LinkedList<>();
		result.add(sql.toString());
		if (insert.getIdName().isPresent()) {
			String id = insert.getIdName().get();
			result.add(String.format(
				"SELECT setval('%s_%s_seq', COALESCE((SELECT MAX(%s)+1 FROM %s), 1), false)", 
				insert.getTable(), id, id, insert.getTable()
			));
		}
		return result;
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
		createWhere(wheres, sql);
		return sql.toString();
	}

	@Override
	public String createSql(DeleteBuilderImpl delete, boolean create) {
		StringBuilder sql = new StringBuilder();
		createWith(delete.getWiths(), sql, create);
		sql.append("DELETE FROM ");
		sql.append(getWithAlias(delete.getTable(), delete.getAlias()));
		
		StringBuilder joins = new StringBuilder();
		StringBuilder wheres = new StringBuilder();
		delete.getJoins().forEach(join->{
			if (joins.isEmpty()) {
				joins.append(" USING ");
				wheres.append(" WHERE");
			} else {
				joins.append(", ");
				wheres.append(" AND");
			}
			joins.append(getWithAlias(
				String.format(
					join.getBuilder().wrap() ? "(%s)" : "%s",
					create ? join.getBuilder().createSql() : join.getBuilder().getSql()
				),
				join.getAlias()
			));
			wheres.append(String.format("(%s)", join.getOn()));
		});
		delete.getWheres().forEach((where)->{
			if (wheres.isEmpty()) {
				wheres.append(" WHERE ");
			} else {
				wheres.append(String.format(" %s ", where._2().toString()));
			}
			wheres.append(String.format("(%s)", where._1()));
		});
		sql.append(joins.toString());
		sql.append(wheres.toString());
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
		sql.append(String.format("CREATE VIEW %s AS ", createView.getView()));
		createPlainSelect(createView, sql, create);
		return sql.toString();
	}

	@Override
	public String createSql(AlterViewBuilderImpl alterView, boolean create) {
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("DROP VIEW %s; CREATE VIEW %s AS ", alterView.getView(), alterView.getView()));
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
			i->"", i->", ", c->getColumn(c, x->appendix.append(", ").append(x))
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
	public List<String> createSql(AlterTableBuilderImpl alterTable) {
		List<String> rows = new LinkedList<>();
		List<String> constains = new LinkedList<>();
		iterateList(
			sql->rows.add(sql), alterTable.getAddColumns(),
			i->"", i->"", c->"ADD " + getColumn(c, x->constains.add(x))
		);
		rows.addAll(constains);
		createAddForeignKey(alterTable.getAddForeignKeys(), sql->rows.add(sql), "ADD ");
		
		iterateList(
			sql->rows.add(sql), alterTable.getDeleteForeignKeys(),
			i->"", i->"", fk->"DROP CONSTRAINT " + fk.getColumn()
		);
		iterateList(
			sql->rows.add(sql), alterTable.getDeleteColumns(),
			i->"", i->"", c->"DROP COLUMN " + c.getName()
		);

		alterTable.getModifyColumns().forEach(c->{
			if (c.getColumnType() != null) {
				rows.add("ALTER COLUMN " + c.getName() + " TYPE " + toString(c.getColumnType()));
			}
			Optional<DefaultValue> defValue = c.getDefValue();
			if (defValue != null) {
				if (defValue.isEmpty()) {
					rows.add("ALTER COLUMN " + c.getName() + " DROP DEFAULT");
				}
				if (defValue.isPresent()) {
					rows.add("ALTER COLUMN " + c.getName() + " SET DEFAULT " + defValue.get().getValue(getEscape()));
				}
			}
			if (c.getIsNullable() != null) {
				if (c.getIsNullable()) {
					rows.add("ALTER COLUMN " + c.getName() + " DROP NOT NULL");
				} else {
					rows.add("ALTER COLUMN " + c.getName() + " SET NOT NULL");
				}
			}
			if (c.getIsUnique() != null) {
				String key = (alterTable.getTable() + "_" + c.getName() + "_key").toLowerCase();
				if (c.getIsUnique()) {
					rows.add("ADD CONSTRAINT " + key + " UNIQUE (" + c.getName() + ")");
				} else {
					rows.add("DROP CONSTRAINT " + key);
				}
			}
		});

		String alterTablePrefix = "ALTER TABLE " + alterTable.getTable();
		
		List<String> result = new LinkedList<>();
		if (!rows.isEmpty()) {
			StringBuilder main = new StringBuilder();
			main.append(alterTablePrefix);
			iterateList(main, rows, i->" ", i->", ", i->i);
			result.add(main.toString());
		}
		// only one rename at once https://stackoverflow.com/a/74110573/8240462
		iterateList(
			sql->result.add(sql), alterTable.getRenameColumns(),
			i->alterTablePrefix, i->alterTablePrefix,
			c->String.format(" RENAME COLUMN %s TO %s", c.getOldName(), c.getNewName())
		);
		if (alterTable.getNewName() != null) {
			result.add(alterTablePrefix + " RENAME TO " + alterTable.getNewName());
		}
		return result;
	}
	
	/****************************/

	protected String toString(ColumnType type) {
		return switch (type.getType()) {
			case STRING->String.format("VARCHAR(%s)", type.getSize());
			case CHAR->String.format("CHAR(%s)", type.getSize());
			case TIME->{
				if (type.getSize() == null) {
					yield  "TIME";
				}
				yield  String.format("TIME(%s)", type.getSize());
			}
			case DATETIME->{
				if (type.getSize() == null) {
					yield  "TIMESTAMP";
				}
				yield  String.format("TIMESTAMP(%s)", type.getSize());
			}
			case DATETIME_ZONED->{
				if (type.getSize() == null) {
					yield "TIMESTAMPTZ";
				}
				yield  String.format("TIMESTAMPTZ(%s)", type.getSize());
			}
			default->type.getType().toString();
		};
	}

	protected String toString(ColumnSetting settings) {
		return switch (settings) {
			case AUTO_INCREMENT->"SERIAL";
			case UNIQUE->"UNIQUE";
			case NOT_NULL->"NOT NULL";
			case NULL->"NULL";
			// never happends: case PRIMARY_KEY: return "";
			default->"";
		};
	}

	protected String toString(OnAction action) {
		return switch (action) {
			case RESTRICT->"RESTRICT";
			case CASCADE->"CASCADE";
			case SET_NULL->"SET NULL";
			case NO_ACTION->"NO ACTION";
			case SET_DEFAULT->"SET DEFAULT";
			default->throw new RuntimeException("Not implemented action: " + action);
		};
	}

	protected String toString(Join join) {
		return switch(join) {
			case FULL_OUTER_JOIN->throw new RuntimeException("Full Outer Join is not supported by postgres");
			case INNER_JOIN->"JOIN";
			case LEFT_OUTER_JOIN->"LEFT JOIN";
			case RIGHT_OUTER_JOIN->"RIGHT JOIN";
			default->throw new RuntimeException("Not implemented join: " + join);
		};
	}

	protected String toString(SelectJoin join) {
		return switch(join) {
			case UNION_ALL->"UNION ALL";
			default->join.toString();
		};
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
			result.append(" AS ").append(alias);
		}
		return result.toString();
	}
	
	private String getColumn(Column column, Consumer<String> onConstaint) {
		StringBuilder result = new StringBuilder();
		result.append(column.getName());
		if (!column.getSettings().contains(ColumnSetting.AUTO_INCREMENT)) {
			result.append(" ");
			result.append(toString(column.getType()));
		}
		if (column.getValue()!= null) {
			result.append(" DEFAULT ");
			result.append(column.getValue().getValue(getEscape()));
		}
		
		for (ColumnSetting settings : column.getSettings()) {
			if (settings == ColumnSetting.PRIMARY_KEY) {
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
		createWhere(builder.getWheres(), sql);
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
			sql.append(" LIMIT ").append(builder.getLimit());
		}
		if (builder.getOffset() != null) {
			sql.append(" OFFSET ").append(builder.getOffset());
		}
	}
	
	private void createWith(List<Tuple2<String, SubSelect>> withs, StringBuilder sql, boolean create) {
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
	
	private void createWhere(List<Tuple2<String, Where>> wheres, StringBuilder sql) {
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
		sql.append(" ON ");
		sql.append(join.getOn());
	}
	
}
