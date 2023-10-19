package toti.answers.router;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
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
		
	public Link(Register register) {
		this.register = register;
	}
	
	private String getPath(Module module, Class<?> controller) {
		return controller.getName()
				.replace(".", "/")
			// TODO	.replace(module.getControllersPath(), "")
				.replace(controller.getSimpleName(), "");
	}
		
	/**
	 * Parse string to link
	 * Required format <[controller]>:[method]<:[parameter]>{n}
	 * @param url
	 * @return
	 */
	public String create(String url) {
        String[] values = url.split(":");
        if (values.length == 1) {
            throw new LogicException("HREF/SRC parameter required format: '[controller]:[method]<:[parameter]>{n}'");
        }
        
        String controller = null;
        if (!values[0].isEmpty()) {
            controller = values[0];
        }
        String method = values[1];
        Map<String, Object> getParams = new HashMap<>();
        List<Object> urlParams = new LinkedList<>();
        for (int i = 2; i < values.length; i++) {
        	if (values[i].contains("=")) {
                String[] get = values[i].split("=", 2);
                getParams.put(get[0], get[1]);
        	} else {
                urlParams.add(values[i]);
        	}
        }
		return create(controller, method, getParams, urlParams.toArray());
	}
	
	/*public String create(String controller, String method) {
		return create(controller, method, new HashMap<>(), new Object[] {});
	}
	
	public String create(String controller, String method, Object... params) {
		return create(controller, method, new HashMap<>(), params);
	}
	
	public String create(String controller, String method, Map<String, Object> params) {
		return create(controller, method, params, new Object[] {});
	}*/
	
	public String create(String controllerName, String methodName, Map<String, Object> getParams, Object... urlParams) {
		try {
			Class<?> controller = Class.forName(controllerName);
			/*if (controllerName == null) {
				controller = Class.forName(StackTrace.classParent(
					ste->Class.forName(ste.getClassName()).isAnnotationPresent(Controller.class)
				));
			} else {
				controller = Class.forName(controllerName);
			}*/
			Method method = getMethod(controller, methodName);
			
			return create(controller, method, getParams, urlParams);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create link.", e);
		}
	}
	
	private Method getMethod(Class<?> controller, String methodName) throws NoSuchMethodException {
		try {
			/*Class<?> []classes = new Class[params.size()];
			for (int i = 0; i < params.size(); i++) {
				classes[i] = params.get(i).getClass(); // TODO can be null !!
			}
			method = controller.getMethod(methodName, classes);*/
			return controller.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			for (Method m : controller.getMethods()) {
				if (m.isAnnotationPresent(Action.class) && m.getName().equals(methodName)) {
					return m;
				}
			}
			throw e;
		}
	}
	
	/////////////////////////////////////
	
	private <T> Method getMethod(Class<T> controller, Function<T, ResponseAction> method) {
		ObjectBuilder<Method> builder = new ObjectBuilder<>();
		T result = new MockCreator().createMock(controller, builder);
    	method.apply(result);
    	return builder.get();
	}
	
	public <T> String create(Class<T> controller, Function<T, ResponseAction> method) {
		return create(controller, getMethod(controller, method), new HashMap<>(), new Object[] {});
	}
	
	public <T> String create(Class<T> controller, Function<T, ResponseAction> method, Object... params) {
		return create(controller, getMethod(controller, method), new HashMap<>(), params);
	}
	
	public <T> String create(Class<T> controller, Function<T, ResponseAction> method, Map<String, Object> params) {
		return create(controller, getMethod(controller, method), params, new Object[] {});
	}
	
	public <T> String create(Class<T> controller, Function<T, ResponseAction> method, Map<String, Object> getParams, Object... urlParams) {
		return create(controller, getMethod(controller, method), getParams, urlParams);
	}
	
	/////////////////////////////////////
	
	protected <T> String create(Class<T> controller, Method method, Map<String, Object> getParams, Object... pathParams) {
		if (!controller.isAnnotationPresent(Controller.class)) {
			throw new LogicException("Class " + controller + " is not TOTI controller");
		}
		if (!method.isAnnotationPresent(Action.class)) {
			throw new LogicException("Method " + method + " is not TOTI action");
		}
		try {
			Module module = register.getModuleForClass(controller);

			StringBuilder uri = new StringBuilder("/");
			uri.append(module.getName());
			uri.append("/");
			uri.append(controller.getAnnotation(Controller.class).value());
			uri.append("/");
			uri.append(method.getAnnotation(Action.class).path());
			for (Object o : pathParams) {
				uri.append("/");
				uri.append(o);
			}
			if (getParams.size() > 0) {
				StringBuilder get = new StringBuilder();
				getParams.forEach((k, v)->parseParams(get, k, v));
				uri.append("?");
				uri.append(get.toString());
			}
			
			return uri.toString();
			/*return parse(
				module.getName(),
				getPath(module, controller),
				controller.getAnnotation(Controller.class).value(), 
				method.getAnnotation(Action.class).path(),
				parameters
			);*/
		} catch (Exception e) {
			throw new RuntimeException("Cannot create link", e);
		}
	}
	
	private void parseParams(StringBuilder get, String key, Object value) {
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
		// TODO lang
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
				.replace("getVariable(()-{", "getVariable(()->{") // TODO fix
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
