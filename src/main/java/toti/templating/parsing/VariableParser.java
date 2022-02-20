package toti.templating.parsing;

import java.util.LinkedList;

import ji.common.functions.Implode;
import toti.templating.TemplateException;
import toti.templating.parsing.enums.VariableSource;

/**
 * Activated if previous == '$' and actual == '{'
 * @author Ondřej Němec
 *
 */
public class VariableParser implements Parser {
	
	enum VarState {
		NOTHING, CANDIDATE, VAR
	}
	enum VarMode {
		VAR_NAME, PARAMS, METHOD_NAME, APPENDIX
	}
	
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
	
	@Override
	public boolean accept(char previous, char actual, boolean isSingleQuoted, boolean isDoubleQuoted) {
		if (actual == '}' && !isSingleQuoted && !isDoubleQuoted) {
			finish(cache);
			cache = "";
			return true;
		} else if (actual == '|' && !isSingleQuoted && !isDoubleQuoted) {
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
			throw new TemplateException("Variable Syntax error: unexpected ','");
		} else if (actual == ')' && !isSingleQuoted && !isDoubleQuoted && mode != VarMode.PARAMS) {
			throw new TemplateException("Variable Syntax error: unexpected ')'");
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
		return false;
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
            params.clear();
		} else if (mode == VarMode.METHOD_NAME) {
			declare.append(String.format(
				"Object %s=%s.getClass().getMethod(\"%s\").invoke(%s);",
				current, last, cache, last
			));
		} else {
			throw new TemplateException("Variable Syntax error: Missing ')'");
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
	
	protected String getVariableName() {
		return String.format("o%s_%s", position, level-1);
	}

	public String getCalling(VariableSource mode) {
		String calling = String.format("getVariable(()->{%sreturn o%s_%s;})", declare.toString(), position, level-1);
		if (clazz != null) {
			calling = String.format("new DictionaryValue(%s).getValue(%s.class)", calling, clazz);
		}
		if (escape) {
			calling = selectEscape(calling, mode);
		}
		return calling;
	}

	private String selectEscape(String calling, VariableSource mode) {
		switch (mode) {
			case NO_ESCAPE: return calling;
			case JAVASCRIPT: return String.format("Template.escapeJavascript(%s,null)", calling);
			case JAVASCRIPT_S: return String.format("Template.escapeJavascript(%s,false)", calling);
			case JAVASCRIPT_D: return String.format("Template.escapeJavascript(%s,true)", calling);
			case JAVASCRIPT_PARAMETER: return String.format("Template.escapeJsParameter(%s)", calling);
			case STYLE: return String.format("Template.escapeStyle(%s)", calling);
			case STYLE_PARAMETER: return String.format("Template.escapeStyleParameter(%s)", calling);
			case URL: return String.format("Template.escapeUrl(%s)", calling);
			case PARAMETER:
			case HTML:
			default: return String.format("Template.escapeHtml(%s)", calling);
		}
	}

	@Override
	public String toString() {
		return "Variable " + declare + " [" + getCalling(VariableSource.NO_ESCAPE) + "]";
	}

	public void addVariable(VariableParser var) {
		declare.append(String.format("Object %s_aux=%s;", var.getVariableName(), var.getCalling(VariableSource.NO_ESCAPE)));
		classes.add(var.clazz == null ? String.format("%s_aux.getClass()", var.getVariableName()) : var.clazz + ".class");
		params.add(String.format("%s_aux", var.getVariableName()));
	}
}
