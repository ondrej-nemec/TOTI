package toti.profiler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.common.annotations.MapperIgnored;
import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;
import ji.json.Jsonable;

public class SqlLog implements Jsonable {

	// maybe add date of sqlLog create and order by this date, use in profiler
	
	@MapperIgnored
	private final String id;

	private String sql;
	
	private List<Object> params = new LinkedList<>();
	
	private boolean isExecuted;
	
	private String preparedSql;
	private String replacedSql;
	private Map<String, String> builderParams;
	
	private Object result;

	@MapperIgnored
	private long startTime;

	@MapperParameter({@MapperType("executionTime")})
	private long executionTime = -1;
	
	public SqlLog(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public Object getResuslt() {
		return result;
	}

	public String getSql() {
		return sql;
	}

	public List<Object> getParams() {
		return params;
	}

	public String getPreparedSql() {
		return preparedSql;
	}

	public String getReplacedSql() {
		return replacedSql;
	}

	public Map<String, String> getBuilderParams() {
		return builderParams;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void addParams(Object param) {
		this.params.add(param);
	}

	public void toExecute() {
		this.startTime = System.nanoTime();
	}

	public void setExecuted(Object result) {
		this.isExecuted = true;
		this.executionTime = System.nanoTime() - startTime;
		this.result = result;
	}

	public void setBuilder(String preparedSql, String replacedSql, Map<String, String> builderParams) {
		this.preparedSql = preparedSql;
		this.replacedSql = replacedSql;
		this.builderParams = builderParams;
	}
	
}
