package toti.lib.database.base.support;

import java.sql.Connection;
import java.sql.SQLException;

import toti.lib.common.structures.ThrowingFunction;

public interface ConnectionFunction<T> extends ThrowingFunction<Connection, T, SQLException> {

	T apply(Connection connection) throws SQLException;
	
}
