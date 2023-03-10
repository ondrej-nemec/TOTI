package toti.profiler;

import java.util.LinkedList;
import java.util.Map;

import ji.json.Jsonable;

public class TemplateLog implements Jsonable {

	private final String module;
	private final String path;
	private final String file;
	private final Map<String, Object> variables;
	
	private final LinkedList<TemplateLog> childs = new LinkedList<>();
	
	public TemplateLog(String module, String path, String file, Map<String, Object> variables) {
		this.module = module;
		this.path = path;
		this.file = file;
		this.variables = variables;
	}
	
	public void addChild(TemplateLog log) {
		childs.add(log);
	}

	public String getModule() {
		return module;
	}

	public String getPath() {
		return path;
	}

	public String getFile() {
		return file;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}
	
	
}
