package ji.testing.entities;

import java.util.LinkedList;
import java.util.List;

public class Table {

	private final String name;
	
	private final List<Row> rows;
	
	public Table(final String name, final List<Row> rows) {
		this.name = name;
		this.rows = rows;
	}
	
	public Table(final String name) {
		this(name, new LinkedList<>());
	}

	public Table addRow(Row row) {
		rows.add(row);
		return this;
	}

	public Table addAllRow(List<Row> rows) {
		this.rows.addAll(rows);
		return this;
	}
	
	public String getName() {
		return name;
	}

	public List<Row> getRows() {
		return rows;
	}
	
	public Table clone() {
		List<Row> rows = new LinkedList<>();
		this.rows.forEach((row)->rows.add(row.clone()));
		return new Table(name, rows);
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder(
			String.format("Table: %s - %s rows (%s)", name,	
			rows.size(), super.toString())
		);
		rows.forEach((r)->{
			string.append("\n\t" + r);
		});
		return string.toString();
	}
	
}
