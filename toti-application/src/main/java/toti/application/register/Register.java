package toti.application.register;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import ji.common.structures.ObjectBuilder;
import ji.common.structures.Tuple2;
import ji.socketCommunication.http.HttpMethod;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.answers.request.AuthMode;
import toti.application.Module;
import toti.extensions.CustomExceptionResponse;

public class Register {
	
	private final Map<String, Factory<?>> FACTORIES;
	private final Map<String, Object> SERVICES;
	
	private final Map<String, Tuple2<Factory<?>, Module>> CONTROLLERS;

	private final Param root;
	private final ObjectBuilder<Module> module;
	
	private CustomExceptionResponse customExceptionResponse = null;
	
	public Register(Param root, ObjectBuilder<Module> module) {
		this.FACTORIES = new HashMap<>();
		this.SERVICES = new HashMap<>();
		this.CONTROLLERS = new HashMap<>();
		this.root = root;
		this.module = module;
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
		
		Param moduleParam = getParam(module.get().getName(), root);
		Param controllerParam = getParam(clazz.getAnnotation(Controller.class).value(), moduleParam);
		for (Method m : clazz.getMethods()) {
    		if (m.isAnnotationPresent(Action.class)) {
    			HttpMethod[] methods = getHttpMethods(m);
    			Action actionAnotation = getActionAnnotation(m);
    			String actionPart = actionAnotation.path();
    			Param actionParam = getParam(actionPart, controllerParam);
    			
    			/*List<Class<?>> methodParameters = new LinkedList<>();
    			for (Parameter p : m.getParameters()) {
    				methodParameters.add(p.getType());
    			}*/
    			MappedAction action = new MappedAction(
    				module.get().getName(), clazz.getName(), m.getName(),
    				m, factory,// methodParameters,
    				getSecurityMode(m), methods
    			);
    			for (HttpMethod method : methods) {
    				actionParam.addAction(method, action);
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
	
	private AuthMode getSecurityMode(Method m) {
		AuthMode securityMode = AuthMode.NO_TOKEN;
		if (m.isAnnotationPresent(Secured.class)) {
			securityMode = m.getAnnotation(Secured.class).value();
		}
		return securityMode;
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
	
	// router
	public MappedAction createRoutedAction(Class<?> controller, Method method) {
		Tuple2<Factory<?>, Module> factory = getController(controller);
		return MappedAction.router(
			factory._2().getName(),
			method,
			factory._1(),
			getSecurityMode(method),
			getHttpMethods(method)
		);
	}
	
	// link
	public Module getModuleForClass(Class<?> controller) {
		return getController(controller)._2();
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
    
    /***************************/
	
	public CustomExceptionResponse getCustomExceptionResponse() {
		return customExceptionResponse;
	}

	public void setCustomExceptionResponse(CustomExceptionResponse customExceptionResponse) {
		this.customExceptionResponse = customExceptionResponse;
	}
}
