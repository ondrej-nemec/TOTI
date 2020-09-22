package mvc.registr;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import common.structures.Tuple2;

public class Registr {
	
	private final static Registr registr = new Registr();;
	
	public static Registr get() {
		return registr;
	}
	
	private final Map<String, Supplier<Object>> FACTORIES;
	private final Map<Tuple2<String, Class<?>>, Object> SERVICES;
	
	private Registr() {
		this.FACTORIES = new HashMap<>();
		this.SERVICES = new HashMap<>();
	}
	
	public void addFactory(String name, Supplier<Object> factory) {
		FACTORIES.put(name, factory);
	}

	public void addFactory(Class<?> clazz, Supplier<Object> factory) {
		FACTORIES.put(clazz.getName(), factory);
	}
	
	public Supplier<Object> getFactory(String className) throws Exception {
		Supplier<Object> result = FACTORIES.get(className);
		if (result == null) {
			throw new RuntimeException("Missing factory " + className);
		}
		return result;
	}

    public void addService(String name, Class<?> clazz, Object object) {
        SERVICES.put(new Tuple2<>(name, object.getClass()), object);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getService(String name, Class<T> clazz) throws Exception {
        Object result = SERVICES.get(new Tuple2<>(name, clazz));
        if (result == null) {
             throw new RuntimeException("Missing service " + name + " " + clazz);
        }
        return (T)result;
    }
	
}
