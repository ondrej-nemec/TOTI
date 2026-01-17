package ji.querybuilder.instances;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ji.database.Connections;
import toti.files.text.Text;
import ji.querybuilder.Builder;
import ji.querybuilder.DbInstance;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.AlterTableBuilder;
import ji.querybuilder.builders.AlterViewBuilder;
import ji.querybuilder.builders.CreateTableBuilder;
import ji.querybuilder.builders.CreateViewBuilder;
import ji.querybuilder.builders.DeleteBuilder;
import ji.querybuilder.builders.InsertBuilder;
import ji.querybuilder.builders.SelectBuilder;
import ji.querybuilder.builders.UpdateBuilder;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.Join;
import ji.querybuilder.enums.OnAction;
import ji.querybuilder.enums.Where;
import ji.querybuilder.structures.ProcedureResult;
import toti.common.structures.ThrowingConsumer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractInstanceTest {
	
	// TODO escape columns https://stackoverflow.com/questions/2901453/sql-standard-to-escape-column-names
	
	private final DbInstance instance;
	private final Connections connections;
	private boolean isInit = false;
	
	public AbstractInstanceTest(DbInstance instance) {
		this.instance = instance;
		this.connections = Connections.QUERY_BUILDER();
	}
	
	@ParameterizedTest
	@MethodSource("dataFunctions")
	public void testFunctions(String message, Function<QueryBuilder, SelectBuilder> create, String expected) throws Exception {
		test(create, expected, expected, b->b.fetchAll());
	}
	
	public Object[] dataFunctions() {
		return new Object[] {
			new Object[] {
				"Concat",
				f(b->b.select(f->f.concat("'\"'", "name", "'\"'")).from("table_for_functions")), getFunctions_concat()
			},
			new Object[] {
				"Group concat",
				f(b->b.select(f->f.groupConcat("name", ",")).from("table_for_functions").groupBy("name")), getFunctions_groupConcat()
			},
			new Object[] {
				"Group concat over",
				f(b->b.select(f->f.groupConcat("name", ",", "id")).from("table_for_functions").groupBy("name")), getFunctions_groupConcatOrderBy()
			},
			new Object[] {
				"Cast",
				f(b->b.select(f->f.cast("id", ColumnType.floatType())).from("table_for_functions")), getFunctions_cast()
			},
			new Object[] {
				"Max",
				f(b->b.select(f->f.max("id")).from("table_for_functions")), getFunctions_max()
			},
			new Object[] {
				"Min",
				f(b->b.select(f->f.min("id")).from("table_for_functions")), getFunctions_min()
			},
			new Object[] {
				"Avg",
				f(b->b.select(f->f.avg("id")).from("table_for_functions")), getFunctions_avg()
			},
			new Object[] {
				"sum",
				f(b->b.select(f->f.sum("id")).from("table_for_functions")), getFunctions_sum()
			},
			new Object[] {
				"Count",
				f(b->b.select(f->f.count("id")).from("table_for_functions")), getFunctions_count()
			},
			new Object[] {
				"Lower",
				f(b->b.select(f->f.lower("name")).from("table_for_functions")), getFunctions_lower()
			},
			new Object[] {
				"Upper",
				f(b->b.select(f->f.upper("name")).from("table_for_functions")), getFunctions_upper()
			}
		};
	}
	
	protected abstract String getFunctions_concat();

	protected abstract String getFunctions_groupConcat();

	protected abstract String getFunctions_groupConcatOrderBy();

	protected abstract String getFunctions_cast();

	protected abstract String getFunctions_max();

	protected abstract String getFunctions_min();

	protected abstract String getFunctions_avg();

	protected abstract String getFunctions_sum();

	protected abstract String getFunctions_count();

	protected abstract String getFunctions_lower();

	protected abstract String getFunctions_upper();

	/********* TABLE **************/
	
	@ParameterizedTest
	@MethodSource("dataCreateTable")
	public void testCreateTable(String message, Function<QueryBuilder, CreateTableBuilder> create, String getSql) throws Exception {
		test(create, getSql, b->b.execute()); // VERIFY ?
	}
	
	public Collection<Object[]> dataCreateTable() {
		Collection<Object[]> result = new LinkedList<>();
		result.add(_createTable("Full", true, true));
		result.add(_createTable("Without restrict", false, true));
		result.add(_createTable("Without set default", true, false));
		result.add(new Object[] {
			"Multiple primary",
			f(b->b.createTable("create_table_2")
				.addColumn("Primary_column_1", ColumnType.integer())
				.addColumn("Primary_column_2", ColumnType.integer())
				.addColumn("Primary_column_3", ColumnType.integer())
				.setPrimaryKey("Primary_column_1", "Primary_column_2", "Primary_column_3")
				.addForeignKey("Primary_column_3", "table_for_index_1", "id")
			),
			getCreateTableWithPrimary()
		});
		return result;
	}

	private Object[] _createTable(String name, boolean withRestrict, boolean withSetDefault) {
		return new Object[] {
			name,
			f(b->b.createTable("create_table")
				.addColumn("Primary_column", ColumnType.integer(), ColumnSetting.PRIMARY_KEY, ColumnSetting.AUTO_INCREMENT, ColumnSetting.NOT_NULL)
				.addColumn("Unique_column", ColumnType.integer(), ColumnSetting.UNIQUE)
				.addColumn("Nullable_column", ColumnType.integer(), 42, ColumnSetting.NULL)
				
				.addColumn("Bool_column", ColumnType.bool())
				.addColumn("Float_column", ColumnType.floatType())
				.addColumn("Double_column", ColumnType.doubleType())
				.addColumn("Char_column", ColumnType.charType(1))
				.addColumn("Text_column", ColumnType.text())
				.addColumn("String_column", ColumnType.string(10))
				.addColumn("Time_column", ColumnType.time())
				.addColumn("Time2_column", ColumnType.time(6))
				.addColumn("Date_column", ColumnType.date())
				.addColumn("DateTime_column", ColumnType.datetime())
				.addColumn("DateTime2_column", ColumnType.datetime(6))
				.addColumn("DateTime_Zoned_column", ColumnType.datetimeZoned())
				.addColumn("DateTime_Zoned2_column", ColumnType.datetimeZoned(6))
				.addColumn("FK_column_1", ColumnType.integer())
				.addColumn("FK_column_2", ColumnType.integer())
				.addColumn("FK_column_3", ColumnType.integer())
				.addColumn("FK_column_4", ColumnType.integer())
				
				.addForeignKey("FK_column_1", "table_for_index_1", "id")
				.addForeignKey("FK_column_2", "table_for_index_2", "id", OnAction.CASCADE, OnAction.NO_ACTION)
				.addForeignKey(
					"FK_column_3", "table_for_index_3",
					"id",
					withRestrict ? OnAction.RESTRICT : OnAction.NO_ACTION,
					withSetDefault ? OnAction.SET_DEFAULT : OnAction.NO_ACTION
				)
				.addForeignKey("FK_column_4", "table_for_index_4", "id", OnAction.SET_NULL, null)
			),
			getCreateTable(withRestrict, withSetDefault)
		};
	}

	protected abstract String getCreateTable(boolean withRestrict, boolean withSetDefault);

	protected abstract String getCreateTableWithPrimary();
	
	@ParameterizedTest
	@MethodSource("dataAlterTable")
	public void testAlterTable(String message, Function<QueryBuilder, AlterTableBuilder> create, String getSql) throws Exception {
		test(create, getSql, b->b.execute()); // VERIFY ?
		
	}
	
	public Object[] dataAlterTable() {
		return new Object[] {
			new Object[] {
				"All",
				f(
					b->b.alterTable("table_to_alter")
					.addColumn("Add_column_1", ColumnType.integer(), ColumnSetting.NOT_NULL)
					.addColumn("Add_column_2", ColumnType.integer(), 42, ColumnSetting.UNIQUE, ColumnSetting.NULL)
					.addForeignKey("Add_column_1", "table_for_index_1", "id")
					.addForeignKey("Add_column_2", "table_for_index_2", "id", OnAction.CASCADE, OnAction.NO_ACTION)
					.deleteColumn("Column_to_delete")
					.deleteForeingKey("FK_to_delete")
					
					.modifyColumnType("Column_to_modify_1", ColumnType.floatType())
					.setColumnNullable("Column_to_modify_1")
					.modifyColumnDefault("Column_to_modify_1", 5)
					.removeColumnUnique("Column_to_modify_1")

					.modifyColumnType("Column_to_modify_2", ColumnType.floatType())
					.setColumnNotNull("Column_to_modify_2")
					.setColumnUnique("Column_to_modify_2")
					.removeColumnDefault("Column_to_modify_2")

					.modifyColumn("Column_to_modify_3", c->{
						c.addDefault(42);
						c.setColumnType(ColumnType.string(255));
					})

					.renameColumn("Column_to_rename", "Renamed_column", ColumnType.integer())
				),
				getAlterTable(true)	
			},
			new Object[] {
				"Rename table",
				f(
					b->b.alterTable("table_to_rename")
					.renameTable("table_with_another_name")
				),
				getAlterTableRenameTable()	
			},
			new Object[] {
				"Simplified",
				// changes supported by sqlite 
				f(
					b->b.alterTable("table_to_alter_2")
					.addColumn("Add_column_1", ColumnType.integer(), ColumnSetting.NOT_NULL)
				//	.addColumn("Add_column_2", ColumnType.integer(), 42, ColumnSetting.UNIQUE, ColumnSetting.NULL)
				//	.addForeignKey("Add_column_1", "table_for_index", "id")
				//	.addForeignKey("Add_column_2", "table_for_index", "id", OnAction.CASCADE, OnAction.NO_ACTION)
					.deleteColumn("Column_to_delete")
				//	.deleteForeingKey("FK_to_delete")
					
				//	.modifyColumnType("Column_to_modify_1", ColumnType.floatType())
				//	.modifyColumnDefault("Column_to_modify_1", 5)
				//	.setColumnNullable("Column_to_modify_1")
				//	.removeColumnUnique("Column_to_modify_1")

				//	.modifyColumnType("Column_to_modify_2", ColumnType.floatType())
				//	.removeColumnDefault("Column_to_modify_2")
				//	.setColumnNotNull("Column_to_modify_2")
				//	.setColumnUnique("Column_to_modify_2")
					.renameColumn("Column_to_rename", "Renamed_column", ColumnType.integer())
				),
				getAlterTable(false)	
			},
		};
	}
	
	protected abstract String getAlterTable(boolean full);
	
	protected abstract String getAlterTableRenameTable();

	@Test
	public void testDeleteTable() throws Exception {
		test(
			b->b.deleteTable("table_to_delete"),
			getDeleteTable(),
			b->b.execute() // VERIFY ?
		);
	}
	
	protected abstract String getDeleteTable();
	
	/********* VIEW **************/

	@ParameterizedTest
	@MethodSource("dataCreateView")
	public void testCreateView(
			Function<QueryBuilder, CreateViewBuilder> create, String getSql, String createSql
		) throws Exception {
		test(create, getSql, createSql, b->b.execute()); // VERIFY ?
	}
	
	public Object[] dataCreateView() {
		return new Object[] {
			new Object[] {
				f(b->b
					.createView("some_view_1")
					.select("1 as A")
				),
				getCreateView_fromString(false),
				getCreateView_fromString(true)
			},
			new Object[] {
				f(b->b
					.createView("some_view_2")
					.select("A")
					.from(
						b.select("1 AS A"),
						"a"
					)
				),
				getCreateView_fromSelect(false),
				getCreateView_fromSelect(true)
			},
			new Object[] {
				f(b->b
					.createView("some_view_3")
					.select("A")
					.from(
						b.multiSelect(b.select("1 AS A"))
						.union(b.select("2 AS A")),
						"a"
					)
				),
				getCreateView_fromMultiSelect(false),
				getCreateView_fromMultiSelect(true)
			},
			new Object[] {
				f(b->b
					.createView("some_view_4")

					.select("t1.id")
					.select("t1.name")
					.select(f->f.max("t1.id") + " as max_id")
					
					.from("table_1", "t1")
					
					.join("table_2", Join.INNER_JOIN, "t1.id = table_2.id")
					.join("table_3", "t3", Join.LEFT_OUTER_JOIN, "t1.id = t3.id")
					.join(b.select("*").from("table_4"), "st4", Join.RIGHT_OUTER_JOIN, "t3.id = st4.id")
					.join("table_5", Join.INNER_JOIN, f->"t1.id = table_5.id")
					.join("table_6", "t6", Join.LEFT_OUTER_JOIN, f->"t1.id = t6.id")
					.join(b.select("*").from("table_7"), "st7", Join.RIGHT_OUTER_JOIN, f->"t1.id = st7.id")
					
					.where("t1.id = 1")
					.where("t1.id != 1", Where.OR)
					.where(f->"t1.id = table_2.id")
					.where(f->"t1.id = t6.id", Where.OR)
					
					.groupBy("t1.id")
					.groupBy("t1.name")
					
					.having(":a != :b")
					.having(f->f.max("t1.id") + " < :max_id")
					
					.orderBy("t1.id")
					.orderBy(f->f.max("t1.id"))
					.limit(10, 15)
					.addParameter(":max_id", 10)
					.addParameter(":a", "AAAA")
					.addParameter(":b", "BBBB")
				),
				getCreateView(false),
				getCreateView(true)
			}
		};
	}
	
	protected abstract String getCreateView_fromString(boolean create);
	protected abstract String getCreateView_fromSelect(boolean create);
	protected abstract String getCreateView_fromMultiSelect(boolean create);
	protected abstract String getCreateView(boolean create);

	@ParameterizedTest
	@MethodSource("dataAlterView")
	public void testAlterView(
			Function<QueryBuilder, AlterViewBuilder> alter, String getSql, String createSql
		) throws Exception {
		test(alter, getSql, createSql, b->b.execute()); // VERIFY ?
	}
	
	public Object[] dataAlterView() {
		return new Object[] {
			new Object[] {
				f(b->b
					.alterView("view_to_alter")
					.select("id")
					.from("table_1")
				),
				getAlterView_fromString(false),
				getAlterView_fromString(true)
			},
			new Object[] {
				f(b->b
					.alterView("view_to_alter")
					.select("id")
					.from("table_1", "a")
				),
				getAlterView_fromStringAlias(false),
				getAlterView_fromStringAlias(true)
			},
			new Object[] {
				f(b->b
					.alterView("view_to_alter")
					.select("A")
					.from(
						b.select("1 AS A"),
						"a"
					)
				),
				getAlterView_fromSelect(false),
				getAlterView_fromSelect(true)
			},
			new Object[] {
				f(b->b
					.alterView("view_to_alter")
					.select("A")
					.from(
						b.multiSelect(b.select("1 AS A"))
						.union(b.select("2 AS A")),
						"a"
					)
				),
				getAlterView_fromMultiSelect(false),
				getAlterView_fromMultiSelect(true)
			},
			new Object[] {
				f(b->b
					.alterView("view_to_alter")

					.select("t1.id")
					.select("t1.name")
					.select(f->f.max("t1.id") + " as max_id")
					
					.from("table_1", "t1")
					
					.join("table_2", Join.INNER_JOIN, "t1.id = table_2.id")
					.join("table_3", "t3", Join.LEFT_OUTER_JOIN, "t1.id = t3.id")
					.join(b.select("*").from("table_4"), "st4", Join.RIGHT_OUTER_JOIN, "t3.id = st4.id")
					.join("table_5", Join.INNER_JOIN, f->"t1.id = table_5.id")
					.join("table_6", "t6", Join.LEFT_OUTER_JOIN, f->"t1.id = t6.id")
					.join(b.select("*").from("table_7"), "st7", Join.RIGHT_OUTER_JOIN, f->"t1.id = st7.id")
					
					.where("t1.id = 1")
					.where("t1.id != 1", Where.OR)
					.where(f->"t1.id = table_2.id")
					.where(f->"t1.id = t6.id", Where.OR)
					
					.groupBy("t1.id")
					.groupBy("t1.name")
					
					.having(":a != :b")
					.having(f->f.max("t1.id") + " < :max_id")
					
					.orderBy("t1.id")
					.orderBy(f->f.max("t1.id"))
					.limit(10, 15)
					.addParameter(":max_id", 10)
					.addParameter(":a", "AAAA")
					.addParameter(":b", "BBBB")
				),
				getAlterView(false),
				getAlterView(true)
			}
		};
	}
	
	protected abstract String getAlterView_fromString(boolean create);
	protected abstract String getAlterView_fromStringAlias(boolean create);
	protected abstract String getAlterView_fromSelect(boolean create);
	protected abstract String getAlterView_fromMultiSelect(boolean create);
	protected abstract String getAlterView(boolean create);

	@Test
	public void testDeleteView() throws Exception {
		test(b->b.deleteView("view_to_delete"), getDeleteView(), b->b.execute()); // VERIFY ?
	}
	
	protected abstract String getDeleteView();
	
	/********* INDEX **************/

	@Test
	public void testCreateIndex() throws Exception {
		test(b->b.createIndex("index_name", "table_for_index_1", "id", "name"), getCreateIndex(), b->b.execute()); // VERIFY ?
	}
	
	protected abstract String getCreateIndex();

	@Test
	public void testDeleteIndex() throws Exception {
		test(b->b.deleteIndex("index_to_delete", "table_for_index_1"), getDeleteIndex(), b->b.execute()); // VERIFY ?
	}
	
	protected abstract String getDeleteIndex();
	
	/********* QUERING **************/

	@ParameterizedTest
	@MethodSource("dataQueryInsert")
	public void testQueryInsert(String message, Function<QueryBuilder, InsertBuilder> alter, String getSql, String createSql) throws Exception {
		test(alter, getSql, createSql, b->b.execute()); // VERIFY ? result + next id
	}
	
	public Object[] dataQueryInsert() {
		return new Object[] {
			new Object[] {
				"Query Insert",
				f(
					b->b.insert("table_1")
					.addValue("id", 123)
					.addValue("name", "Item 123")
					.addValue("typ", 'X')
				),
				getQueryInsert(),
				getQueryInsert()
			},
			new Object[] {
				"Query Insert from Select",
				f(
					b->b
					.with("cte", b.select("id, name").from("table_2").where("id = 2"))
					.with("cte2", b.select("id, name").from("table_2").where("id = 2"))
					.insert("table_1")
					.fromSelect(
						Arrays.asList("id", "name", "typ"),
						b.select("id", "name", ":type")
						.from("cte")
						.addParameter(":type", 'X')
					)
				),
				getQueryInsertFromSelect(false),
				getQueryInsertFromSelect(true)
			},
			new Object[] {
				"Query Insert check Auto Increment",
				// check autoincrement
				f(
					b->b
					.insert("table_ai", Optional.of("id"))
					.addValue("id", 123)
					.addValue("name", "Item 123")
					.addValue("typ", 'X')
				),
				getQueryInsertOverrideAI(),
				getQueryInsertOverrideAI()
			}
		};
	}
	
	protected abstract String getQueryInsert();
	
	protected abstract String getQueryInsertFromSelect(boolean create);

	protected abstract String getQueryInsertOverrideAI();

	@ParameterizedTest
	@MethodSource("dataQueryUpdate")
	public void testQueryUpdate(String message, Function<QueryBuilder, UpdateBuilder> alter, String getSql, String createSql) throws Exception {
		test(alter, getSql, createSql, b->b.execute()); // VERIFY ?
	}
	
	public Object[] dataQueryUpdate() {
		boolean useAlias = useQueryUpdateJoinsAlias();
		return new Object[] {
			new Object[] {
				"Update Basic",
				f(
					b->b.update("table_1")
					.set("name = :value").addParameter(":value", 123)
					.set(f->"typ = " + f.upper("'x'"))
					.where("id = :id")
					.where("id = :id", Where.OR)
					.where(f->"id = :id")
					.where(f->"id = :id", Where.OR)
					.addParameter(":id", 1)
				),
				getQueryUpdateBasic(false),
				getQueryUpdateBasic(true)
			},
			new Object[] {
				"Update Joins",
				f(
					b->b.update("table_1", "t1")
					.set((useAlias ? "t1." : "") + "name = :value").addParameter(":value", 123)
					.set(f->(useAlias ? "t1." : "") + "typ = " + f.upper("'x'"))

					.join("table_2", Join.INNER_JOIN, "table_2.id = t1.id")
					.join("table_3", "t3", Join.LEFT_OUTER_JOIN, "table_2.id = t3.id")
					.join(b.select("*").from("table_4"), "st4", Join.RIGHT_OUTER_JOIN, "t3.id = st4.id")
					.join("table_5", Join.INNER_JOIN, f->"table_2.id = table_5.id")
					.join("table_6", "t6", Join.LEFT_OUTER_JOIN, f->"table_2.id = t6.id")
					.join(b.select("*").from("table_7"), "st7", Join.RIGHT_OUTER_JOIN, f->"table_2.id = st7.id")
					
					.where("t1.id = 1")
				),
				getQueryUpdateJoins(false),
				getQueryUpdateJoins(true)
			},
			new Object[] {
				"Update With",
				f(
					b->b
					.with("cte", b.select("1 as id"))
					.with("cte2", b.select("1 as id"))
					.update("table_1", "t1")
					.set("name = :value").addParameter(":value", 123)
					.set(f->"typ = " + f.upper("'x'"))
					.join("cte", Join.INNER_JOIN, "cte.id = t1.id")
				),
				getQueryUpdateWith(false),
				getQueryUpdateWith(true)
			}
		};
	}
	
	protected abstract String getQueryUpdateBasic(boolean create);
	
	protected abstract boolean useQueryUpdateJoinsAlias();

	protected abstract String getQueryUpdateJoins(boolean create);
	
	protected abstract String getQueryUpdateWith(boolean create);

	@ParameterizedTest
	@MethodSource("dataQueryDelete")
	public void testQueryDelete(Function<QueryBuilder, DeleteBuilder> alter, String getSql, String createSql) throws Exception {
		test(alter, getSql, createSql, b->b.execute()); // VERIFY ?
	}
	
	public Object[] dataQueryDelete() {
		return new Object[] {
			new Object[] {
				f(
					b->b.delete("table_1")
					.where("id = :id")
					.where("id = :id", Where.OR)
					.where(f->"id = :id")
					.where(f->"id = :id", Where.OR)
					.addParameter(":id", 1)
				),
				getQueryDeleteBasic(false),
				getQueryDeleteBasic(true)
			},
			new Object[] {
				f(
					b->b.delete("table_1", "t1")
					.join("table_2", Join.INNER_JOIN, "t1.id = table_2.id")
					.join("table_3", "t3", Join.LEFT_OUTER_JOIN, "t1.id = t3.id")
					.join(b.select("*").from("table_4"), "st4", Join.RIGHT_OUTER_JOIN, "t3.id = st4.id")
					.join("table_5", Join.INNER_JOIN, f->"t1.id = table_5.id")
					.join("table_6", "t6", Join.LEFT_OUTER_JOIN, f->"t1.id = t6.id")
					.join(b.select("*").from("table_7"), "st7", Join.RIGHT_OUTER_JOIN, f->"t1.id = st7.id")
					
					.where("st7.id = 1")
				),
				getQueryDeleteJoins(false),
				getQueryDeleteJoins(true)
			},
			new Object[] {
				f(
					b->b
					.with("cte", b.select("1 as id"))
					.with("cte2", b.select("1 as id"))
					.delete("table_1", "t1")
					.join("cte", Join.INNER_JOIN, "cte.id = t1.id")
				),
				getQueryDeleteWith(false),
				getQueryDeleteWith(true)
			}
		};
	}

	protected abstract String getQueryDeleteBasic(boolean create);

	protected abstract String getQueryDeleteJoins(boolean create);

	protected abstract String getQueryDeleteWith(boolean create);

	@ParameterizedTest
	@MethodSource("dataQuerySelect")
	public void testQuerySelect(String message, Function<QueryBuilder, SelectBuilder> alter, String getSql, String createSql) throws Exception {
		test(alter, getSql, createSql, b->b.fetchAll()); // VERIFY?
	}
	
	public Object[] dataQuerySelect() {
		return new Object[] {
			new Object[] {
				"From string",
				f(b->b
					.select("id, name, typ")
					.from("table_1")
				),
				getQuerySelect_fromString(false),
				getQuerySelect_fromString(true)
			},
			new Object[] {
				"From string alias",
				f(b->b
					.select("id, name, typ")
					.from("table_1", "a")
				),
				getQuerySelect_fromStringAlias(false),
				getQuerySelect_fromStringAlias(true)
			},
			new Object[] {
				"From select",
				f(b->b
					.select("A")
					.from(
						b.select("1 AS A"),
						"a"
					)
				),
				getQuerySelect_fromSelect(false),
				getQuerySelect_fromSelect(true)
			},
			new Object[] {
				"Multi select",
				f(b->b
					.select("A")
					.from(
						b.multiSelect(b.select("1 AS A"))
						.union(b.select("2 AS A")),
						"a"
					)
				),
				getQuerySelect_fromMultiSelect(false),
				getQuerySelect_fromMultiSelect(true)
			},
			new Object[] {
				"With",
				f(b->b
					.with("cte", b.select("42 as a"))
					.with("cte2", b.select("42 as a"))
					.select("a")
					.from("cte")
				),
				getQuerySelect_with(false),
				getQuerySelect_with(true)
			},
			new Object[] {
				"With Recursive",
				// this is recursive
				f(b->b
					.with(
						"cte",
						b.multiSelect(b.select("1 AS A"))
						.union(b.select("2 AS A").from("cte"))
					)
					.with("cte2", b.select("42 as a"))
					.select("A")
					.from("cte")
					.limit(2)
				),
				getQuerySelect_withRecursive(false),
				getQuerySelect_withRecursive(true)
			},
			new Object[] {
				"Full",
				f(b->b
					.select("t1.id")
					.select("t1.name")
					.select(f->f.max("t1.id") + " as max_id")
					
					.from("table_1", "t1")
					
					.join("table_2", Join.INNER_JOIN, "t1.id = table_2.id")
					.join("table_3", "t3", Join.LEFT_OUTER_JOIN, "t1.id = t3.id")
					.join(b.select("*").from("table_4"), "st4", Join.RIGHT_OUTER_JOIN, "t3.id = st4.id")
					.join("table_5", Join.INNER_JOIN, f->"t1.id = table_5.id")
					.join("table_6", "t6", Join.LEFT_OUTER_JOIN, f->"t1.id = t6.id")
					.join(b.select("*").from("table_7"), "st7", Join.RIGHT_OUTER_JOIN, f->"t1.id = st7.id")
					
					.where("t1.id = 1")
					.where("t1.id != 1", Where.OR)
					.where(f->"t1.id = table_2.id")
					.where(f->"t1.id = t6.id", Where.OR)
					
					.groupBy("t1.id")
					.groupBy("t1.name")
					
					.having(":a != :b")
					.having(f->f.max("t1.id") + " < :max_id")
					
					.orderBy("t1.id")
					.orderBy(f->f.max("t1.id"))
					.limit(10, 15)
					.addParameter(":max_id", 10)
					.addParameter(":a", "AAAA")
					.addParameter(":b", "BBBB")
				),
				getQuerySelect(false),
				getQuerySelect(true)
			},
			new Object[] {
				"Limit",
				f(b->b
					.select("*")
					.from("table_1")
					.limit(10)
				),
				getQuerySelect_limit(false),
				getQuerySelect_limit(true)
			},
			new Object[] {
				"LimitAndOffset",
				f(b->b
					.select("*")
					.from("table_1")
					.limit(10, 15)
				),
				getQuerySelect_limitOffset(false),
				getQuerySelect_limitOffset(true)
			},
			new Object[] {
				"LimitAndOffsetOrderBy",
				f(b->b
					.select("*")
					.from("table_1")
					.orderBy("id")
					.limit(10, 15)
				),
				getQuerySelect_limitOffsetOrderBy(false),
				getQuerySelect_limitOffsetOrderBy(true)
			}
		};
	}

	protected abstract String getQuerySelect_fromString(boolean create);

	protected abstract String getQuerySelect_fromStringAlias(boolean create);

	protected abstract String getQuerySelect_fromSelect(boolean create);
	
	protected abstract String getQuerySelect_with(boolean create);
	
	protected abstract String getQuerySelect_withRecursive(boolean create);

	protected abstract String getQuerySelect_fromMultiSelect(boolean create);

	protected abstract String getQuerySelect(boolean create);

	protected abstract String getQuerySelect_limit(boolean create);

	protected abstract String getQuerySelect_limitOffset(boolean create);

	protected abstract String getQuerySelect_limitOffsetOrderBy(boolean create);

	@Test
	public void testQueryMultipleSelect() throws Exception{
		test(f(
			b->b
			.multiSelect(b.select(":id as id, name").from("table_5"))
			.union(b.select("id, name").from("table_5"))
			.intersect(b.select("id, name").from("table_5"))
			.unionAll(b.select("id, name").from("table_5"))
			.except(b.select("id, name").from("table_5"))
			.orderBy("name")
			.orderBy(f->"id")
			.addParameter(":id", 321)
		), getQueryMultipleSelect(false), getQueryMultipleSelect(true), b->b.fetchAll()); // VERIFY?
	}
	
	protected abstract String getQueryMultipleSelect(boolean create);
	
	@Test
	public void testCallProcedureReturning() throws Exception {
		test(f(
			b->b.call("procedure_int")
			.addInputParameter("some")
			.addOutputParameter("output1", String.class)
			.addInputParameter(123)
			.addOutputParameter("output2", Integer.class)
			.addInputParameter(false)
			.registerProcedureOutput()
		), getCallProcedureInt(), b->{
			ProcedureResult actual = b.execute();
			
			ProcedureResult expected = new ProcedureResult(1);
			expected.addOutput("output1", "something");
			expected.addOutput("output2", 42);
			try {
				assertEquals(expected, actual);
			} catch (Error e) {
				assertEquals(expected.toString(), actual.toString());
			}
		});
	}

	protected abstract String getCallProcedureInt();
	
	@Test
	public void testCallProcedureVoid() throws Exception {
		test(f(
			b->b.call("procedure_void")
			.addInputParameter("some")
			.addOutputParameter("output1", String.class)
			.addInputParameter(123)
			.addOutputParameter("output2", Integer.class)
			.addInputParameter(false)
		), getCallProcedureVoid(), b->{
			ProcedureResult actual = b.execute();
			
			ProcedureResult expected = new ProcedureResult(0);
			expected.addOutput("output1", "something");
			expected.addOutput("output2", 42);
			try {
				assertEquals(expected, actual);
			} catch (Error e) {
				assertEquals(expected.toString(), actual.toString());
			}
		});
	}

	protected abstract String getCallProcedureVoid();

	/********* OTHER **************/

	@Test
	public void testBatch() throws Exception {
		test(f(
			b->b
			.batch()
			.addBatch(b.update("table_1").set("name = ':x'").where("id > :id"))
			.addBatch(b.update("table_2").set("name = :x").where("id > :id").addParameter(":x", "NAME"))
			.addParameter(":id", 4)
		), getBatch(false), getBatch(true), b->b.execute()); // VERIFY ?
	}
	
	protected abstract String getBatch(boolean create);

	/*********************/
	
	private <B extends Builder> void test(
			Function<QueryBuilder, B> create, String expected, ThrowingConsumer<B, Exception> execute
		) throws Exception {
		test(create, expected, expected, execute);
	}
	
	@SuppressWarnings("CallToPrintStackTrace")
	private <B extends Builder> void test(
			Function<QueryBuilder, B> create,
			String expectedGet, String expectedCreate,
			ThrowingConsumer<B, Exception> execute
		) throws Exception {
		if (expectedGet != null && expectedGet.startsWith("ERROR: ")) {
			fail(expectedGet);
		}
		try (Connection connection = getConnection(connections)) {
			QueryBuilder queryBuilder = new QueryBuilder(instance, connection);
			B actual = create.apply(queryBuilder);
			
			if (expectedGet == null) {
				RuntimeException ex = assertThrows(RuntimeException.class, ()->{
					actual.getSql();
				});
				assertEquals("Not supported operation", ex.getMessage());
				return;
			}

			/*
			// test expected first, then syntax
			if (!expectedCreate.contains("?")) {
				connection.setAutoCommit(false);
				// check if expected SQL is correct
				try (Statement stmt = connection.createStatement()) {
					//for (String query : expectedCreate.split(";")) {
					//	stmt.execute(query);
					//}
					stmt.execute(expectedCreate);
					connection.rollback();
				} catch(SQLException e) {
					System.err.println();
					System.err.println("Incorrect expected SQL");
					System.err.println(expectedCreate);
					e.printStackTrace();
					connection.rollback();
					throw e;
				}
			}
			//*/
			assertSql(expectedGet, actual.getSql());
			assertSql(expectedCreate, actual.createSql());
	
			connection.setAutoCommit(false);
			execute.accept(actual);
			connection.rollback();
		}
	}

	private void assertSql(String expected, String actual) {
		assertEquals(
			expected.replace(";", "\n").replace(",", ",\n\t"),
			actual.replace(";", "\n").replace(",", ",\n\t")
		);
	}
	
	protected abstract Connection getConnection(Connections connections) throws SQLException;

	protected abstract Connection getBaseConnection(Connections connections) throws SQLException;

	protected abstract String getFilename();

	private static <B extends Builder> Function<QueryBuilder, B> f(Function<QueryBuilder, B> f) {
		return f;
	}

	@BeforeEach
	public void initDb() throws Exception {
		if (isInit) {
			return;
		}
		initEmptyDb(connections);
		this.isInit = true;
		String[] commands = Text.get().read(br->{
			return br.asString();
		}, "sql/" + getFilename() + "_dump.sql").split("\n-- separator --\n");
		try (Connection conn = getConnection(connections)) {
			for (String command : commands) {
				try (Statement stmt = conn.createStatement();) {
					stmt.execute(command);
				} catch (SQLException e) {
					throw new SQLException("INIT command: " + command, e);
				}
			}
		}
	}

	@SuppressWarnings("CallToPrintStackTrace")
	protected void initEmptyDb(Connections connections) throws SQLException {
		try (Connection conn = getBaseConnection(connections)) {
			try (Statement stmt = conn.createStatement()) {
				stmt.execute("DROP DATABASE " + Connections.QUERY_BUILDER_DATABASE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try (Statement stmt = conn.createStatement();) {
				stmt.execute("CREATE DATABASE " + Connections.QUERY_BUILDER_DATABASE);
			}
		}
	}
	
}
