package toti.templating.parsing2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import common.exceptions.LogicException;
import toti.templating.parsing2.enums.TagMode;

public class TagParser {

	private final boolean isClose;
	
	private String preTag = "";
	private String tagName = "";
	private String paramName = "";
	private String paramValue = "";
	
	private TagMode mode = TagMode.TAG;
	
	private Map<String, String> params = new HashMap<>();
	
	public TagParser(boolean isClose) {
		this.isClose = isClose;
	}
	
	public String getPre() {
		return preTag;
	}
	
	public boolean isClose() {
		return isClose;
	}

	public String getName() {
		return tagName;
	}

	public Map<String, String> getParams() {
		if (!paramName.isEmpty()) {
			params.put(paramName, paramValue);
		}
		return params;
	}

	public void addVariable(VariableParser var) {
		preTag = var.getDeclare();
		// TODO check allready can be started param mode
		paramValue += var.getCalling();
		mode = TagMode.PARAM_VALUE;
	}
	
	public void parse(char previous, char actual, boolean isSingleQuoted, boolean isDoubleQuoted) throws IOException {
		if (mode == TagMode.TAG && actual == ' ') {
			mode = TagMode.NAN;
		} else if (mode == TagMode.TAG) {
			tagName += actual;
		} else if (mode == TagMode.NAN && actual != ' ' && actual != '=' && !isDoubleQuoted && !isSingleQuoted) {
			if (!paramName.isEmpty()) {
				params.put(paramName, paramValue);
				paramName = "";
				paramValue = "";
			}
			mode = TagMode.PARAM_NAME;
			paramName += actual;
		} else if (mode == TagMode.NAN && actual != ' ' && (isDoubleQuoted || isSingleQuoted)) {
			if (paramName.isEmpty()) {
				throw new LogicException("Attribute without name"); // TODO another place
			}
			mode = TagMode.PARAM_VALUE;
		} else if (mode == TagMode.PARAM_NAME && (actual == ' ' || actual == '=')) {
			mode = TagMode.NAN;
		} else if (mode == TagMode.PARAM_VALUE && !isDoubleQuoted && !isSingleQuoted) {
			mode = TagMode.NAN;
			params.put(paramName, paramValue);
			paramName = "";
			paramValue = "";
		} else if (mode == TagMode.PARAM_NAME) {
			paramName += actual;
		} else if (mode == TagMode.PARAM_VALUE) {
			paramValue += actual;
		}
	}
	
}
