package toti.lib.database.querybuilder;

import toti.lib.database.querybuilder.enums.ColumnType;

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

	String coalesce(String param1, String param2, String ...params);
	
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

	/*
	CHAR_LENGTH(text)
CHARACTER_LENGTH(text)
POSITION(podretezec IN text)
SUBSTRING(text FROM start FOR delka)
ABS(x)
MOD(a, b)
POWER(a, b)
SQRT(x)
FLOOR(x)
CEILING(x)
ROUND(x)

CURRENT_DATE
CURRENT_TIME
CURRENT_TIMESTAMP
LOCALTIME
LOCALTIMESTAMP
=> Naopak funkce typu DATEADD(), DATEDIFF(), NOW() nebo GETDATE() =>> now
 */
	
}
