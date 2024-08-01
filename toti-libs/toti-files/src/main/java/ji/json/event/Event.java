package ji.json.event;

public class Event {
	
	private EventType type;
	
	private String name;
	
	private Value<?> value;
	
	private int level;

	public Event(EventType type, String name, Value<?> value, int level) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.level = level;
	}

	public boolean isJsonEnd() {
		return (level == 0 && (type == EventType.OBJECT_END || type == EventType.LIST_END)) || level < 0;
	}
	
	public boolean hasNext() {
		return !isJsonEnd();
	}
	
	public EventType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Value<?> getValue() {
		return value;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s (%s-%s)", name, value, type, level);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Event)) {
			return false;
		}
		Event e = (Event)obj;
		if (e.type != type) {
			return false;
		}
		if (!name.equals(e.name)) {
			return false;
		}
		if (!value.equals(e.value)) {
			return false;
		}
		if (level != e.level) {
			return false;
		}
		return true;
	}
	
}
