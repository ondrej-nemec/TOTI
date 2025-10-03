package ji.querybuilder.enums;

public class ColumnType {
	
	public enum Type {
		BOOLEAN,
		INT,
		FLOAT,
		CHAR,
		STRING,
		TEXT,
		TIME,
		DATE,
		DATETIME,
		DATETIME_ZONED
	}

	public static ColumnType bool() {
		return new ColumnType(Type.BOOLEAN);
	}
	
	public static ColumnType integer() {
		return new ColumnType(Type.INT);
	}
	
	public static ColumnType floatType() {
		return new ColumnType(Type.FLOAT);
	}
	
	public static ColumnType doubleType() {
		return new ColumnType(Type.FLOAT);
	}

	public static ColumnType charType(int size) {
		return new ColumnType(Type.CHAR, size);
	}
	
	public static ColumnType text() {
		return new ColumnType(Type.TEXT);
	}
	
	public static ColumnType string(int size) {
		return new ColumnType(Type.STRING, size);
	}
	
	public static ColumnType time() {
		return new ColumnType(Type.TIME, null);
	}
	
	public static ColumnType time(int size) {
		return new ColumnType(Type.TIME, size);
	}
	
	public static ColumnType date() {
		return new ColumnType(Type.DATE);
	}
	
	public static ColumnType datetime() {
		return new ColumnType(Type.DATETIME);
	}
	
	public static ColumnType datetime(int size) {
		return new ColumnType(Type.DATETIME, size);
	}
	
	public static ColumnType datetimeZoned() {
		return new ColumnType(Type.DATETIME_ZONED, null);
	}
	
	public static ColumnType datetimeZoned(int size) {
		return new ColumnType(Type.DATETIME_ZONED, size);
	}
	
	private Integer size;
	
	private final Type type;
	
	private ColumnType(Type type) {
		this.type = type;
	}
	
	private ColumnType(Type type, Integer size) {
		this.type = type;
		this.size = size;
	}
	
	public Type getType() {
		return type;
	}
	
	public Integer getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "ColumnType [size=" + size + ", type=" + type + "]";
	}
}
