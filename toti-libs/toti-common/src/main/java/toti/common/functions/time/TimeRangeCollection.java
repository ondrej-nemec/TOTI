package toti.common.functions.time;

import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import toti.common.structures.DictionaryValue;

public class TimeRangeCollection<V> {
	
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
			if (obj instanceof TimeRangeCollection.Interval) {
				TimeRangeCollection<?>.Interval i = (TimeRangeCollection<?>.Interval)obj;
				return Objects.equals(start, i.start) && Objects.equals(end, i.end);
			}
			return false;
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
			TimeRangeCollection<?>.Value other = (TimeRangeCollection<?>.Value) obj;
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

	private final Function<V, Temporal> getTime;
	
	public TimeRangeCollection(Function<V, Temporal> getTime) {
		this(Arrays.asList(), getTime);
	}

	public TimeRangeCollection(Collection<V> data, Function<V, Temporal> getTime) {
		this.getTime = getTime;
		this.data = new TreeMap<>(new Comparator<Interval>() {
			public int compare(Interval o1, Interval o2) {
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
			}
		});
		for (V row : data) {
			add(getTime.apply(row), row);
		}
	}

	public void add(V object) {
		add(getTime.apply(object), object);
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

	public TimeRange<V> get(Temporal time) {
		Value v = data.get(new Interval(getTime(time), true));
		if (v == null) {
			return null;
		}
		return new TimeRange<>(v.interval.start, v.interval.end, v.value);
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
		TimeRangeCollection<?> other = (TimeRangeCollection<?>) obj;
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
