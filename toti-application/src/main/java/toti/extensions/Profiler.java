package toti.extensions;

import java.util.Map;

import ji.common.functions.Env;
import ji.database.support.SqlQueryProfiler;
import ji.json.Jsonable;
import ji.socketCommunication.http.profiler.HttpServerProfiler;
import ji.socketCommunication.http.profiler.HttpServerProfilerEvent;
import ji.translator.TransProfiler;
import toti.application.register.Register;
import toti.templating.TemplateProfiler;

@Deprecated
public interface Profiler extends Extension, TransProfiler, HttpServerProfiler, SqlQueryProfiler, TemplateProfiler, Jsonable {

	static Profiler empty() {
		return new Profiler() {
			@Override public void logGetTemplate(String module, String namespace, String filename, Map<String, Object> variables, int parent, int self) {}
			@Override public void prepare(String identifier, String sql) {}
			@Override public void executed(String identifier, Object res) {}
			@Override public void execute(String identifier) {}
			@Override public void execute(String identifier, String sql) {}
			@Override public void builderQuery(String identifier, String query, String sql, Map<String, String> params) {}
			@Override public void addParam(String identifier, Object param) {}
			@Override public void log(Map<HttpServerProfilerEvent, Long> events) {}
			@Override public void missingParameter(String module, String key, Map<String, Object> variables, String locale) {}
			@Override public void missingLocale(String locale) {}
			@Override public void loadFile(String locale, String domain, String path, boolean success) {}
			@Override public String getIdentifier() {
				return "emptyProfiler";
			}
			@Override public void init(Env appEnv, Register register) {}
		};
	}
	
}
