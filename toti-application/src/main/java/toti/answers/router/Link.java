package toti.answers.router;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.functions.StackTrace;
import ji.common.structures.DictionaryValue;
import ji.common.structures.ObjectBuilder;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.router.mock.MockCreator;
import toti.application.Module;
import toti.application.register.Register;

public class Link {
	
	public final static String PATH = "[path]";
	public final static String MODULE = "[module]";
	public final static String CONTROLLER = "[controller]";
	public final static String METHOD = "[method]";
	public final static String LANG = "[lang]";
	public final static String PARAM = "[param]";
	
	// Allow anything starting with "/", except paths starting
	// "//" and "/\".
	public static boolean isRelative(String url) {
	  return url.matches("/[^/\\\\]?.*");
	}

	private final Register register;
	private final UriPattern pattern;
		
	public Link(Register register, UriPattern pattern) {
		this.register = register;
		this.pattern = pattern;
	}
	
	/*private String getPath(Module module, Class<?> controller) {
		return controller.getName()
				.replace(".", "/")
			//	.replace(module.getControllersPath(), "")
				.replace(controller.getSimpleName(), "");
	}*/
		
	/**
	 * Parse string to link
	 * Required format <[controller]>:[method]<:[parameter]>{n}
	 * @param url
	 * @return
	 */
	public String create(String href) {
		StringHref base = parseStringHref(href);
		return create(base.getController(), base.getMethod(), base.getQueryParams(), base.getPathParams().toArray());
	}
	
	protected StringHref parseStringHref(String href) {
		String[] values = href.split(":");
        if (values.length == 0) {
			throw new LogicException("No HREF/SRC specified");
		}
        if (values.length == 1) {
            throw new LogicException("HREF/SRC parameter required format: '[controller]:[method]<:[parameter]>{n}'");
        }
        String controller = null;
        if (!values[0].isEmpty()) {
            controller = values[0];
        }
		Map<String, Object> queryParams = new HashMap<>();
		List<Object> pathParams= new LinkedList<>();
        String method = values[1];
        for (int i = 2; i < values.length; i++) {
        	if (values[i].contains("=")) {
                String[] get = values[i].split("=", 2);
                queryParams.put(get[0], get[1]);
        	} else {
        		pathParams.add(values[i]);
        	}
        }
        return new StringHref(controller, method, queryParams, pathParams);
	}
	
	public String create(String controllerName, String methodName, Map<String, Object> getParams, Object... urlParams) {
		try {
			Class<?> controller = getController(controllerName);
			Method method = getMethod(controller, methodName, urlParams.length);
			return create(controller, method, getParams, urlParams);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create link.", e);
		}
	}
	
	protected Class<?> getController(String controllerName) throws ClassNotFoundException {
		if (controllerName == null) {
			String name = StackTrace.classParent(
				ste->Class.forName(ste.getClassName()).isAnnotationPresent(Controller.class)
			);
			if (name == null) {
				throw new ClassNotFoundException();
			}
			return Class.forName(name);
		} else {
			return Class.forName(controllerName);
		}
	}
	
	protected Method getMethod(Class<?> controller, String methodName, int parameterCount) throws NoSuchMethodException {
		try {
			if (parameterCount == 0) {
				Method m = controller.getMethod(methodName);
				if (m.isAnnotationPresent(Action.class)) {
					return m;
				}		
			}
			throw new NoSuchMethodException(
				controller.getName() + "." + methodName + " with at least " + parameterCount
				+ " parameters (not exists or not @Action)"
			);
		} catch (NoSuchMethodException e) {
			for (Method m : controller.getMethods()) {
				if (m.isAnnotationPresent(Action.class) && m.getName().equals(methodName)) {
					if (m.getParameterCount() == parameterCount) {
						return m;
					} else if (m.getParameterCount() > parameterCount) {
						return m;
					}
				}
			}
			throw e;
		}
	}
	
	/////////////////////////////////////
	
	private <T> Method mockMethod(Class<T> controller, Function<T, ResponseAction> method) {
		ObjectBuilder<Method> builder = new ObjectBuilder<>();
		T result = new MockCreator().createMock(controller, builder);
    	method.apply(result);
    	return builder.get();
	}
	
	public <T> String create(Class<T> controller, Function<T, ResponseAction> method) {
		return create(controller, mockMethod(controller, method), new HashMap<>(), new Object[] {});
	}
	
