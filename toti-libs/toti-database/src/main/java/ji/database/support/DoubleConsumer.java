package ji.database.support;

import java.sql.SQLException;

import toti.lib.common.structures.ThrowingSupplier;

public interface DoubleConsumer<T> extends ThrowingSupplier<T, SQLException> {

	T get() throws SQLException;
	
}
