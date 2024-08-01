package ji.database.wrappers;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import ji.database.support.SqlQueryProfiler;

public class PreparedStatementWrapper extends StatementWrapper implements PreparedStatement {

	private final PreparedStatement statement;
	
	public PreparedStatementWrapper(PreparedStatement statement, String id, SqlQueryProfiler profiler) {
		super(statement, id, profiler);
		this.statement = statement;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		profiler.execute(ID);
		ResultSet rs = statement.executeQuery();
		profiler.executed(ID, rs.getClass().getName());
		return rs;
	}

	@Override
	public int executeUpdate() throws SQLException {
		profiler.execute(ID);
		int res = statement.executeUpdate();
		profiler.executed(ID, res);
		return res;
	}

	@Override
	public boolean execute() throws SQLException {
		profiler.execute(ID);
		boolean res = statement.execute();
		profiler.executed(ID, res);
		return res;
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setObject(parameterIndex, x);
	//	profiler.executed();
	}
	
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		profiler.addParam(ID, x);
		statement.setObject(parameterIndex, x, targetSqlType);
	//	profiler.executed();
	}
	
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		profiler.addParam(ID, x);
		statement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	//	profiler.executed();
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		profiler.addParam(ID, x);
		statement.setDate(parameterIndex, x, cal);
	//	profiler.executed();
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		profiler.addParam(ID, x);
		statement.setTime(parameterIndex, x, cal);
	//	profiler.executed();
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		profiler.addParam(ID, x);
		statement.setTimestamp(parameterIndex, x, cal);
	//	profiler.executed();
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		profiler.addParam(ID, null);
		statement.setNull(parameterIndex, sqlType, typeName);
	//	profiler.executed();
	}
	
	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		profiler.addParam(ID, null);
		statement.setNull(parameterIndex, sqlType);
	//	profiler.executed();
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setBoolean(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setByte(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setShort(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setInt(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setLong(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setFloat(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setDouble(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setBigDecimal(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setString(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setBytes(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setDate(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setTime(parameterIndex, x);
	//	profiler.executed();
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		profiler.addParam(ID, x);
		statement.setTimestamp(parameterIndex, x);
	//	profiler.executed();
	}
	
	/************/

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		statement.setAsciiStream(parameterIndex, x, length);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		statement.setUnicodeStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		statement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void clearParameters() throws SQLException {
		statement.clearParameters();
	}

	@Override
	public void addBatch() throws SQLException {
		statement.addBatch();
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		statement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		statement.setRef(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		statement.setBlob(parameterIndex, x);
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		statement.setClob(parameterIndex, x);
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		statement.setArray(parameterIndex, x);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return statement.getMetaData();
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		statement.setURL(parameterIndex, x);
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return statement.getParameterMetaData();
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		statement.setRowId(parameterIndex, x);
	}

	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		statement.setNString(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		statement.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		statement.setNClob(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		statement.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		statement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		statement.setNCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		statement.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		statement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		statement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		statement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		statement.setAsciiStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		statement.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		statement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		statement.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		statement.setClob(parameterIndex, reader);	
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		statement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		statement.setNClob(parameterIndex, reader);
	}
}
