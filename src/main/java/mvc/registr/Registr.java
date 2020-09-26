package mvc.registr;

import java.util.HashMap;
import java.util.Map;

import common.structures.ThrowingSupplier;
import common.structures.Tuple2;

public class Registr {
	
	private final static Registr registr = new Registr();;
	
	public static Registr get() {
		return registr;
	}
	
	private final Map<String, ThrowingSupplier<Object, Throwable>> FACTORIES;
	private final Map<Tuple2<String, Class<?>>, Object> SERVICES;
	
	private Registr() {
		this.FACTORIES = new HashMap<>();
		this.SERVICES = new HashMap<>();
	}
	
	public void addFactory(String name, ThrowingSupplier<Object, Throwable> factory) {
		FACTORIES.put(name, factory);
	}

	public void addFactory(Class<?> clazz, ThrowingSupplier<Object, Throwable> factory) {
		FACTORIES.put(clazz.getName(), factory);
	}
	
	public ThrowingSupplier<Object, Throwable> getFactory(String className) throws Exception {
		ThrowingSupplier<Object, Throwable> result = FACTORIES.get(className);
		if (result == null) {
			throw new RuntimeException("Missing factory " + className);
		}
		return result;
	}

    public void addService(String name, Class<?> clazz, Object object) {
        SERVICES.put(new Tuple2<>(name, clazz), object);
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
