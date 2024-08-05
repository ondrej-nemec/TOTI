package ji.querybuilder.structures;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ji.querybuilder.Escape;
import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;

public class Column {

	private final String name;
	private final ColumnType type;
	private final Object value;
	private final List<ColumnSetting> settings;
	
	public static Column create(String name, ColumnType type, Object value, ColumnSetting[] settings) {
		return new Column(name, type, value == null ? null : Escape.escape(value), settings);
	}
	
	public static Column rename(String oldName, String newName, ColumnType type) {
		return new Column(oldName, type, newName, null);
	}
	
	public static Column modify(String name, ColumnType type) {
		return new Column(name, type, null, null);
	}

	public static Column delete(String name) {
		return new Column(name, null, null, null);
	}

	private Column(String name, ColumnType type, Object value, ColumnSetting[] settings) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.settings = settings == null ? new LinkedList<>() : Arrays.asList(settings);
	}

	public String getName() {
		return name;
	}

	public ColumnType getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public List<ColumnSetting> getSettings() {
		return settings;
	}
	
	public String getOldName() {
		return name;
	}
	
	public String getNewName() {
		return value.toString();
	}

}
