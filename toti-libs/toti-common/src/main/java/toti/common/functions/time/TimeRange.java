package toti.common.functions.time;

import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;

import toti.common.structures.DictionaryValue;

public class TimeRange<V> {

	private final ZonedDateTime from;
	// inclusive
	private final ZonedDateTime to;
	private final V value;
	
	public TimeRange(ZonedDateTime from, ZonedDateTime to, V value) {
		this.from = from;
		this.to = to;
		this.value = value;
	}

	public ZonedDateTime getFrom() {
		return from;
	}

	public ZonedDateTime getTo() {
		return to;
	}

	public V getValue() {
		return value;
	}

	public boolean includes(Temporal temporal) {
		ZonedDateTime time = new DictionaryValue(temporal).getDateTimeZone();
		int startCompare = from.compareTo(time);
		if (startCompare > 0) {
			return false;
		}
		if (to != null) {
			return time.compareTo(to) <= 0;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TimeRange [from=" + from + ", to=" + to + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TimeRange<?> other = (TimeRange<?>) obj;
		return Objects.equals(from, other.from) && Objects.equals(to, other.to) && Objects.equals(value, other.value);
	}

}
