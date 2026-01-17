package toti.common.structures;

@FunctionalInterface
public interface ThrowingCallback<E extends Throwable> {

	void call() throws E;
	
}
