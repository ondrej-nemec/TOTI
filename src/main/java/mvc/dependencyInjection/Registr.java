package mvc.dependencyInjection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Registr {
	
	private final static Map<String, Supplier<Object>> FACTORIES = new HashMap<>();
	
	public static void addFactory(String name, Supplier<Object> factory) {
		FACTORIES.put(name, factory);
	}

	public static void addFactory(Class<?> clazz, Supplier<Object> factory) {
		FACTORIES.put(clazz.getName(), factory);
	}
	
	public static Supplier<Object> getFactory(String className) throws Exception {
		Supplier<Object> result = FACTORIES.get(className);
		if (result == null) {
			throw new RuntimeException("Missing factory " + className);
		}
		return result;
	}
	
	
}
