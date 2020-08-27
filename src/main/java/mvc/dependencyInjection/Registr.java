package mvc.dependencyInjection;

import java.util.function.Supplier;

import controllers.TestController;

public class Registr {

	public static Supplier<Object> getFactory(String className) throws Exception {
		return ()->{
			return new TestController(); // TODO
		};
	}
	
	
}
