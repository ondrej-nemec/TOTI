package ji.testing;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ji.database.DatabaseConfig;
import ji.testing.entities.Row;
import ji.testing.entities.Table;

// for doc
public class Example extends DatabaseTestCase {

	public Example() {
		super(new DatabaseConfig(
			"", 
			"", 
			true,
			"",
			"",
			"",
			Arrays.asList(),
			1
		));
	}

	@Override
	protected List<Table> getDataSet() {
		return Arrays.asList(
			new Table("Table1")
			.addRow(
				Row.insert()
				.addColumn("id", 1)
				.addColumn("name", "Name #1")
			)
			.addRow(
				Row.insert()
				.addColumn("id", 2)
				.addColumn("name", "Name #2")
			),
			new Table("Table2")
			.addRow(
				Row.insert()
				.addColumn("id", 1)
				.addColumn("value", "Value #1")
			)
			.addRow(
				Row.insert()
				.addColumn("id", 2)
				.addColumn("value", "Value #2")
			)
		);
	}
	
	@Test
	public void test1() {
		// ...
	}
	
	@Test
	public void test2() throws SQLException {
		alterDataSet(Arrays.asList(
			new Table("Table1")
			.addRow(
				Row.insert() // add one more row to Table1
				.addColumn("id", 3)
				.addColumn("name", "Name #3")
			),
			new Table("Table2")
			.addRow(
				Row.update("id", 2) // update row with id=2
				.addColumn("value", "another value")
			)
		));
		// ...
	}

}
