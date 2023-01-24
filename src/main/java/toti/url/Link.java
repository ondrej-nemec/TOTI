package toti.url;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.DictionaryValue;
import ji.common.structures.ObjectBuilder;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.register.Register;
import toti.response.Response;
import toti.url.mock.MockCreator;

public class Link {
	
	public final static String PATH = "[path]";
	public final static String MODULE = "[module]";
	public final static String CONTROLLER = "[controller]";
	public final static String METHOD = "[method]";
	public final static String LANG = "[lang]";
	public final static String PARAM = "[param]";

	private static String patternCache = null;
	private static Register registerCache = null;

	@Deprecated
	public static Link get() {
		return new Link(patternCache, registerCache);
	}
	
	// Allow anything starting with "/", except paths starting
	// "//" and "/\".
	public static boolean isRelative(String url) {
	  return url.matches("/[^/\\\\]?.*");
	}

	@Deprecated
	public static void init(String pattern, Register reg) {
		patternCache = pattern;
		registerCache = reg;
	}

	private final String pattern;
	private final Register register;
	
	//private ThrowingFunction<Class<?>, Module, Exception> getModule;
	/*private ThrowingSupplier<Class<?>, Exception> getController = ()->Class.forName(StackTrace.classParent(
		ste->Class.forName(ste.getClassName()).isAnnotationPresent(Controller.class)
	));*/
	//private ThrowingFunction<Class<?>, Method, Exception> getMethod = null; // TODO set actual method?
//	private List<UrlParam> params = new LinkedList<>();
	
	public Link(String pattern, Register register) {
		this.pattern = pattern;
		this.register = register;
		/*getModule = (controllerClass)->{
			String moduleClass = register._getFactory(controllerClass.getName())._2();
			return Module.class.cast(Class.forName(moduleClass).getDeclaredConstructor().newInstance());
		};*/
	}
	
	/*** module ***

	@Deprecated
	public Link setModule(Module module) {
		getModule = (c)->module;
		return this;
	}

	@Deprecated
	public <M extends Module> Link setModule(Class<M> moduleClass) {
		getModule = (c)->Module.class.cast(moduleClass.getDeclaredConstructor().newInstance());
		return this;
	}
	
	/*** controller ***

	@Deprecated
	public Link setController(Class<?> controllerClass) {
		if (!controllerClass.isAnnotationPresent(Controller.class)) {
			throw new LogicException("Class " + controllerClass + " is not TOTI controller");
		}
		getController = ()->controllerClass;
		return this;
	}

	@Deprecated
	public Link setController(String controllerClass) {
		try {
			return setController(Class.forName(controllerClass));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot create link - " + controllerClass + " not found", e);
		}
	}
	
	/*** method ***

	@Deprecated
	public Link setMethod(String methodName) {
		getMethod = (clazz)->{
			try {
				Class<?> []classes = new Class[params.size()];
				for (int i = 0; i < params.size(); i++) {
					classes[i] = params.get(i).getClass(); // TODO can be null !!
				}
				Method method = clazz.getMethod(methodName, classes);
				if (!method.isAnnotationPresent(Action.class)) {
					throw new LogicException("Method " + method + " is not TOTI action");
				}
				return method;
			} catch (NoSuchMethodException e) {
				for (Method m : clazz.getMethods()) {
					if (m.isAnnotationPresent(Action.class) && m.getName().equals(methodName)) {
						return m;
					}
				}
				throw e;
			}
		};
		return this;
	}
	
	/*** universal ***

	@Deprecated
	public Link addGetParam(String name, Object value) {
		params.add(new UrlParam(name, value));
		return this;
	}

	@Deprecated
	public Link addUrlParam(Object value) {
		params.add(new UrlParam(value));
		return this;
	}
	
	/******/
	
	private String getPath(Module module, Class<?> controller) {
		return controller.getName()
				.replace(".", "/")
				.replace(module.getControllersPath(), "")
				.replace(controller.getSimpleName(), "");
	}
	/*
	@Deprecated
	public String create() {
		if (getMethod == null) {
			throw new LogicException("Target method is missing");
		}
		try {
			Class<?> controller = getController.get();
			Module module = getModule.apply(controller);
			Method method = getMethod.apply(controller);
			return create(
				module.getName(),
				getPath(module, controller),
				controller.getAnnotation(Controller.class).value(), 
				method.getAnnotation(Action.class).value(),
				params
			);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create link", e);
		}
	}
	*/
	
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
	
	public String create(String controller, String method) {
		return create(controller, method, new HashMap<>(), new Object[] {});
	}
	
	public String create(String controller, String method, Object... params) {
		return create(controller, method, new HashMap<>(), params);
	}
	
	public String create(String controller, String method, Map<String, Object> params) {
		return create(controller, method, params, new Object[] {});
	}
	
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
	
	private <T> Method getMethod(Class<T> controller, Function<T, Response> method) {
		ObjectBuilder<Method> builder = new ObjectBuilder<>();
		T result = new MockCreator().createMock(controller, builder);
    	method.apply(result);
    	return builder.get();
	}
	
	public <T> String create(Class<T> controller, Function<T, Response> method) {
		return create(controller, getMethod(controller, method), new HashMap<>(), new Object[] {});
	}
	
	public <T> String create(Class<T> controller, Function<T, Response> method, Object... params) {
		return create(controller, getMethod(controller, method), new HashMap<>(), params);
	}
	
	public <T> String create(Class<T> controller, Function<T, Response> method, Map<String, Object> params) {
		return create(controller, getMethod(controller, method), params, new Object[] {});
	}
	
	public <T> String create(Class<T> controller, Function<T, Response> method, Map<String, Object> getParams, Object... urlParams) {
		return create(controller, getMethod(controller, method), getParams, urlParams);
	}
	
	/////////////////////////////////////
	
	protected <T> String create(Class<T> controller, Method method, Map<String, Object> getParams, Object... urlParams) {
		if (!controller.isAnnotationPresent(Controller.class)) {
			throw new LogicException("Class " + controller + " is not TOTI controller");
		}
		if (!method.isAnnotationPresent(Action.class)) {
			throw new LogicException("Method " + method + " is not TOTI action");
		}
		try {
			List<UrlParam> parameters = new LinkedList<>();
			getParams.forEach((name, value)->parameters.add(new UrlParam(name, value)));
			for (Object o : urlParams) {
				parameters.add(new UrlParam(o));
			}
			
			String moduleClass = register._getFactory(controller.getName())._2();
			Module module = Module.class.cast(Class.forName(moduleClass).getDeclaredConstructor().newInstance());
			
			return parse(
				module.getName(),
				getPath(module, controller),
				controller.getAnnotation(Controller.class).value(), 
				method.getAnnotation(Action.class).value(),
				parameters
			);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create link", e);
		}
	}

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
	
}
