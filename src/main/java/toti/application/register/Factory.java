package toti.application.register;

public interface Factory<T> {

	T create() throws Throwable;
	
}
