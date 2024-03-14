package toti.ui.backend;

import java.util.Collection;

public class Owner {
	
	private final String columnName;
	private final Collection<Object> allowedValues;
	
	public Owner(String columnName, Collection<Object> allowedValues) {
		this.columnName = columnName;
		this.allowedValues = allowedValues;
	}

	public String getColumnName() {
		return columnName;
	}

	public Collection<Object> getAllowedValues() {
		return allowedValues;
	}

	
}
