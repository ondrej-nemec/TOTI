package ji.testing.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Row {

	private final Map<String, Object> columns;
	private final String idName;
	private final Object idValue;
	
	public static Row insert() {
		return new Row(null, null);
	}
	
	public static Row insert(String name) {
		return new Row(name, null);
	}
	
	public static Row update(String name, Object value) {
		if (value == null) {
			throw new RuntimeException("Update Row cannot have null value");
		}
		if (name == null) {
			throw new RuntimeException("Update Row cannot have null name");
		}
		return new Row(name, value);
	}
	
	private Row(String idName, Object value) {
		this.columns = new HashMap<>();
		this.idName = idName;
		this.idValue = value;
	}
	
	public Row addColumn(String columnName, Object value) {
		columns.put(columnName, value);
		return this;
	}
	
	public Map<String, Object> getColumns() {
		return columns;
	}
	
	public boolean isInsert() {
		return idValue == null;
	}

	public Optional<String> getIdName() {
		if (idName == null) {
			return Optional.empty();
		}
		return Optional.of(idName);
	}

	public Object getIdValue() {
		return idValue;
	}
	
	public Row clone() {
		Row row = new Row(idName, idValue);
		row.columns.putAll(columns);
		return row;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		if (isInsert()) {
			string.append("INSERT:");
		} else {
			string.append(String.format("UPDATE %s[%s]:", idName, idValue));
		}
		string.append(" ");
		string.append(columns.toString());
		string.append(" (" + super.toString() + ")");
		return string.toString();
	}
	
}