	public <T> String create(Class<T> controller, Function<T, ResponseAction> method, Object... params) {
		return create(controller, mockMethod(controller, method), new HashMap<>(), params);
	}
	
	public <T> String create(Class<T> controller, Function<T, ResponseAction> method, Map<String, Object> params) {
		return create(controller, mockMethod(controller, method), params, new Object[] {});
	}
	
	public <T> String create(Class<T> controller, Function<T, ResponseAction> method, Map<String, Object> getParams, Object... urlParams) {
		return create(controller, mockMethod(controller, method), getParams, urlParams);
	}
	
	/////////////////////////////////////
	
	protected <T> String create(Class<T> controller, Method method, Map<String, Object> queryParams, Object... pathParams) {
		if (!controller.isAnnotationPresent(Controller.class)) {
			throw new LogicException("Class " + controller + " is not TOTI controller");
		}
		if (!method.isAnnotationPresent(Action.class)) {
			throw new LogicException("Method " + method + " is not TOTI action");
		}
		try {
			Module module = register.getModuleForClass(controller); 
			StringBuilder uri = new StringBuilder(pattern.createUri(
				module,
				controller,
				method,
				module.getName(),
				controller.getAnnotation(Controller.class).value(),
				method.getAnnotation(Action.class).path()	
			));
			for (Object o : pathParams) {
				if (uri.toString().contains(UriPattern.PARAM)) {
					uri = new StringBuilder(
						uri.toString()
						.replaceFirst(UriPattern.PARAM.replace("[", "\\[").replace("]", "\\]"), o == null ? "" : o.toString())
					);
				} else {
					uri.append("/");
					uri.append(o);
				}
			}
			if (queryParams.size() > 0) {
				StringBuilder get = new StringBuilder();
				queryParams.forEach((k, v)->parseParams(get, k, v));
				uri.append("?");
				uri.append(get.toString());
			}
			
			return uri.toString();
		} catch (Exception e) {
			throw new RuntimeException("Cannot create link", e);
		}
	}
	
	protected void parseParams(StringBuilder get, String key, Object value) {
		if (value instanceof Map) {
			new DictionaryValue(value).getMap().forEach((k, v)->{
				parseParams(get, String.format("%s[%s]", key, k), v);
			});
		} else if (value instanceof Iterable) {
			new DictionaryValue(value).getList().forEach((v)->{
				parseParams(get, key + "[]", v);
			});
		} else {
			if (get.toString().length() > 0) {
				get.append("&");
			}
			get.append(key);
			get.append("=");
			get.append(value);
		}
	}
	
/*
	protected String parse(String module, String path, String controller, String method, List<UrlParam> params) {
		return parse(pattern, module, path, controller, method, params);
	}
	
	private String parse(String pattern, String module, String path, String controller, String method, List<UrlParam> params) {
		pattern = parseUrl(pattern, MODULE, module);
		pattern = parseUrl(pattern, PATH, path);
		pattern = parseUrl(pattern, CONTROLLER, controller);
		pattern = parseUrl(pattern, METHOD, method);
		StringBuilder get = new StringBuilder("");
		for (UrlParam p : params) {
			Object value = p.getValue() == null ? "" : p.getValue();
			if (p.getName() == null) {
				pattern = parseUrl(pattern, PARAM, value.toString());
			} else {
				parseParams(get, p.getName(), value);
			}
		}
		pattern = pattern.replaceAll("<[^<>\\(]*\\[([^<>]*)\\][^<>]*>", "") //replace missing optional characters
				.replace("<", "")
				.replace(">", "")
				.replace("getVariable(()-{", "getVariable(()->{") // fix
				;
		return pattern + (get.toString().length() > 1 ? "?" + get.toString() : "");
	}
	
	private String parseUrl(String pattern, String key, String value) {
		value = normalize(value);
		if (value.length() == 0) {
			return pattern;
		}
		key = key.replace("[", "\\[").replace("]", "\\]");
		return pattern.replaceFirst(key, value);
	}
	
	private String normalize(String url) {
		if (url == null) {
			return "";
		}
		if (url.length() > 0 && url.charAt(0) == '/') {
			url = url.substring(1);
		}
		if (url.length() > 0 && url.charAt(url.length() - 1) == '/') {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}
	*/
}
