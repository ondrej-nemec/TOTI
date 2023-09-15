package toti.url;

public interface ParamConsumer {

	void accept(Class<?> clazz, String paramName, boolean isRequestParam);
	
}
