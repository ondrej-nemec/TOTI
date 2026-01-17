package toti.lib.database.querybuilder.builders;

import java.sql.SQLException;

import toti.lib.database.querybuilder.Builder;
import toti.lib.database.querybuilder.structures.ProcedureResult;

public interface CallProcedureBuilder extends Builder {
	
	CallProcedureBuilder addInputParameter(Object value);
	
	CallProcedureBuilder addOutputParameter(String name, Class<?> type);

	CallProcedureBuilder registerProcedureOutput();
	
	ProcedureResult execute() throws SQLException;

}
