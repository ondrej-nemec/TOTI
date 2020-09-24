package mvc.urlMapping;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import common.structures.Tuple2;
import socketCommunication.http.HttpMethod;

public class MappedUrl {

	private String url;
	
	private final HttpMethod[] allowedMethods;

	private final String className;
	
	private final String methodName;
	
	private final List<Tuple2<Class<?>, String>> params;
	
	private final List<String> paramNames;
	
	private final String folder;
	
	private boolean isRegex = false;

	public MappedUrl(String url, HttpMethod[] allowedMethods, String className, String methodName, String folder) {
		this.url = url;
		this.folder = folder;
		this.allowedMethods = allowedMethods;
		this.className = className;
		this.methodName = methodName;
		this.params = new LinkedList<>();
		this.paramNames = new LinkedList<>();
	}

	public String getUrl() {
		return url;
	}
	
	public String getFolder() {
		return folder;
	}

	public void appendUrl(String append) {
		url += "/" + append;
	}

	public HttpMethod[] getAllowedMethods() {
		return allowedMethods;
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

	public List<Tuple2<Class<?>, String>> getParams() {
		return params;
	}

	public List<String> getParamNames() {
		return paramNames;
	}
	
	public String getParamName(int index) {
		return paramNames.get(index);
	}
	
	public void addParamName(String paramName) {
		paramNames.add(paramName);
	}
	
	@Override
	public String toString() {
		return url;
	}

	public boolean isRegex() {
		return isRegex;
	}

	public void setRegex(boolean isRegex) {
		this.isRegex = isRegex;
	}
	
}
