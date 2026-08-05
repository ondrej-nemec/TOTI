package toti.core.application.register;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.router.UriPattern;
import toti.core.application.Module;
import toti.core.extensions.CustomErrorHandler;
import toti.core.extensions.Extension;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.common.structures.Tuple2;
import toti.lib.tcpip.enums.HttpMethod;

public class Register {

	private final Map<String, Factory<?>> FACTORIES;
	private final Map<String, Object> SERVICES;

	private final Map<String, Tuple2<Factory<?>, Module>> CONTROLLERS;

	private final Param root;
	private final ObjectBuilder<Module> module;
	private final UriPattern pattern;

	private final Map<String, Extension> extensions;

	private Factory<CustomErrorHandler> customErrorHandler = null;

	public Register(Param root, ObjectBuilder<Module> module, UriPattern pattern, Map<String, Extension> extensions) {
		this.FACTORIES = new HashMap<>();
		this.SERVICES = new HashMap<>();
		this.CONTROLLERS = new HashMap<>();
		this.root = root;
		this.module = module;
		this.pattern = pattern;
		this.extensions = extensions;
	}

	public <T> void addController(Class<?> clazz, Factory<T> factory) {
		if (module.get() == null) {
			throw new RegisterException("Cannot add controller outside 'initInstance' method. Class: " + clazz);
		}
		if (!clazz.isAnnotationPresent(Controller.class)) {
			throw new RegisterException("Class is not TOTI controller: " + clazz);
		}
		if (clazz.isInterface() || clazz.isAnonymousClass() || clazz.isPrimitive()) {
			throw new RegisterException("Class is interface or anonymous: " + clazz);
		}
		if (CONTROLLERS.containsKey(clazz.getName())) {
			throw new RegisterException("One controler can be registered only once. Class: " + clazz);
		}
		CONTROLLERS.put(clazz.getName(), new Tuple2<>(factory, module.get()));
		if (CustomErrorHandler.class.isAssignableFrom(clazz)) {
			if (this.customErrorHandler != null) {
				throw new RegisterException("Another CustomErrorHandler is already defined.");
			}
			this.customErrorHandler = ()->CustomErrorHandler.class.cast(factory.create());
		}

		for (Method m : clazz.getMethods()) {
			if (m.isAnnotationPresent(Action.class)) {
				HttpMethod[] methods = getHttpMethods(m);
				Action actionAnotation = getActionAnnotation(m);
				String actionPart = actionAnotation.path();
				
				String pattern = this.pattern.createUri(
					module.get(), clazz, m,
					module.get().getName(), clazz.getAnnotation(Controller.class).value(), actionPart
				);
				Param base = root;
				LinkedList<Class<?>> parameters = new LinkedList<>();
				for (Parameter p : m.getParameters()) {
					parameters.add(p.getType());
				}
				String parametersPart = parameters.toString();
				// substring - remove first '/'
				for (String part : pattern.substring(1).split("/")) {
					if (part.equals(UriPattern.PARAM)) {
						if (parameters.isEmpty()) {
							throw new RegisterException(
								"URI pattern expects more parameters than method contains. "
								+ module.get().getName()
								+ ":" + clazz.getAnnotation(Controller.class).value()
								+ ":" + actionPart
								+ ":" + parametersPart
							);
						}
						base = base.addChild(null);
					} else {
						base = base.addChild(part);
					}
				}
				MappedAction action = new MappedAction(
					module.get().getName(), clazz.getName(), m.getName(), parametersPart,
					m, factory, methods
				);
				for (HttpMethod method : methods) {
					base.addAction(method, action);
				}
			}
		}
	}

	private HttpMethod[] getHttpMethods(Method m) {
		Action actionAnotation = getActionAnnotation(m);
		return  actionAnotation.methods();
	}

	private Action getActionAnnotation(Method m) {
		return m.getAnnotation(Action.class);
	}

	protected Param getParam(String part, Param parent) {
		if (part == null || part.isEmpty()) {
			return parent;
		}
		if (part.contains("/")) {
			throw new RuntimeException("URL path cannot contains / in '" + part + "'");
		}
		return parent.addChild(part);
	}

	/*******/

	private Tuple2<Factory<?>, Module> getController(Class<?> clazz) {
		Tuple2<Factory<?>, Module> result = CONTROLLERS.get(clazz.getName());
		if (result == null) {
			throw new RegisterException("Missing controller " + clazz.getName());
		}
		return result;
	}

	// link
	public Module getModuleForClass(Class<?> controller) {
		return getController(controller)._2();
	}

	/********************************/

	public <E extends Extension> E getExtension(Class<E> clazz) {
		Extension ex = extensions.get(clazz.getName());
		if (ex == null) {
			return null; // throw ?
		}
		return clazz.cast(ex);
	}

	/********************************/

	public <T> void addFactory(Class<?> clazz, Factory<T> factory) {
		addFactory(clazz.getName(), factory);
	}

	public <T> void addFactory(String name, Factory<T> factory) {
		FACTORIES.put(name, factory);
	}

	public <T> Factory<T> getFactory(Class<T> clazz) {
		return getFactory(clazz.getName(), clazz);
	}

	@SuppressWarnings("unchecked")
	public <T> Factory<T> getFactory(String name, Class<T> clazz) {
		Factory<?> result = FACTORIES.get(name);
		if (result == null) {
			 throw new RegisterException("Missing factory " + name + " " + clazz);
		}
		return (Factory<T>)result;
	}

	public boolean isFactoryPresent(Class<?> clazz) {
		return FACTORIES.get(clazz.getName()) != null;
	}
	
	public boolean isFactoryPresent(String name) {
		return FACTORIES.get(name) != null;
	}

	/********************************/

	public void addService(Object object) {
		addService(object.getClass().getName(), object);
	}

	public void addService(String name, Object object) {
		SERVICES.put(name, object);
	}

	public <T> T getService(Class<T> clazz) {
		return getService(clazz.getName(), clazz);
	}

	@SuppressWarnings("unchecked")
	public <T> T getService(String name, Class<T> clazz) {
		Object result = SERVICES.get(name);
		if (result == null) {
			 throw new RegisterException("Missing service " + name + " " + clazz);
		}
		return (T)result;
	}
	
	public boolean isServicePresent(Class<?> clazz) {
		return SERVICES.get(clazz.getName()) != null;
	}

	public boolean isServicePresent(String name) {
		return SERVICES.get(name) != null;
	}

	public Factory<CustomErrorHandler> getCustomErrorHandler() {
		return customErrorHandler;
	}

}
