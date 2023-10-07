package toti.profiler;

import java.util.Map;

import ji.database.support.SqlQueryProfiler;
import ji.translator.TransProfiler;
import ji.json.Jsonable;
import ji.socketCommunication.http.profiler.HttpServerProfiler;
import ji.socketCommunication.http.profiler.HttpServerProfilerEvent;
import toti.templating.TemplateProfiler;

public class Profiler implements TransProfiler, HttpServerProfiler, SqlQueryProfiler, TemplateProfiler, Jsonable {

	@Override
	public void execute(String identifier, String sql) {}

	@Override
	public void execute(String identifier) {}

	@Override
	public void executed(String identifier, Object res) {}
	
	@Override
	public void prepare(String identifier, String sql) {}

	@Override
	public void addParam(String identifier, Object param) {}

	@Override
	public void builderQuery(String identifier, String query, String sql, Map<String, String> params) {}

	@Override
	public void missingParameter(String module, String key, Map<String, Object> variables, String locale) {}

	@Override
	public void missingLocale(String locale) {}
	
	@Override
	public void loadFile(String locale, String domain, String path, boolean success) {}

	@Override
	public void logGetTemplate(
			String module, String namespace, String filename, Map<String, Object> variables, int parent, int self) {}
	
	@Override
	public void log(Map<HttpServerProfilerEvent, Long> events) {}

	public boolean isUse() {
		// TODO Auto-generated method stub
		return false;
	}

	public Profiler used() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUse(Boolean useProfiler) {
		// TODO Auto-generated method stub
		
	}
	
}
