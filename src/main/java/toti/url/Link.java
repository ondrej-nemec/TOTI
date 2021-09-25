package toti.url;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

import common.exceptions.LogicException;
import common.functions.StackTrace;
import common.structures.DictionaryValue;
import common.structures.ObjectBuilder;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.url.mock.MockCreator;
import toti.registr.Registr;
import toti.response.Response;

public class Link {
	
	public final static String PATH = "[path]";
	public final static String MODULE = "[module]";
	public final static String CONTROLLER = "[controller]";
	public final static String METHOD = "[method]";
	public final static String LANG = "[lang]";
	public final static String PARAM = "[param]";
	
	public static String PATTERN = "/[module]</[path]>/[controller]/[method]</[param]></[param]>"; // TODO configurable, one place urls
	
	public static Link get() {
		return new Link(PATTERN);
	}

	public static Link get(String pattern) {
		return new Link(pattern);
	}
	
	private final String pattern;
	
	protected Link(String pattern) {
		this.pattern = pattern;
	}
	
	public String create(String method, UrlParam ...params) {
		try {
			Class<?> controllerClass = getControllerClass();
			return create(getModule(controllerClass.getName()), controllerClass, getMethod(controllerClass, method, params), params);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String create(Class<?> controller, String method, UrlParam ...params) {
		try {
			return create(getModule(controller.getName()), controller, getMethod(controller, method, params), params);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String create(Class<?> module, Class<?> controller, String method, UrlParam ...params) {
		try {
			return create(getModule(module), controller, getMethod(controller, method, params), params);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public <T, M extends Module> String create(Class<M> module, Class<T> controller, Function<T, Response> method, UrlParam ...params) {
		try {
			return create(getModule(module), controller, getMethod(controller, method), params);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String create(Module module, Class<?> controller, Method method, UrlParam ...params) {
		if (!controller.isAnnotationPresent(Controller.class)) {
			throw new LogicException("Class " + controller + " is not TOTI controller");
		}
		if (!method.isAnnotationPresent(Action.class)) {
			throw new LogicException("Method " + method + " is not TOTI action");
		}
		try {
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

	protected String create(String module, String path, String controller, String method, UrlParam ...params) {
		return create(pattern, module, path, controller, method, params);
	}
	
	private String create(String pattern, String module, String path, String controller, String method, UrlParam ...params) {
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
				.replace(">", "");
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
	
	private Class<?> getControllerClass() throws ClassNotFoundException {
		return Class.forName(StackTrace.classParent(
			ste->Class.forName(ste.getClassName()).isAnnotationPresent(Controller.class)
		));
	}
	
	private String getPath(Module module, Class<?> controller) {
		return controller.getName()
		.replace(".", "/")
		.replace(module.getControllersPath(), "")
		.replace(controller.getSimpleName(), "");
	}

	private Module getModule(String controllerClass) throws Exception {
		String moduleClass = Registr.get()._getFactory(controllerClass)._2();
		return getModule(Class.forName(moduleClass));
	}
	
	private Module getModule(Class<?> clazz) throws Exception {
		return Module.class.cast(clazz.newInstance());
	}
	
	private <T> Method getMethod(Class<T> controller, Function<T, Response> method) {
		ObjectBuilder<Method> builder = new ObjectBuilder<>();
		T result = new MockCreator().createMock(controller, builder);
    	method.apply(result);
    	return builder.get();
	}
	
	private Method getMethod(Class<?> clazz, String method, UrlParam ...params) throws Exception {
		try {
			Class<?> []classes = new Class[params.length];
			for (int i = 0; i < params.length; i++) {
				classes[i] = params[i].getClass(); // TODO can be null !!
			}
			return clazz.getMethod(method, classes);
		} catch (NoSuchMethodException e) {
			for (Method m : clazz.getMethods()) {
				if (m.isAnnotationPresent(Action.class) && m.getName().equals(method)) {
					return m;
				}
			}
			throw e;
		}
		
	}
	
}
