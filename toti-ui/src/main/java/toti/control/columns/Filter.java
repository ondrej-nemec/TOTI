package toti.control.columns;

import java.util.Map;

public interface Filter {

	String getType();
	
	Map<String, Object> getFilterSettings();
	/*
	F addParam(String name, String value);
	
	F setDefaultValue(Object value);
	
	F setPlaceholder(String placeholder);
	*/
}
