package toti.lib.common.functions.time;

import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import toti.lib.common.structures.DictionaryValue;

public final class TimeRange<V> {
	
	class Interval {
		final ZonedDateTime start;
		ZonedDateTime end;
		final boolean isSearch;
		public Interval(ZonedDateTime start, boolean isSearch) {
			this.start = start;
			this.isSearch = isSearch;
		}
		@Override
		public String toString() {
			if (isSearch) {
				return String.format("TimeRange.Interval-Search(%s)", start);
			}
			return String.format("TimeRange.Interval[%s, %s]", start, end);
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TimeRange.Interval) {
				TimeRange<?>.Interval i = (TimeRange<?>.Interval)obj;
				return Objects.equals(start, i.start) && Objects.equals(end, i.end);
			}
			return false;
		}
		@Override
		public int hashCode() {
			int hash = 7;
			hash = 29 * hash + Objects.hashCode(this.start);
			hash = 29 * hash + Objects.hashCode(this.end);
			hash = 29 * hash + (this.isSearch ? 1 : 0);
			return hash;
		}
	}
	
	class Value {
		V value;
		Interval interval;
		public Value(Interval interval, V value) {
			this.value = value;
			this.interval = interval;
		}
		@Override
		public String toString() {
			return value == null ? "NULL" : value.toString();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
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
			TimeRange<?>.Value other = (TimeRange<?>.Value) obj;
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
		}
	}

	private final TreeMap<Interval, Value> data;

	public static <V> TimeRange<V> create(Collection<V> data, Function<V, Temporal> getTime) {
		TimeRange<V> timeRange = new TimeRange<>();
		for (V row : data) {
			timeRange.add(getTime.apply(row), row);
		}
		return timeRange;
	}

	public TimeRange() {
		this.data = new TreeMap<>((o1, o2)->{
			// o1 > o2 = 1
			// o1 < o2 = -1
			int startCompare = o1.start.compareTo(o2.start);
			if (!o1.isSearch && !o2.isSearch) {
				return startCompare;
			}
			if (o1.end == null && o2.end == null) {
				if (o1.isSearch) {
					return startCompare > 0 ? 0 : startCompare;
				}
				if (o2.isSearch) {
					return startCompare < 0 ? 0 : startCompare;
				}
				// return startCompare; // neverHappends
			}
			if (o1.end != null && o2.end != null) {
				// optimalization
				// if both ends are set, intervals are on theirs places
				return startCompare;
			}
			if (startCompare > 0 && o2.end != null) {
				int compare = o1.start.compareTo(o2.end);
				return compare < 0 ? 0 : compare;
			}
			if (startCompare < 0 && o1.end != null) {
				int compare = o2.start.compareTo(o1.end);
				return compare < 0 ? 0 : compare;
			}
			return startCompare;
		});
	}

	public void add(Temporal time, V object) {
		Interval actual = new Interval(getTime(time), false);
		
		Interval sameOrHigher = data.ceilingKey(actual);
		Interval sameOrLess = data.floorKey(actual);
		
		if (sameOrLess != null) {
			if (sameOrLess.start.equals(actual.start)) {
				actual = sameOrLess;
			} else {
				sameOrLess.end = actual.start.minusNanos(1);
			}
		}
		if (sameOrHigher != null) {
			if (sameOrHigher.start.equals(actual.start)) {
				actual = sameOrHigher;
			} else {
				actual.end = sameOrHigher.start.minusNanos(1);
			}
		}
		data.put(actual, new Value(actual, object));
	}

	public Timestone<V> get(Temporal time) {
		Value v = data.get(new Interval(getTime(time), true));
		if (v == null) {
			return null;
		}
		return new Timestone<>(v.interval.start, v.interval.end, v.value);
	}

	private ZonedDateTime getTime(Temporal t) {
		return new DictionaryValue(t).getDateTimeZone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
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
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TimeRangeCollection [" + data + "]";
	}

}
