package ji.json.event;

public class Value<T> {

	private final T value;
	
	private final ValueType type;
	
	public Value(T value, ValueType type) {
		this.value = value;
		this.type = type;
	}
	
	protected ValueType getType() {
		return type;
	}
	
	public T get() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Value)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Value<T> v = (Value<T>)obj;
		if (!type.equals(v.type)) {
			return false;
		}
		if (type == ValueType.NULL) { //  && value == null && v.value == null - if type == null value no matter
			return true;
		}
		if (!value.equals(v.value)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		if (type == ValueType.STRING) {
			return String.format("\"%s\"", value);
		}
		return value + "";
	}
}
