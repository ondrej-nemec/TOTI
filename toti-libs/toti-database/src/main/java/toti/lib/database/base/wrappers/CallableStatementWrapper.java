package toti.lib.database.base.wrappers;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import toti.lib.database.base.support.SqlQueryProfiler;

public class CallableStatementWrapper extends PreparedStatementWrapper implements CallableStatement {

	private final CallableStatement statement;
	
	public CallableStatementWrapper(CallableStatement statement, String id, SqlQueryProfiler profiler) {
		super(statement, id, profiler);
		this.statement = statement;
	}
	
	/******/

	@Override
	public void setURL(String parameterName, URL val) throws SQLException {
		profiler.addParam(parameterName, val);
		statement.setURL(parameterName, val);
		// profiler.executed(ID);
	}

	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		profiler.addParam(parameterName, null);
		statement.setNull(parameterName, sqlType);
	//	profiler.executed();
	}

	@Override
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setBoolean(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setByte(String parameterName, byte x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setByte(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setShort(String parameterName, short x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setShort(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setInt(String parameterName, int x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setInt(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setLong(String parameterName, long x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setLong(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setFloat(String parameterName, float x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setFloat(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setDouble(String parameterName, double x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setDouble(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setBigDecimal(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setString(String parameterName, String x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setString(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setBytes(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setDate(String parameterName, Date x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setDate(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setTime(String parameterName, Time x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setTime(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setTimestamp(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		statement.setAsciiStream(parameterName, x, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		statement.setBinaryStream(parameterName, x, length);
	}

	@Override
	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setObject(parameterName, x, targetSqlType, scale);
	//	profiler.executed();
	}

	@Override
	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setObject(parameterName, x, targetSqlType);
	//	profiler.executed();
	}

	@Override
	public void setObject(String parameterName, Object x) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setObject(parameterName, x);
	//	profiler.executed();
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		statement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setDate(parameterName, x, cal);
	//	profiler.executed();
	}

	@Override
	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setTime(parameterName, x, cal);
	//	profiler.executed();
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		profiler.addParam(parameterName, x);
		statement.setTimestamp(parameterName, x, cal);
	//	profiler.executed();
	}

	@Override
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		profiler.addParam(parameterName, null);
		statement.setNull(parameterName, sqlType, typeName);
	//	profiler.executed();
	}
	
	/***********/

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		statement.registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		statement.registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		statement.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		statement.registerOutParameter(parameterName, sqlType);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		statement.registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		statement.registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
	public boolean wasNull() throws SQLException {
		return statement.wasNull();
	}

	@Override
	public String getString(int parameterIndex) throws SQLException {
		return statement.getString(parameterIndex);
	}

	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return statement.getBoolean(parameterIndex);
	}

	@Override
	public byte getByte(int parameterIndex) throws SQLException {
		return statement.getByte(parameterIndex);
	}

	@Override
	public short getShort(int parameterIndex) throws SQLException {
		return statement.getShort(parameterIndex);
	}

	@Override
	public int getInt(int parameterIndex) throws SQLException {
		return statement.getInt(parameterIndex);
	}

	@Override
	public long getLong(int parameterIndex) throws SQLException {
		return statement.getLong(parameterIndex);
	}

	@Override
	public float getFloat(int parameterIndex) throws SQLException {
		return statement.getFloat(parameterIndex);
	}

	@Override
	public double getDouble(int parameterIndex) throws SQLException {
		return statement.getDouble(parameterIndex);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return statement.getBigDecimal(parameterIndex, scale);
	}

	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return statement.getBytes(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex) throws SQLException {
		return statement.getDate(parameterIndex);
	}

	@Override
	public Time getTime(int parameterIndex) throws SQLException {
		return statement.getTime(parameterIndex);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return statement.getTimestamp(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		return statement.getObject(parameterIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return statement.getBigDecimal(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
		return statement.getObject(parameterIndex, map);
	}

	@Override
	public Ref getRef(int parameterIndex) throws SQLException {
		return statement.getRef(parameterIndex);
	}

	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {
		return statement.getBlob(parameterIndex);
	}

	@Override
	public Clob getClob(int parameterIndex) throws SQLException {
		return statement.getClob(parameterIndex);
	}

	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		return statement.getArray(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return statement.getDate(parameterIndex, cal);
	}

	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return statement.getTime(parameterIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return statement.getTimestamp(parameterIndex, cal);
	}

	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		return statement.getURL(parameterIndex);
	}

	@Override
	public String getString(String parameterName) throws SQLException {
		return statement.getString(parameterName);
	}

	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		return statement.getBoolean(parameterName);
	}

	@Override
	public byte getByte(String parameterName) throws SQLException {
		return statement.getByte(parameterName);
	}

	@Override
	public short getShort(String parameterName) throws SQLException {
		return statement.getShort(parameterName);
	}

	@Override
	public int getInt(String parameterName) throws SQLException {
		return statement.getInt(parameterName);
	}

	@Override
	public long getLong(String parameterName) throws SQLException {
		return statement.getLong(parameterName);
	}

	@Override
	public float getFloat(String parameterName) throws SQLException {
		return statement.getFloat(parameterName);
	}

	@Override
	public double getDouble(String parameterName) throws SQLException {
		return statement.getDouble(parameterName);
	}

	@Override
	public byte[] getBytes(String parameterName) throws SQLException {
		return statement.getBytes(parameterName);
	}

	@Override
	public Date getDate(String parameterName) throws SQLException {
		return statement.getDate(parameterName);
	}

	@Override
	public Time getTime(String parameterName) throws SQLException {
		return statement.getTime(parameterName);
	}

	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return statement.getTimestamp(parameterName);
	}

	@Override
	public Object getObject(String parameterName) throws SQLException {
		return statement.getObject(parameterName);
	}

	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return statement.getBigDecimal(parameterName);
	}

	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return statement.getObject(parameterName, map);
	}

	@Override
	public Ref getRef(String parameterName) throws SQLException {
		return statement.getRef(parameterName);
	}

	@Override
	public Blob getBlob(String parameterName) throws SQLException {
		return statement.getBlob(parameterName);
	}

	@Override
	public Clob getClob(String parameterName) throws SQLException {
		return statement.getClob(parameterName);
	}

	@Override
	public Array getArray(String parameterName) throws SQLException {
		return statement.getArray(parameterName);
	}

	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return statement.getDate(parameterName, cal);
	}

	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return statement.getTime(parameterName, cal);
	}

	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return statement.getTimestamp(parameterName, cal);
	}

	@Override
	public URL getURL(String parameterName) throws SQLException {
		return statement.getURL(parameterName);
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		return statement.getRowId(parameterIndex);
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		return statement.getRowId(parameterName);
	}

	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		statement.setRowId(parameterName, x);
	}

	@Override
	public void setNString(String parameterName, String value) throws SQLException {
		statement.setNString(parameterName, value);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		statement.setNCharacterStream(parameterName, value, length);
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		statement.setNClob(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		statement.setClob(parameterName, reader, length);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		statement.setBlob(parameterName, inputStream, length);
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		statement.setNClob(parameterName, reader, length);
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		return statement.getNClob(parameterIndex);
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		return statement.getNClob(parameterName);
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		statement.setSQLXML(parameterName, xmlObject);
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return statement.getSQLXML(parameterIndex);
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return statement.getSQLXML(parameterName);
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {
		return statement.getNString(parameterIndex);
	}

	@Override
	public String getNString(String parameterName) throws SQLException {
		return statement.getNString(parameterName);
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return statement.getNCharacterStream(parameterIndex);
	}

	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return statement.getNCharacterStream(parameterName);
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return statement.getCharacterStream(parameterIndex);
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return statement.getCharacterStream(parameterName);
	}

	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		statement.setBlob(parameterName, x);
	}

	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		statement.setClob(parameterName, x);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		statement.setAsciiStream(parameterName, x, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
		statement.setBinaryStream(parameterName, x, length);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		statement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
		statement.setAsciiStream(parameterName, x);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		statement.setBinaryStream(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		statement.setCharacterStream(parameterName, reader);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		statement.setNCharacterStream(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader) throws SQLException {
		statement.setClob(parameterName, reader);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		statement.setBlob(parameterName, inputStream);
	}

	@Override
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		statement.setNClob(parameterName, reader);
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return statement.getObject(parameterIndex, type);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return statement.getObject(parameterName, type);
	}

}
