package ji.querybuilder.structures;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ji.querybuilder.enums.ColumnSetting;
import ji.querybuilder.enums.ColumnType;

public class Column {

	private final String name;
	private final ColumnType type;
	private final Object value;
	private final List<ColumnSetting> settings;
	
	public Column(String name, ColumnType type, Object value, ColumnSetting[] settings) {
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
