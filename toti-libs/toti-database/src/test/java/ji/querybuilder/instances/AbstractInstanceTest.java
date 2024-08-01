package ji.querybuilder.instances;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.functions.Terminal;
import ji.common.structures.ThrowingConsumer;
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
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public abstract class AbstractInstanceTest {
	
	private final static Terminal TERMINAL = new Terminal(mock(Logger.class));
	private static boolean USE_REAL_DB = true;
	
	private final DbInstance instance;
	
	public AbstractInstanceTest(DbInstance instance) {
		this.instance = instance;
	}
	
	@BeforeClass
	public static void beforeClass() {
		int i = TERMINAL.runCommand(out->System.out.println(out), err->System.err.println(err), "docker-compose up -d");
		USE_REAL_DB = i == 0;
	}
	
	@AfterClass
	public static void afterClass() {
		if (USE_REAL_DB) {
			TERMINAL.runCommand(out->System.out.println(out), err->System.err.println(err), "docker-compose stop");
		}
	}
	
	// TODO improve tests - more for real db
		// all enums will be tested during queries
	
	@Test
	@Parameters(method="dataFunctions")
	public void testFunctions(Function<QueryBuilder, SelectBuilder> create, String expected) throws Exception {
		test(create, expected, expected, b->b.fetchAll());
	}
	
	public Object[] dataFunctions() {
		return new Object[] {
			new Object[] {
				f(b->b.select(f->f.concat("'", "name", "'")).from("table_for_functions")), getFunctions_concat()
			},
			new Object[] {
				f(b->b.select(f->f.groupConcat("name", ",")).from("table_for_functions").groupBy("name")), getFunctions_groupConcat()
			},
			new Object[] {
				f(b->b.select(f->f.cast("id", ColumnType.floatType())).from("table_for_functions")), getFunctions_cast()
			},
			new Object[] {
				f(b->b.select(f->f.max("id")).from("table_for_functions")), getFunctions_max()
			},
			new Object[] {
				f(b->b.select(f->f.min("id")).from("table_for_functions")), getFunctions_min()
			},
			new Object[] {
				f(b->b.select(f->f.avg("id")).from("table_for_functions")), getFunctions_avg()
			},
			new Object[] {
				f(b->b.select(f->f.sum("id")).from("table_for_functions")), getFunctions_sum()
			},
			new Object[] {
				f(b->b.select(f->f.count("id")).from("table_for_functions")), getFunctions_count()
			},
			new Object[] {
				f(b->b.select(f->f.lower("name")).from("table_for_functions")), getFunctions_lower()
			},
			new Object[] {
				f(b->b.select(f->f.upper("name")).from("table_for_functions")), getFunctions_upper()
			}
		};
	}
	
	protected abstract String getFunctions_concat();

	protected abstract String getFunctions_groupConcat();

	protected abstract String getFunctions_cast();

	protected abstract String getFunctions_max();

	protected abstract String getFunctions_min();

	protected abstract String getFunctions_avg();

	protected abstract String getFunctions_sum();

	protected abstract String getFunctions_count();

	protected abstract String getFunctions_lower();

	protected abstract String getFunctions_upper();

	/********* TABLE **************/
	
	@Test
	@Parameters(method="dataCreateTable")
	public void testCreateTable(Function<QueryBuilder, CreateTableBuilder> create, String getSql) throws Exception {
		test(create, getSql, b->b.execute()); // VERIFY ?
	}
	
	public Object[] dataCreateTable() {
		return new Object[] {
			new Object[] {
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
					.addColumn("DateTime_column", ColumnType.datetime())

					.addColumn("FK_column_1", ColumnType.integer())
					.addColumn("FK_column_2", ColumnType.integer())
					.addColumn("FK_column_3", ColumnType.integer())
					.addColumn("FK_column_4", ColumnType.integer())
					
					.addForeignKey("FK_column_1", "table_for_index", "id")
					.addForeignKey("FK_column_2", "table_for_index", "id", OnAction.CASCADE, OnAction.NO_ACTION)
					.addForeignKey("FK_column_3", "table_for_index", "id", OnAction.RESTRICT, OnAction.SET_DEFAULT)
					.addForeignKey("FK_column_4", "table_for_index", "id", OnAction.SET_NULL, null)
				),
				getCreateTable()
			},
			new Object[] {
				f(b->b.createTable("create_table")
					.addColumn("Primary_column_1", ColumnType.integer())
					.addColumn("Primary_column_2", ColumnType.integer())
					.addColumn("Primary_column_3", ColumnType.integer())
					.setPrimaryKey("Primary_column_1", "Primary_column_2", "Primary_column_3")
					.addForeignKey("Primary_column_3", "table_for_index", "id")
				),
				getCreateTableWithPrimary()
			}
		};
	}

	protected abstract String getCreateTable();

	protected abstract String getCreateTableWithPrimary();
	
	@Test
	@Parameters(method="dataAlterTable")
	public void testAlterTable(Function<QueryBuilder, AlterTableBuilder> create, String getSql) throws Exception {
		test(create, getSql, b->b.execute()); // VERIFY ?
		
	}
	
	public Object[] dataAlterTable() {
		return new Object[] {
			new Object[] {
				f(
					b->b.alterTable("table_to_alter")
					.addColumn("Add_column_1", ColumnType.integer(), ColumnSetting.NOT_NULL)
					.addColumn("Add_column_2", ColumnType.integer(), 42, ColumnSetting.UNIQUE, ColumnSetting.NULL)
					.addForeignKey("Add_column_1", "table_for_index", "id")
					.addForeignKey("Add_column_2", "table_for_index", "id", OnAction.CASCADE, OnAction.NO_ACTION)
					.deleteColumn("Column_to_delete")
					.deleteForeingKey("FK_to_delete")
					.modifyColumnType("Column_to_modify_type", ColumnType.floatType())
				),
				getAlterTable()	
			},
			new Object[] {
				// postgres allow only one rename and nothing more
				f(
					b->b.alterTable("table_to_alter")
					.renameColumn("Column_to_rename", "Renamed_column", ColumnType.integer())
				),
				getAlterTableRename()	
			},
			// full query with all options - not supported by all dbs
			/*new Object[] {
				f(
					b->b.alterTable("table_to_alter")
					.addColumn("Add_column_1", ColumnType.integer(), ColumnSetting.NOT_NULL)
					.addColumn("Add_column_2", ColumnType.integer(), 42, ColumnSetting.UNIQUE, ColumnSetting.NULL)
					.addForeignKey("Add_column_1", "table_for_index", "id")
					.addForeignKey("Add_column_2", "table_for_index", "id", OnAction.CASCADE, OnAction.NO_ACTION)
					.deleteColumn("Column_to_delete")
					.deleteForeingKey("FK_to_delete")
					.modifyColumnType("Column_to_modify_type", ColumnType.floatType())
					.renameColumn("Column_to_rename", "Renamed_column", ColumnType.integer())
				),
				getAlterTable()	
			}*/
		};
	}
	
	protected abstract String getAlterTable();
	
	protected abstract String getAlterTableRename();

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

	@Test
	@Parameters(method="dataCreateView")
	public void testCreateView(
			Function<QueryBuilder, CreateViewBuilder> create, String getSql, String createSql
		) throws Exception {
		test(create, getSql, createSql, b->b.execute()); // VERIFY ?
	}
	
	public Object[] dataCreateView() {
		return new Object[] {
			new Object[] {
				f(b->b
					.createView("some_view")
					.select("1 as A")
				),
				getCreateView_fromString(false),
				getCreateView_fromString(true)
			},
			new Object[] {
				f(b->b
					.createView("some_view")
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
					.createView("some_view")
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
					.createView("some_view")

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

	@Test
	@Parameters(method="dataAlterView")
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
		test(b->b.createIndex("index_name", "table_for_index", "id", "name"), getCreateIndex(), b->b.execute()); // VERIFY ?
	}
	
	protected abstract String getCreateIndex();

	@Test
	public void testDeleteIndex() throws Exception {
		test(b->b.deleteIndex("index_to_delete"), getDeleteIndex(), b->b.execute()); // VERIFY ?
	}
	
	protected abstract String getDeleteIndex();
	
	/********* QUERING **************/

	@Test
	@Parameters(method="dataQueryInsert")
	public void testQueryInsert(Function<QueryBuilder, InsertBuilder> alter, String getSql, String createSql) throws Exception {
		test(alter, getSql, createSql, b->b.execute()); // VERIFY ?
	}
	
	public Object[] dataQueryInsert() {
		return new Object[] {
			new Object[] {
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
				f(
					b->b
					.with("cte", b.select("id, name").from("table_2").where("id = 2"))
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
			}
		};
	}
	
	protected abstract String getQueryInsert();
	
	protected abstract String getQueryInsertFromSelect(boolean create);

	@Test
	@Parameters(method="dataQueryUpdate")
	public void testQueryUpdate(Function<QueryBuilder, UpdateBuilder> alter, String getSql, String createSql) throws Exception {
		test(alter, getSql, createSql, b->b.execute()); // VERIFY ?
	}
	
	public Object[] dataQueryUpdate() {
		return new Object[] {
			new Object[] {
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
				f(
					b->b.update("table_1", "t1")
					.set("name = :value").addParameter(":value", 123)
					.set(f->"typ = " + f.upper("'x'"))

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
				f(
					b->b
					.with("cte", b.select("1 as id"))
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

	protected abstract String getQueryUpdateJoins(boolean create);
	
	protected abstract String getQueryUpdateWith(boolean create);

	@Test
	@Parameters(method="dataQueryDelete")
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

	@Test
	@Parameters(method="dataQuerySelect")
	public void testQuerySelect(Function<QueryBuilder, SelectBuilder> alter, String getSql, String createSql) throws Exception {
		test(alter, getSql, createSql, b->b.fetchAll()); // VERIFY?
	}
	
	public Object[] dataQuerySelect() {
		return new Object[] {
			new Object[] {
				f(b->b
					.select("id, name, typ")
					.from("table_1")
				),
				getQuerySelect_fromString(false),
				getQuerySelect_fromString(true)
			},
			new Object[] {
				f(b->b
					.select("id, name, typ")
					.from("table_1", "a")
				),
				getQuerySelect_fromStringAlias(false),
				getQuerySelect_fromStringAlias(true)
			},
			new Object[] {
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
				f(b->b
					.with("cte", b.select("42 as a"))
					.select("a")
					.from("cte")
				),
				getQuerySelect_with(false),
				getQuerySelect_with(true)
			},
			new Object[] {
				// this is recursive
				f(b->b
					.with(
						"cte",
						b.multiSelect(b.select("1 AS A"))
						.union(b.select("2 AS A").from("cte"))
					)
					.select("A")
					.from("cte")
				),
				getQuerySelect_withRecursive(false),
				getQuerySelect_withRecursive(true)
			},
			new Object[] {
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

	/********* OTHER **************/

	@Test
	public void testBatch() throws Exception {
		test(f(
			b->b
			.batch()
			.addBatch(b.select(":id, ':x' as a"))
			.addBatch(b.select(":id, ':x' as b").addParameter(":x", 1))
			.addParameter(":id", 123)
		), getBatch(false), getBatch(true), b->b.execute()); // VERIFY ?
	}
	
	protected abstract String getBatch(boolean create);

	/*********************/
	
	private <B extends Builder> void test(
			Function<QueryBuilder, B> create, String expected, ThrowingConsumer<B, Exception> execute
		) throws Exception {
		test(create, expected, expected, execute);
	}
	
	private <B extends Builder> void test(
			Function<QueryBuilder, B> create,
			String expectedGet, String expectedCreate,
			ThrowingConsumer<B, Exception> execute
		) throws Exception {
		try (Connection connection = (USE_REAL_DB ? getConnection() : mock(Connection.class))) {
			QueryBuilder queryBuilder = new QueryBuilder(instance, connection);
			B actual = create.apply(queryBuilder);
			assertEquals(expectedGet, actual.getSql());
			assertEquals(expectedCreate, actual.createSql());
			
			if (USE_REAL_DB) {
				connection.setAutoCommit(false);
				execute.accept(actual);
				connection.rollback();
			} else {
				fail("Read db was not tested");
			}
		}
	}
	
	protected abstract Connection getConnection() throws SQLException;

	private <B extends Builder> Function<QueryBuilder, B> f(Function<QueryBuilder, B> f) {
		return f;
	}
	
}
