package toti.templating.parsing2;

import java.util.LinkedList;

import common.functions.Implode;
import toti.templating.parsing2.enums.VarMode;

public class VariableParser {

	private int position;
	private VarMode mode = VarMode.VAR_NAME;
	private final StringBuilder declare = new StringBuilder();
	private boolean escape = true;
	
	public VariableParser(int position) {
		this.position = position;
	}
	
	private LinkedList<Object> params = new LinkedList<>();
	private LinkedList<String> classes = new LinkedList<>();
	
	private String cache = "";
	private String auxCache = "";
	private int level = 0;
	private String clazz = null;
	
	public void accept(char previous, char actual, boolean isSingleQuoted, boolean isDoubleQuoted) {
		if (actual == '|' && !isSingleQuoted && !isDoubleQuoted) {
			finish(cache);
			if (mode == VarMode.APPENDIX) {
				finishAppendix(cache);
			}
			cache = "";
			mode = VarMode.APPENDIX;
		} else if (actual == '.' && !isSingleQuoted && !isDoubleQuoted && mode != VarMode.PARAMS) {
			finish(cache);
			mode = VarMode.VAR_NAME;
			cache = "";
		} else if (actual == '(' && !isSingleQuoted && !isDoubleQuoted && mode != VarMode.PARAMS) {
			mode = VarMode.PARAMS;
			auxCache = cache;
			cache = "";
		} else if (actual == ',' && !isSingleQuoted && !isDoubleQuoted && mode != VarMode.PARAMS) {
			throw new RuntimeException("Syntax error: ','");
		} else if (actual == ')' && !isSingleQuoted && !isDoubleQuoted && mode != VarMode.PARAMS) {
			throw new RuntimeException("Syntax error: ')'");
		} else if (actual == ',' && !isSingleQuoted && !isDoubleQuoted && mode == VarMode.PARAMS) {
			Object var = finishObjectParam(cache);
			params.add(var);
			classes.add(var.getClass().getCanonicalName()+ ".class");
			cache = "";
		} else if (actual == ')' && !isSingleQuoted && !isDoubleQuoted && mode == VarMode.PARAMS) {
			if (!cache.isEmpty()) {
				Object var = finishObjectParam(cache);
				params.add(var);
				classes.add(var.getClass().getCanonicalName()+ ".class");
			}
			cache = auxCache;
			auxCache = "";
			mode = VarMode.METHOD_NAME;
		} else {
			cache += actual;
		}
	}
	
	private void finishAppendix(String cache) {
		if (cache.equalsIgnoreCase("noescape")) {
			escape = false;
		} else {
			clazz = cache;
		}
	}
	
	private void finish(String cache) {
		if (mode == VarMode.APPENDIX) {
			finishAppendix(cache);
			return;
		}
		String current = "o" + position + "_" + level;
		String last = "o" + position + "_" + (level-1);
		cache = cache.trim();
		
		if (level == 0) {
			declare.append(String.format(
				"Object o%s_%s=getVariable(\"%s\");",
				position, level, cache
			));
		} else if (mode == VarMode.VAR_NAME) {
			declare.append(String.format(
				"Object %s=%s.getClass().getMethod(\"get%s\").invoke(%s);",
				current, last, 
				cache.substring(0,1).toUpperCase() + cache.substring(1),
				last
			));
		} else if (mode == VarMode.METHOD_NAME && params.size() > 0) {
			/*
			declare.append(String.format(
				"Object %s=%s.getClass().getMethod(\"%s\",%s).invoke(%s,%s);",
				current, last, cache, Implode.implode(",", classes), last, Implode.implode(",", params)
			));
			/*/
			declare.append(String.format("Object %s=null;", current));
            declare.append("try{");
            declare.append(String.format(
                "%s=%s.getClass().getMethod(\"%s\",%s).invoke(%s,%s);",
                current, last, cache, Implode.implode(",", classes), last, Implode.implode(",", params)
            ));
            declare.append("}catch(NoSuchMethodException e){");
            declare.append(String.format(
                     "%s=%s.getClass().getMethod(\"%s\",%s).invoke(%s,%s);",
                     current, last, cache, Implode.implode((o)->"Object.class", ",", classes), last, Implode.implode(",", params)
                ));
            declare.append("}");
            //*/
		} else if (mode == VarMode.METHOD_NAME) {
			declare.append(String.format(
				"Object %s=%s.getClass().getMethod(\"%s\").invoke(%s);",
				current, last, cache, last
			));
		} else {
			throw new RuntimeException("Syntax error: Missing ')'");
		}
		level++;
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
			// return object.substring(1, object.length() -2);
			return object;
		}
		if (object.matches("^([0-9]+)$")) {
			return Integer.parseInt(object);
		}
		if (object.matches("^([0-9\\.]+)$")) {
			return Integer.parseInt(object);
		}
		return (Object)object; // is variable
	}
	
	public boolean escape() {
		return escape;
	}
	
	public String getDeclare() {
		if (!cache.isEmpty()) {
			finish(cache);
		}
		return declare.toString();
	}
	
	public String getCalling() {
		if (clazz != null) {
			//return String.format("%s.class.cast(o%s_%s)", clazz, position, level-1);
			return String.format("new common.structures.DictionaryValue(o%s_%s).getValue(%s.class)", position, level-1, clazz);
		}
		return String.format("o%s_%s", position, level-1);
	}
	
	public void addVariable(VariableParser var) {
		declare.append(var.getDeclare());
		classes.add(var.clazz == null ? var.getCalling() + ".getClass()" : var.clazz + ".class");
		params.add(var.getCalling());
	}
	
	@Override
	public String toString() {
		return "Variable " + position + " [" + declare + " " + getCalling() + "]";
	}
}
