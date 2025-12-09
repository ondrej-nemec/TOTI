package ji.querybuilder.builder_impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import ji.common.structures.SortedMap;
import ji.querybuilder.DbInstance;
import ji.querybuilder.Escape;
import ji.querybuilder.builders.CallProcedureBuilder;
import ji.querybuilder.structures.ProcedureResult;

public class CallProcedureBuilderImpl implements CallProcedureBuilder {
	
	private final DbInstance instance;
	private final Connection connection;
	private final String procedure;
	
	private final List<String> parameters;
	private final SortedMap<String, Class<?>> outputs;
	
	public CallProcedureBuilderImpl(Connection connection, DbInstance instance, String procedure) {
		this.connection = connection;
		this.instance = instance;
		this.procedure = procedure;
		this.parameters = new LinkedList<>();
		this.outputs = new SortedMap<>();
	}

	public String getProcedure() {
		return procedure;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	@Override
	public String getSql() {
		return instance.createSql(this, false);
	//	return createSql();
	}
	
	@Override
	public String createSql() {
		return instance.createSql(this, true);
		// return "{? = call " + procedure + "(" + Implode.implode(", ", this.parameters) + ")}";
	}

	@Override
	public CallProcedureBuilder addInputParameter(Object value) {
		this.parameters.add(instance.getEscape().escape(value));
		return this;
	}

	@Override
	public CallProcedureBuilder addOutputParameter(String name, Class<?> type) {
		this.parameters.add("?");
		this.outputs.put(name,type);
		return this;
	}

	@Override
	public ProcedureResult execute() throws SQLException {
		try (CallableStatement stmt = connection.prepareCall(createSql())) {
			// call result
            stmt.registerOutParameter(1, getType(null));
            outputs.forEach((index, parameterName, clazz)->{
            	stmt.registerOutParameter(index + 2, getType(clazz));
            });
            stmt.execute();
            
            ProcedureResult result = new ProcedureResult(stmt.getObject(1));
            outputs.forEach((index, parameterName, clazz)->{
	           	 result.addOutput(parameterName, Escape.parseValue(stmt, index + 2));
	        });
            return result;
		}
	}
	
	private int getType(Class<?> clazz) {
		if (clazz == null) {
			return Types.INTEGER;
		}
		if (clazz.isAssignableFrom(Integer.class)) {
			return Types.INTEGER;
		}
		if (clazz.isAssignableFrom(Double.class)) {
			return Types.DOUBLE;
		}
		if (clazz.isAssignableFrom(Float.class)) {
			return Types.FLOAT;
		}
		if (clazz.isAssignableFrom(Boolean.class)) {
			return Types.BOOLEAN;
		}
		if (clazz.isAssignableFrom(String.class)) {
			return Types.VARCHAR;
		}
		if (clazz.isAssignableFrom(LocalDate.class)) {
			return Types.DATE;
		}
		if (clazz.isAssignableFrom(LocalTime.class)) {
			return Types.TIME;
		}
		if (clazz.isAssignableFrom(LocalDateTime.class)) {
			return Types.DATE;
		}
		return Types.INTEGER;
	}
	
}
