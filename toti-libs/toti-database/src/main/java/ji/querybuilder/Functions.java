package ji.querybuilder;

import ji.querybuilder.enums.ColumnType;

public interface Functions {

	/**
	 * Param(s) must be column name or escaped value - you can use parameters
	 * @return
	 */
	String concat(String param1, String param2, String ...params);
	
	default String groupConcat(String param, String delimeter) {
		return groupConcat(param, delimeter, null);
	}
	
	String groupConcat(String param, String delimeter, String orderBy);
	
	/**
	 * Param(s) must be column name or escaped value - you can use parameters
	 * @return
	 */
	String cast(String param, ColumnType type);
	
	String max(String param);
	
	String min(String param);
	
	String avg(String param);
	
	String sum(String param);
	
	String count(String param);
	
	String lower(String param);
	
	String upper(String param);
	
	String trim(String param);
	
}
