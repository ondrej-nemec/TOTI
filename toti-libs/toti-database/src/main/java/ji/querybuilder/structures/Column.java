package ji.querybuilder.structures;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;

public class Column {

	private final String name;
	private final ColumnType type;
	private final DefaultValue value;
	private final List<ColumnSetting> settings;
	
	public static Column create(String name, ColumnType type, Object value, ColumnSetting[] settings) {
		return new Column(name, type, DefaultValue.set(value), settings);
	}

	public static Column create(String name, ColumnType type, ColumnSetting[] settings) {
		return new Column(name, type, DefaultValue.notUse(), settings);
	}
	
	public static Column rename(String oldName, String newName, ColumnType type) {
		return new Column(oldName, type, DefaultValue.set(newName), new LinkedList<>());
	}
	
	public static Column modify(String name) {
		return new Column(name, null, DefaultValue.notUse(), new LinkedList<>());
	}

	public static Column delete(String name) {
		return new Column(name, null, DefaultValue.notUse(), new LinkedList<>());
	}

	private Column(String name, ColumnType type, DefaultValue value, ColumnSetting[] settings) {
		this(name, type, value, settings == null ? new LinkedList<>() : Arrays.asList(settings));
	}

	private Column(String name, ColumnType type, DefaultValue value, List<ColumnSetting> settings) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.settings = settings;
	}

	public String getName() {
		return name;
	}

	public ColumnType getType() {
		return type;
	}

	public DefaultValue getValue() {
		return value;
	}

	public List<ColumnSetting> getSettings() {
		return settings;
	}
	
	public String getOldName() {
		return name;
	}
	
	public String getNewName() {
		return value.get().toString();
	}
	
	public Column withType(ColumnType type) {
		return new Column(name, type, value, settings);
	}
	
	public Column withValue(Object value) {
		return new Column(name, type, DefaultValue.set(value), settings);
	}
	
	public Column removeValue() {
		return new Column(name, type, DefaultValue.clear(), settings);
	}

	@Override
	public String toString() {
		return "Column [name=" + name + ", type=" + type + ", value=" + value + ", settings=" + settings + "]";
	}

}
