package toti.register;

import java.util.HashMap;
import java.util.Map;

import ji.common.functions.StackTrace;
import ji.common.structures.ThrowingSupplier;
import ji.common.structures.Tuple2;
import toti.Module;

public class Register {
	
	private final Map<String, Tuple2<ControllerFactory, String>> FACTORIES;
	private final Map<String, Object> SERVICES;
	
	public Register() {
		this.FACTORIES = new HashMap<>();
		this.SERVICES = new HashMap<>();
	}
	
	public void addFactory(String name, ThrowingSupplier<Object, Throwable> factory) {
		// FACTORIES.put(name, (a, b, c, d)->factory.get());
		addFactory(name, (a, b, c, d)->factory.get());
	}

	public void addFactory(Class<?> clazz, ThrowingSupplier<Object, Throwable> factory) {
		// FACTORIES.put(clazz.getName(), (a, b, c, d)->factory.get());
		addFactory(clazz.getName(), (a, b, c, d)->factory.get());
	}

	public void addFactory(String name, ControllerFactory factory) {
		String clazzName = StackTrace.classParent(
			ste->Module.class.isAssignableFrom(Class.forName(ste.getClassName()))
		);
		FACTORIES.put(name, new Tuple2<>(factory, clazzName));
	}

	public void addFactory(Class<?> clazz, ControllerFactory factory) {
		addFactory(clazz.getName(), factory);
	//	FACTORIES.put(clazz.getName(), factory);
	}
	
	public Tuple2<ControllerFactory, String> _getFactory(String className) throws Exception {
		Tuple2<ControllerFactory, String> result = FACTORIES.get(className);
		if (result == null) {
			throw new RuntimeException("Missing factory " + className);
		}
		return result;
	}
	
	public ControllerFactory getFactory(String className) throws Exception {
		return _getFactory(className)._1();
	}

    public void addService(Object object) {
    	addService(object.getClass().getName(), object);
    }

    public void addService(String name, Object object) {
        SERVICES.put(name, object);
    }
    
    public <T> T getService(Class<T> clazz) throws Exception {
    	return getService(clazz.getName(), clazz);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getService(String name, Class<T> clazz) throws Exception {
        Object result = SERVICES.get(name);
        if (result == null) {
             throw new RuntimeException("Missing service " + name + " " + clazz);
        }
        return (T)result;
    }
	
    public boolean isServicePresent(String name) {
    	return SERVICES.get(name) != null;
    }
}
