package ji.querybuilder.builders;

import java.sql.SQLException;
import java.util.List;

import ji.querybuilder.Builder;
import ji.querybuilder.structures.SubSelect;
import toti.lib.common.structures.DictionaryValue;

public interface InsertBuilder extends Builder {
	
	// TODO insert more values
	
	InsertBuilder addValue(String columnName, Object value);
	
	InsertBuilder fromSelect(List<String> columns, SubSelect select);
	
	DictionaryValue execute() throws SQLException;

}
