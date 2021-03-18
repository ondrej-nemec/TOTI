package toti.templating.parsing;

import java.io.IOException;
import java.util.LinkedList;

import common.functions.Implode;
import toti.templating.enums.VarParseState;

public class VariableParser {
	
	private final StringBuilder result = new StringBuilder();
	private final StringBuilder init = new StringBuilder();
	
	private final int level;
	
	public VariableParser(int level) {
		this.level = level;
	}
	
	public String getInit() {
		return init.toString();
	}
	
	public String getResult() {
		return result.toString();
	}
	
	public void addVariable(VariableParser var) {
		init.append(var.getInit());
		params.add(var.getResult());
	}
	
	private boolean isSingleQuoted = false;
	private boolean isDoubleQuoted = false;
	
	private VarParseState state = VarParseState.VAR_NAME;
	private String actualObject = "";
	private LinkedList<Object> params = new LinkedList<>();
	private int objectCount = 1;
	
	public boolean parse(char actual, char previous) throws IOException {
		if (actual == '"' && previous != '\\' && !isSingleQuoted) {
			isDoubleQuoted = !isDoubleQuoted;
		} else if (actual == '\'' && previous != '\\' && !isDoubleQuoted) {
			isSingleQuoted = !isSingleQuoted;
		}
		
			// Object o0_1=variables.get(\"var\");
			// o0_1.getClass().getMethod(\"equals\").invoke(o0_1, 1)
			// Object res = o.getClass().getMethod("equals", Object.class).invoke(o, params);
		if (actual == '}' && !(isSingleQuoted || isDoubleQuoted)) {
			finishMethod(actual);
			result.append("o" + level + "_" + objectCount);
			return false;
		} else if (actual == '.' && !isSingleQuoted && !isDoubleQuoted) {
			if (finishMethod(actual)) {
				params = new LinkedList<>();
				actualObject = "";
				objectCount++;
				state = VarParseState.METHOD_NAME;				
			}
		} else if (actual == '(' && !isSingleQuoted && !isDoubleQuoted) {
			state = VarParseState.PARAMS;
		} else if (actual == ')' && !isSingleQuoted && !isDoubleQuoted) {
			if (params.size() > 0) {
				Object last = params.removeLast();
				params.add(finishObjectParam(last.toString()));
			}
			state = VarParseState.PARAMS_FINISHED;
		} else if (state == VarParseState.VAR_NAME || state == VarParseState.METHOD_NAME) {
			actualObject += actual;
		} else if (state == VarParseState.PARAMS && actual == ',' && !isSingleQuoted && !isDoubleQuoted) {
			Object last = params.removeLast();
			params.add(finishObjectParam(last.toString()));
			params.add("");
		} else if (state == VarParseState.PARAMS) {
			if (params.size() == 0) {
				params.add("");				
			}
			Object last = params.removeLast();
			params.add(last.toString() + actual);
		}
		return true;
	}
	
	private boolean finishMethod(char actual) {
		String actuName = "o" + level + "_" + objectCount;
		String lastName = "o" + level + "_" + (objectCount-1);
		if (state == VarParseState.VAR_NAME) {
			init.append(String.format("Object %s=variables.get(\"%s\");", actuName, actualObject.trim()));
		} else if (state == VarParseState.METHOD_NAME || (state == VarParseState.PARAMS_FINISHED && params.size() == 0)) {
			String method = actualObject.trim();
			if (state == VarParseState.METHOD_NAME) {
				method = "get" + method.substring(0, 1).toUpperCase() + method.substring(1);
			}
			init.append(String.format(
					"Object %s=%s.getClass().getMethod(\"%s\").invoke(%s);",
					actuName, lastName, method, lastName
			));
		} else if (state == VarParseState.PARAMS_FINISHED) {
			String[] classes = new String[params.size()];
			for(int i = 0; i < params.size(); i++) {
				classes[i] = params.get(i).getClass().getCanonicalName()+ ".class";
			}
			String method = actualObject.trim();
			init.append(String.format(
					"Object %s=%s.getClass().getMethod(\"%s\",%s).invoke(%s,%s);",
					actuName, lastName, method,
					Implode.implode(",", classes), lastName,
					Implode.implode(",", params.toArray())
			));
		} else {
			return false;
		}
		return true;
	}
	
	private Object finishObjectParam(String object) {
		if (object.toLowerCase().equals("null")) {
			return null;
		}
		if (object.toLowerCase().equals("false") || object.toLowerCase().equals("true")) {
			return Boolean.parseBoolean(object);
		}
		if (object.length() == 3 && object.charAt(0) == '\'' && object.charAt(2) == '\'') {
			return object.charAt(1);
		}
		if (object.length() >= 2 && object.charAt(0) == '"' && object.charAt(object.length()-1) == '"') {
			return object.substring(1, object.length() -2);
		}
		if (object.matches("^([0-9]+)$")) {
			return Integer.parseInt(object);
		}
		if (object.matches("^([0-9\\.]+)$")) {
			return Integer.parseInt(object);
		}
		return (Object)object; // is variable
	}
	
	public String toString() {
		return init.toString() + result.toString();
	}

}
