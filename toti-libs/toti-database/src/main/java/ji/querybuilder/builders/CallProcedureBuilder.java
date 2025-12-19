package ji.querybuilder.builders;

import java.sql.SQLException;

import ji.querybuilder.Builder;
import ji.querybuilder.structures.ProcedureResult;

public interface CallProcedureBuilder extends Builder {
	
	CallProcedureBuilder addInputParameter(Object value);
	
	CallProcedureBuilder addOutputParameter(String name, Class<?> type);

	CallProcedureBuilder registerProcedureOutput();
	
	ProcedureResult execute() throws SQLException;

}
