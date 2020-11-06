package toti.control.columns;

import java.util.Map;

public interface Filter {

	String getType();
	
	Map<String, Object> getFilterSettings();
	
}
