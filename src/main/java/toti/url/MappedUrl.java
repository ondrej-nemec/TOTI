package toti.url;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import common.structures.Tuple2;
import json.Jsonable;
import toti.annotations.Domain;
import toti.validation.Validator;

public class MappedUrl implements Jsonable{

	private final String moduleName;
	private final String controllerUrl;
	private final String methodUrl;
	private final String pathUrl;

	private final String className;
	private final String methodName;
	private final List<Tuple2<Class<?>, String>> params;
	
	private final List<String> paramNames;
		
	private boolean isRegex = false;
	
	private final Domain[] domains;
	
	private final Optional<Validator> validator;
	
	private final boolean isApi;

	public MappedUrl(
			String moduleName,
			String controllerUrl,
			String methodUrl,
			String pathUrl,
			String className, String methodName,
			Domain[] domains, boolean isApi,
			Optional<Validator> validator) {
		this.moduleName = moduleName;
		this.controllerUrl = controllerUrl;
		this.methodUrl = methodUrl;
		this.pathUrl = pathUrl;
		
		this.className = className;
		this.methodName = methodName;
		this.params = new LinkedList<>();
		this.paramNames = new LinkedList<>();
		this.domains = domains;
		this.isApi = isApi;
		this.validator = validator;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public void addParam(Class<?> clazz, String name) {
		params.add(new Tuple2<>(clazz, name));
	}
	
	public void forEachParams(BiConsumer<Class<?>, String> consumer) {
		params.forEach((tuple)->{
			consumer.accept(tuple._1(), tuple._2());
		});
	}
/*
	public List<Tuple2<Class<?>, String>> getParams() {
		return params;
	}

	public List<String> getParamNames() {
		return paramNames;
	}
	*/
	public String getParamName(int index) {
		return paramNames.get(index);
	}
	
	public void addParamName(String paramName) {
		paramNames.add(paramName);
	}

	public boolean isRegex() {
		return isRegex;
	}

	public void setRegex(boolean isRegex) {
		this.isRegex = isRegex;
	}

	public boolean isSecured() {
		return domains != null;
	}
	
	public Domain[] getSecured() {
		return domains;
	}

	public Optional<Validator> getValidator() {
		return validator;
	}

	public boolean isApi() {
		return isApi;
	}
	
	public String getModuleName() {
		return moduleName;
	}

	public String createParametrizedLink() {
		List<UrlParam> regexParams = new ArrayList<>();
		paramNames.forEach(n->regexParams.add(new UrlParam(true)));
		
		return Link.get().create(
			moduleName, pathUrl, controllerUrl, methodUrl, regexParams
		);
	}

	@Override
	public String toString() {
		return "{moduleName=" + moduleName + ", controllerUrl=" + controllerUrl + ", methodUrl=" + methodUrl
				+ ", pathUrl=" + pathUrl + ", allowedMethods="
				+ ", className=" + className + ", methodName=" + methodName
				+ ", params=" + params + ", paramNames=" + paramNames + ", isRegex=" + isRegex + ", domains="
				+ Arrays.toString(domains) + ", validator=" + validator + ", isApi=" + isApi + "}";
	}
	@Override
	public Object toJson() {
		return toString();
	}
	
}
