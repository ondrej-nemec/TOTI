package ji.querybuilder;

import java.sql.Connection;
import java.util.Arrays;

import org.mockito.Mockito;

import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;
import ji.querybuilder.enums.Join;
import ji.querybuilder.enums.Where;
import ji.querybuilder.instances.MySqlQueryBuilder;

@Deprecated
public class QueryBuilderExamples {

	public static void main(String[] args) {
		QueryBuilder builder = new QueryBuilder(new MySqlQueryBuilder(), Mockito.mock(Connection.class));
		printTest(
			"SELECT",
			builder.select("*")
			.from("Table1 a")
			.join("Table2 b", Join.INNER_JOIN, "a.id = b.a_id")
			.join(
				builder.select("max(id) as max_id, name").from("Table1").groupBy("name"),
				"c", // name of subselect
				Join.LEFT_OUTER_JOIN,
				"a.id = c.max_id"
			)
			.where("a.id < :id")
			.where("b.id < :id")
			.where("b.number in (:in)")
			.where("c.name != :name", Where.OR)
			.orderBy("a.id")
			.limit(10, 5)
			.addParameter(":id", 18)
			.addParameter(":name", "admin")
			.addParameter(":in", Arrays.asList(1,2,3,4))
		);
		printTest(
			"UPDATE",
			builder.update("Table1")
			.set("name = :name")
			.where("id = :id")
			.addParameter(":id", 18)
			.addParameter(":name", "user18")
		);
		printTest(
			"INSERT",
			builder.insert("Table2")
			.addValue("id", 123)
			.addValue("number", 12.8)
			.addValue("name", "Item 8")
		);
		printTest(
			"DELETE",
			builder.delete("Table1")
			.where("id = :id")
			.addParameter(":id", 1)
		);
		printTest(
			"CREATE TABLE",
			builder.createTable("Table3")
			.addColumn("id", ColumnType.integer(), ColumnSetting.PRIMARY_KEY, ColumnSetting.AUTO_INCREMENT)
			.addColumn("name", ColumnType.string(10)) // no addition settings
			.addColumn("is_used", ColumnType.bool(), true, ColumnSetting.NOT_NULL) // defaut value is 'true'
		);
		printTest(
			"ALTER TABLE",
			builder.alterTable("Table3")
			.addColumn("cost", ColumnType.integer())
		);
		printTest(
			"ALTER TABLE",
			builder.alterTable("Table3")
			.renameColumn("is_used", "used", ColumnType.bool())
		);
		printTest(
			"DELETE TABLE",
			builder.deleteTable("Table3")
		);
		printTest(
			"CREATE VIEW 1",
			builder.createView("table1_table2_view")
			.select("*")
			.from("Table1 a")
			.join("Table2 b", Join.INNER_JOIN, "a.id = b.a_id")
			.join(
				builder.select("max(id) as max_id, name").from("Table1").groupBy("name"),
				"c", // name of subselect
				Join.LEFT_OUTER_JOIN,
				"a.id = c.max_id"
			)
			.where("a.id < :id")
			.where("b.id < :id")
			.where("b.number in (:in)")
			.where("c.name != :name", Where.OR)
			.orderBy("a.id")
			.limit(10, 5)
			.addParameter(":id", 18)
			.addParameter(":name", "admin")
			.addParameter(":in", Arrays.asList(1,2,3,4))
		);
		/*printTest( // TODO
				"CREATE VIEW 2",
				builder
			);*/
		printTest(
			"ALTER VIEW",
			builder.alterView("table1_table2_view")
			.select("*")
			.from("Table1 a")
			.join("Table2 b", Join.INNER_JOIN, "a.id = b.a_id")
			.where("a.id < :id")
			.where("b.id < :id")
			.where("b.number in (:in)")
			.where("c.name != :name", Where.OR)
			.orderBy("a.id")
			.limit(10, 5)
			.addParameter(":id", 18)
			.addParameter(":name", "admin")
			.addParameter(":in", Arrays.asList(1,2,3,4))
		);
		printTest(
			"DELETE VIEW",
			builder.deleteView("table1_table2_view")
		);
		printTest(
			"MULTY SELECT",
			builder.multiSelect(
				builder.select("*")
				.from("Table1")
				.where("name != :name")
				.where("id > :id")
				.addParameter(":name", "Table1-name")
			)
			.union(
				builder.select("*")
				.from("Table2")
				.where("name != :name")
				.where("id > :id")
				.addParameter(":name", "Table2-name")
			)
			.addParameter(":id", 42)
			.orderBy("id")
		);
		printTest(
			"BATCH",
			builder.batch()
			.addBatch(
				builder.update("Table1")
				.set("name = :name")
				.where("id = :id")
				.addParameter(":name", "Table1-Name")
			)
			.addBatch(
				builder.update("Table2")
				.set("name = :name")
				.where("id = :id")
				.addParameter(":name", "Table2-Name")
			)
			.addParameter(":id", 42)
		);
	}
	
	private static void printTest(String name, Builder builder) {
		System.out.println("Test: " + name);
		System.out.println("<strong>Example</strong>");
		System.out.println();
		System.out.println("<pre><code class=\"language-java\">");
		System.out.println();
		System.out.println("</code></pre>");
		System.out.println();
		System.out.println("<p>SQL without translated parameters:</p>");
		System.out.println("<pre><code class=\"language-sql\">");
		System.out.println(builder.getSql());
		System.out.println("</code></pre>");
		System.out.println("<p>SQL with translated parameters</p>");
		System.out.println("<pre><code class=\"language-sql\">");
		System.out.println(builder.createSql());
		System.out.println("</code></pre>");
		System.out.println();
	}
	
}
