package toti.extension.ui.backend;

import java.sql.SQLException;

public interface TransactionListener<S> {

	default void onTransactionStart(Object id, S item) throws SQLException {}

	default void onTransactionEnd(Object id, S item) throws SQLException {}
	
}
