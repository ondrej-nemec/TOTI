package toti.lib.database.querybuilder.instances;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import toti.lib.database.querybuilder.DbInstance;
import toti.lib.database.querybuilder.Escape;
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
import toti.lib.common.functions.Implode;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.common.structures.Tuple2;

public class SqlServerQueryBuilder implements DbInstance {

	@Override
	public Escape getEscape() {
		return new Escape() {
			@Override
			protected String escapeBoolean(Object value) {
				return Boolean.class.cast(value) ? "1" : "0";
			}
			
		};
	}

	@Override
	public String concat(String param1, String param2, String... params) {
		StringBuilder builder = new StringBuilder(String.format("CONCAT(%s, %s", param1, param2));
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
			"STRING_AGG(%s, '%s')%s",
			param, delimeter, orderBy == null ? "" : " WITHIN GROUP (ORDER BY " + orderBy + ")"
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
		return "{"
			+ (callProcedure.isOutput() ? "? = " : "")
			+ "CALL "
			+ callProcedure.getProcedure() + "("
			+ Implode.implode(", ", callProcedure.getParameters())
			+ ")}";
	}

	@Override
	public String createSql(DeleteIndexBuilderImpl deleteIndex) {
		return "DROP INDEX " + deleteIndex.getIndexName() + " ON " + deleteIndex.getTable();
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
	public List<String> createSql(InsertBuilderImpl insert, boolean create) {
		List<String> result = new LinkedList<>();
		if (insert.getIdName().isPresent()) {
			result.add("SET IDENTITY_INSERT " + insert.getTable() + " ON");
		}
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
		result.add(sql.toString());
		if (insert.getIdName().isPresent()) {
			result.add("SET IDENTITY_INSERT " + insert.getTable() + " OFF");
			result.add(String.format(
				"DECLARE @nextId INT;"
				+ "SELECT @nextId = ISNULL(MAX(%s), 0) FROM %s;"
				+ "DBCC CHECKIDENT ('%s', RESEED, @nextId)",
				insert.getIdName().get(), insert.getTable(), insert.getTable()
			));
			/*result.add(String.format(
				"DBCC CHECKIDENT ('%s', RESEED, (SELECT ISNULL(MAX(%s), 0) FROM %s))",
				insert.getTable(), insert.getIdName().get(), insert.getTable()
			));*/
			/*

SET IDENTITY_INSERT table_name OFF;
DBCC CHECKIDENT ('table_name', RESEED, (SELECT ISNULL(MAX(id), 0) FROM table_name));
			*/
		}
		return result;
	}

	@Override
	public String createSql(UpdateBuilderImpl updateBuilder, boolean create) {
		StringBuilder sql = new StringBuilder();
		createWith(updateBuilder.getWiths(), sql, create);
		sql.append("UPDATE ");
		if (updateBuilder.getJoins().isEmpty()) {
			sql.append(updateBuilder.getTable());
		} else {
			sql.append(updateBuilder.getAlias() == null ? updateBuilder.getTable() : updateBuilder.getAlias());
		}
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
		if (!updateBuilder.getJoins().isEmpty()) {
			sql.append(" FROM ");
			sql.append(getWithAlias(updateBuilder.getTable(), updateBuilder.getAlias()));
			
		}
		updateBuilder.getJoins().forEach(join->{
			createJoin(join, sql, create);
		});
		createWhere(updateBuilder.getWheres(), sql);
		return sql.toString();
	}

	@Override
	public String createSql(DeleteBuilderImpl delete, boolean create) {
		StringBuilder sql = new StringBuilder();
		createWith(delete.getWiths(), sql, create);
		sql.append("DELETE ");
		sql.append(delete.getAlias() == null ? delete.getTable() : delete.getAlias());
		sql.append(" FROM ");
		sql.append(getWithAlias(delete.getTable(), delete.getAlias()));
		
		StringBuilder joins = new StringBuilder();
		StringBuilder wheres = new StringBuilder();

		delete.getJoins().forEach(join->{
			createJoin(join, sql, create);
		});
		createWhere(delete.getWheres(), sql);
		
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
		// sql.append("DROP VIEW " + alterView.getView() + "; ");
		sql.append(String.format("ALTER VIEW %s AS ", alterView.getView()));
		createPlainSelect(alterView, sql, create);
		return sql.toString();
	}

	@Override
	public String createSql(DeleteViewBuilderImpl deleteView) {
		return "DROP VIEW IF EXISTS " + deleteView.getView();
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
			i->"", i->", ", c->getColumn(createTable.getTable(), c, x->appendix.append(", ").append(x))
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
		String prefix = "ALTER TABLE " + alterTable.getTable() + " ";
		List<String> result = new LinkedList<>();

		iterateAlterTable(result, addCol->iterateList(
			sql->addCol.append(sql), alterTable.getAddColumns(),
			i->prefix + "ADD ", i->", ", c->getColumn(alterTable.getTable(), c, x->{})
		));
		iterateAlterTable(result, dropCol->iterateList(
			sql->dropCol.append(sql), alterTable.getDeleteColumns(),
			i->prefix + "DROP COLUMN ", i->", ", c->c.getName()
		));


		alterTable.getModifyColumns().forEach(c->{
			Optional<DefaultValue> defValue = c.getDefValue();
			String uniqueKey = "UQ_" + alterTable.getTable() + "_" + c.getName();
			String defaultKey = "DF_" + alterTable.getTable() + "_" + c.getName();

			if (defValue != null && (defValue.isEmpty() || defValue.get().isModify())) {
				result.add(prefix + "DROP CONSTRAINT " + defaultKey);
			}
			if (c.getIsUnique() != null && !c.getIsUnique()) {
				result.add(prefix + "DROP CONSTRAINT " + uniqueKey);
			}
			if (c.getColumnType() != null) {
				result.add(
					prefix + "ALTER COLUMN " + c.getName() + " " + toString(c.getColumnType())
					+ (c.getIsNullable() == null ? "" : c.getIsNullable() ? " NULL" : " NOT NULL")
				);
			} else if (c.getIsNullable() != null) {
				throw new RuntimeException("SQL Server not support change null / not null without data type.");
			}
			if (c.getIsUnique() != null && c.getIsUnique()) {
				result.add(prefix + "ADD CONSTRAINT " + uniqueKey + " UNIQUE (" + c.getName() + ")");
			}
			if (defValue != null && defValue.isPresent()) {
				result.add(String.format(
					prefix + "ADD CONSTRAINT %s DEFAULT %s FOR %s",
					defaultKey, defValue.get().getValue(getEscape()), c.getName()
				));
			}
		});

		createAddForeignKey(alterTable.getAddForeignKeys(), sql->result.add(prefix + sql), "ADD ");
		iterateList(
			sql->result.add(prefix + sql), alterTable.getDeleteForeignKeys(),
			i->"", i->"", fk->"DROP CONSTRAINT " + fk.getColumn()
		);
		
		iterateList(
			sql->result.add(sql), alterTable.getRenameColumns(),
			i->"", i->"", c->String.format(
				"EXEC sp_rename '%s.%s', '%s', 'COLUMN'", alterTable.getTable(), c.getOldName(), c.getNewName()
			)
		);
		if (alterTable.getNewName() != null) {
			result.add(String.format(
				"EXEC sp_rename '%s', '%s'", alterTable.getTable(), alterTable.getNewName()
			));
		}
		return result;
	}

	private void iterateAlterTable(List<String> result, Consumer<StringBuilder> iterate) {
		StringBuilder sql = new StringBuilder();
		iterate.accept(sql);
		if (!sql.isEmpty()) {
			result.add(sql.toString());
		}
	}

	@Override
	public String createSql(DeleteTableBuilderImpl deleteTable) {
		return "DROP TABLE " + deleteTable.getTable();
	}

	/************************************************/

	protected String toString(ColumnType type) {
		return switch (type.getType()) {
			case STRING -> String.format("VARCHAR(%s)", type.getSize());
			case CHAR -> String.format("CHAR(%s)", type.getSize());
			case BOOLEAN -> "BIT";
			case TIME -> {
				if (type.getSize() == null) {
					yield "TIME";
				}
				yield String.format("TIME(%s)", type.getSize());
			}
			case DATETIME -> {
				if (type.getSize() == null) {
					yield "DATETIME2";
				}
				yield String.format("DATETIME2(%s)", type.getSize());
			}
			case DATETIME_ZONED -> {
				if (type.getSize() == null) {
					yield "DATETIMEOFFSET";
				}
				yield String.format("DATETIMEOFFSET(%s)", type.getSize());
			}
			default -> type.getType().toString();
		};
	}
	
	protected String toString(Join join) {
		return switch(join) {
			case FULL_OUTER_JOIN -> throw new RuntimeException("Full Outer Join is not supported by mysql");
			case INNER_JOIN -> "JOIN";
			case LEFT_OUTER_JOIN -> "LEFT JOIN";
			case RIGHT_OUTER_JOIN -> "RIGHT JOIN";
			default -> throw new RuntimeException("Not implemented join: " + join);
		};
	}

	protected String toString(SelectJoin join) {
		return switch (join) {
			case UNION_ALL -> "UNION ALL";
			default -> join.toString();
		};
	}

	protected String toString(ColumnSetting settings) {
		return switch (settings) {
			case AUTO_INCREMENT -> "IDENTITY(1,1)";
			case UNIQUE -> "UNIQUE";
			case NOT_NULL -> "NOT NULL";
			case NULL -> "NULL";
			// never happends: case PRIMARY_KEY: return "";
			default -> "";
		};
	}

	protected String toString(OnAction action) {
		return switch (action) {
			case RESTRICT -> throw new RuntimeException("Not supported operation");
				//return "RESTRICT";
			case CASCADE -> "CASCADE";
			case SET_NULL -> "SET NULL";
			case NO_ACTION -> "NO ACTION";
			case SET_DEFAULT -> "SET DEFAULT";
			default -> throw new RuntimeException("Not implemented action: " + action);
		};
	}
	
	/*****************************/
	
	private String getColumn(String tableName, Column column, Consumer<String> onConstaint) {
		StringBuilder result = new StringBuilder();
		result.append(column.getName());
		result.append(" ");
		result.append(toString(column.getType()));

		// need order
		for (ColumnSetting settings : new ColumnSetting[] {
			ColumnSetting.AUTO_INCREMENT, ColumnSetting.NOT_NULL, ColumnSetting.NULL, ColumnSetting.UNIQUE
		}) {
			if (!column.getSettings().contains(settings)) {
				continue;
			}
			switch (settings) {
				case UNIQUE -> {
					result.append(String.format(" CONSTRAINT UQ_%s_%s UNIQUE", tableName, column.getName()));
				}
				default -> {
					result.append(" ");
					result.append(toString(settings));
				}
			}
		}
		if (column.getValue() != null) {
			result.append(String.format(
				" CONSTRAINT DF_%s_%s DEFAULT %s",
				tableName, column.getName(), column.getValue().getValue(getEscape())
			));
		}
		
		if (column.getSettings().contains(ColumnSetting.PRIMARY_KEY)){
			onConstaint.accept(String.format("PRIMARY KEY (%s)", column.getName()));
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
			i->{
				String res = "SELECT ";
				if (builder.getOffset() == null && builder.getLimit() != null) {
					res += "TOP " + builder.getLimit() + " ";
				}
				return res;
			}, i->", ", i->i
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
		if (builder.getOffset() != null && builder.getLimit() != null) {
			if (builder.getOrderBy().isEmpty()) {
				sql.append(" ORDER BY (SELECT null)");
			}
			sql.append(String.format(
				" OFFSET %s ROWS FETCH NEXT %s ROWS ONLY",
				builder.getOffset(), builder.getLimit()
			));
		}
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
	
	private String getWithAlias(String table, String alias) {
		StringBuilder result = new StringBuilder();
		result.append(table);
		if (alias != null) {
			result.append(" AS ");
			result.append(alias);
		}
		return result.toString();
	}

	private void createWith(List<Tuple2<String, SubSelect>> withs, StringBuilder sql, boolean create) {
		iterateList(
			sql,
			withs,
			(item)->"WITH",
			(with)->",",
			(with)->{
				String subquery = create ? with._2().createSql() : with._2().getSql();
				// sql server needs UNION ALL
				boolean isRecursive = subquery.contains(" " + with._1() + " ") || subquery.endsWith(" " + with._1());
				if (isRecursive) {
					subquery = subquery.replaceFirst(" UNION ", " UNION ALL ");
				}
				return String.format(
					" %s AS (%s)", with._1(), subquery
				);
			}
		);
		if (!withs.isEmpty()) {
			sql.append(" ");
		}
	}
	
	private <T> void iterateList(
			StringBuilder sql, List<T> list,
			Function<T, String> onFirst, Function<T, String> onOthers, Function<T, String> otherwise) {
		iterateList(s->sql.append(s), list, onFirst, onOthers, otherwise);
	}
	
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
}
