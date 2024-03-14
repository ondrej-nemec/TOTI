package toti.ui.backend;

public interface TransactionListener<S> {

	default void onTransactionStart(Object id, S item) {}

	default void onTransactionEnd(Object id, S item) {}
	
}
