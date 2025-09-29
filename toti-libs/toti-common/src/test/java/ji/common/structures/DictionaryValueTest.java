package ji.common.structures;

import static org.junit.Assert.*;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class DictionaryValueTest {

	// TODO test mapper?
	
	
	@Test
	@Parameters(method = "dataPrimitives")
	public void testPrimitives(Object value, Class<?> clazz, Object expected) {
		DictionaryValue dictionaryValue = new DictionaryValue(value);
		assertEquals(expected, dictionaryValue.getValue(clazz));
	}
	
	public Object[] dataPrimitives() {
		return new Object[] {
			new Object[] {false, Boolean.class, false},
			new Object[] {false, boolean.class, false},
			new Object[] {"true", Boolean.class, true},
			new Object[] {"true", boolean.class, true},
			new Object[] {"on", boolean.class, true},
			new Object[] {"1", Boolean.class, true},
			new Object[] {42, int.class, 42},
			new Object[] {"42", int.class, 42},
			new Object[] {42, Integer.class, 42},
			new Object[] {"42", Integer.class, 42},
			new Object[] {42, double.class, 42.0},
			new Object[] {"42", double.class, 42.0},
			new Object[] {42, Double.class, 42.0},
			new Object[] {"42", Double.class, 42.0},
			/********/
			new Object[] {"true", Boolean.class, true},
			new Object[] {"42", Byte.class, (byte)42},
			new Object[] {"42", Short.class, (short)42},
			new Object[] {"42", Integer.class, 42},
			new Object[] {"42", Long.class, 42L},
			new Object[] {"42", Float.class, 42f},
			new Object[] {"42", Double.class, 42.0},
			new Object[] {"C", Character.class, 'C'},
			new Object[] {"C", char.class, 'C'},
			new Object[] {"string", String.class, "string"},
			/********/
			new Object[] {"null", String.class, "null"},
			new Object[] {null, String.class, null},
			/*******/
		};
	}

	@Test
	@Parameters(method = "dataTimestamps")
	public void testTimestamps(String message, Object value, Class<?> clazz, Object expected) {
		DictionaryValue dictionaryValue = new DictionaryValue(value);
		dictionaryValue.withZoneId(ZoneId.of("+01:00"));
		assertEquals(message, expected, dictionaryValue.getValue(clazz));
	}
	
	public Object[] dataTimestamps() {
		ZoneId zoneId = ZoneId.of("+01:00");
		long epoch = 1681127100; // ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId).toEpochSecond()
		return new Object[] {
			// date
			new Object[] {
				"LocalDate from Epoch", epoch,
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			new Object[] {
				"LocalDate from LocalDate object", LocalDate.of(2023, 4, 10),
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			new Object[] {
				"LocalDate from LocalDate string", "2023-04-10",
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			new Object[] {
				"LocalDate from LocalTime object", LocalTime.of(12, 45),
				LocalDate.class, LocalDate.of(1970, 1, 1)
			},
			new Object[] {
				"LocalDate from LocalTime string", "12:45:00",
				LocalDate.class, LocalDate.of(1970, 1, 1)
			},
			new Object[] {
				"LocalDate from LocalDateTime object", LocalDateTime.of(2023, 4, 10, 12, 45),
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			new Object[] {
				"LocalDate from LocalDateTime string 1", "2023-04-10T12:45:00",
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			new Object[] {
				"LocalDate from LocalDateTime string 2", "2023-04-10 12:45:00",
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			new Object[] {
				"LocalDate from ZonedDateTimed object", ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId),
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			new Object[] {
				"LocalDate from ZonedDateTimed string 1", "2023-04-10T12:45:00+01:00",
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			new Object[] {
				"LocalDate from ZonedDateTimed string 2", "2023-04-10 12:45:00+01:00",
				LocalDate.class, LocalDate.of(2023, 4, 10)
			},
			// time
			new Object[] {
				"LocalTime from Epoch", epoch,
				LocalTime.class, LocalTime.of(12, 45)
			},
			new Object[] {
				"LocalTime from LocalTime object", LocalTime.of(12, 45),
				LocalTime.class, LocalTime.of(12, 45)
			},
			new Object[] {
				"LocalTime from LocalTime string", "12:45",
				LocalTime.class, LocalTime.of(12, 45)
			},
			new Object[] {
				"LocalTime from LocalTime string 2", "12:45:35.123456789",
				LocalTime.class, LocalTime.of(12, 45, 35, 123_456_789)
			},
			new Object[] {
				"LocalTime from LocalTime string 2", "12:45:35.123",
				LocalTime.class, LocalTime.of(12, 45, 35, 123_000_000)
			},
			new Object[] {
				"LocalTime from LocalDate object", LocalDate.of(2023, 4, 10),
				LocalTime.class, LocalTime.of(0, 0)
			},
			new Object[] {
				"LocalTime from LocalDate string", "2023-04-10",
				LocalTime.class, LocalTime.of(0, 0)
			},
			new Object[] {
				"LocalTime from LocalDateTime object", LocalDateTime.of(2023, 4, 10, 12, 45),
				LocalTime.class, LocalTime.of(12, 45)
			},
			new Object[] {
				"LocalTime from LocalDateTime string 1", "2023-04-10T12:45",
				LocalTime.class, LocalTime.of(12, 45)
			},
			new Object[] {
				"LocalTime from LocalDateTime string 2", "2023-04-10 12:45",
				LocalTime.class, LocalTime.of(12, 45)
			},
			new Object[] {
				"LocalTime from ZonedDateTime object", ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId),
				LocalTime.class, LocalTime.of(12, 45)
			},
			new Object[] {
				"LocalTime from ZonedDateTime string 1", "2023-04-10 12:45:00+01:00",
				LocalTime.class, LocalTime.of(12, 45)
			},
			new Object[] {
				"LocalTime from ZonedDateTime string 2", "2023-04-10 12:45:00+01:00",
				LocalTime.class, LocalTime.of(12, 45)
			},
			// datetime
			new Object[] {
				"LocalDateTime from Epoch", epoch, 
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 12, 45)
			},
			new Object[] {
				"LocalDateTime from LocalDateTime object", LocalDateTime.of(2023, 4, 10, 12, 45), 
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 12, 45)
			},
			new Object[] {
				"LocalDateTime from LocalDateTime string 1", "2023-04-10T12:45:00", 
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 12, 45)
			},
			new Object[] {
				"LocalDateTime from LocalDateTime string 2", "2023-04-10 12:45:00", 
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 12, 45)
			},
			new Object[] {
				"LocalDateTime from LocalTime object", LocalTime.of(12, 45),
				LocalDateTime.class, LocalDateTime.of(1970,  1, 1, 12, 45)
			},
			new Object[] {
				"LocalDateTime from LocalTime string", "12:45",
				LocalDateTime.class, LocalDateTime.of(1970, 1, 1, 12, 45)
			},
			new Object[] {
				"LocalDateTime from LocalDate object", LocalDate.of(2023, 4, 10),
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 0, 0)
			},
			new Object[] {
				"LocalDateTime from LocalDate string", "2023-04-10",
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 0, 0)
			},
			new Object[] {
				"LocalDateTime from ZonedDateTime object", ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId), 
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 12, 45)
			},
			new Object[] {
				"LocalDateTime from ZonedDateTime string 1", "2023-04-10T12:45:00+01:00", 
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 12, 45)
			},
			new Object[] {
				"LocalDateTime from ZonedDateTime string 2", "2023-04-10 12:45:00+01:00", 
				LocalDateTime.class, LocalDateTime.of(2023, 4, 10, 12, 45)
			},
			// zoned date time
			new Object[] {
				"ZonedDateTime from Epoch", epoch, 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from ZonedDateTime object", ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId), 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from ZonedDateTime string 1", "2023-04-10T12:45:00+01:00", 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from ZonedDateTime string 2", "2023-04-10 12:45:00+01:00", 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from ZonedDateTime string 3", "2023-04-10 12:45:35.123456789+05:00", 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 35, 123_456_789, ZoneId.of("+5"))
			},
			new Object[] {
				"ZonedDateTime from LocalTime object", LocalTime.of(12, 45),
				ZonedDateTime.class, ZonedDateTime.of(1970, 1, 1, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from LocalTime string", "12:45",
				ZonedDateTime.class, ZonedDateTime.of(1970, 1, 1, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from LocalDate object", LocalDate.of(2023, 4, 10),
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 0, 0, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from LocalDate string", "2023-04-10",
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 0, 0, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from LocalDateTime object", LocalDateTime.of(2023, 4, 10, 12, 45), 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from LocalDateTime string 1", "2023-04-10T12:45:00", 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from LocalDateTime string 2", "2023-04-10 12:45:00", 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 0, 0, zoneId)
			},
			new Object[] {
				"ZonedDateTime from ZonedDateTime string Zone 1", "2023-04-10 12:45:35.123456789+00:00", 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 35, 123_456_789, ZoneId.of("Z"))
			},
			/*new Object[] {
				"ZonedDateTime from ZonedDateTime string Zone 2", "2023-04-10 12:45:35.123456789[UTC]", 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 35, 123_456_789, ZoneId.of("UTC"))
			},*/
			new Object[] {
				"ZonedDateTime from ZonedDateTime string Zone 3", "2023-04-10 12:45:35.123456789Z", 
				ZonedDateTime.class, ZonedDateTime.of(2023, 4, 10, 12, 45, 35, 123_456_789, ZoneId.of("Z"))
			},
		};
	}

	@Test
	@Parameters(method = "dataListsAndMaps")
	public void testListsAndMaps(String message, Object value, Class<?> clazz, Object expected) {
		DictionaryValue dictionaryValue = new DictionaryValue(value);
		assertEquals(message, expected, dictionaryValue.getValue(clazz));
	}

	public Object[] dataListsAndMaps() {
		// set, collection, array, listdictionary mezi sebou
		// map, mapdictionary, sorted map na list, collection, array, listdictionary
		// map, mapdictionary, sorted map mezi sebou
		// TODO improve
		return new Object[] {
			/** LIST **/
			// set
			new Object[] {
				"Set to Set", new ListInit<>().add("a").add("b").toSet(),
				Set.class, new ListInit<>().add("a").add("b").toSet()
			},
			new Object[] {
				"List to Set", new ListInit<>().add("a").add("b").add("b").toList(),
				Set.class, new ListInit<>().add("a").add("b").toSet()
			},
			new Object[] {
				"Array to Set", new String[] {"a", "b", "b"},
				Set.class, new ListInit<>().add("a").add("b").toSet()
			},
			new Object[] {
				"ListDictionary to Set", new ListDictionary(Arrays.asList("a", "b", "b")),
				Set.class, new ListInit<>().add("a").add("b").toSet()
			},
			// list
			new Object[] {
				"List to List", new ListInit<>().add("a").add("b").toList(),
				List.class, new ListInit<>().add("a").add("b").toList()
			},
			new Object[] {
				"Set to List", new ListInit<>().add("a").add("b").toSet(),
				List.class, new ListInit<>().add("a").add("b").toList()
			},
			new Object[] {
				"Array to List", new String[] {"a", "b"},
				List.class, new ListInit<>().add("a").add("b").toList()
			},
			new Object[] {
				"ListDictionary to List", new ListDictionary(Arrays.asList("a", "b")),
				List.class, new ListInit<>().add("a").add("b").toList()
			},
			// array - on another place
			// listDictionary
			new Object[] {
				"ListDictionary to ListDictionary", new ListDictionary(Arrays.asList("a", "b")),
				ListDictionary.class, new ListDictionary(Arrays.asList("a", "b"))
			},
			new Object[] {
				"List to ListDictionary", new ListInit<>().add("a").add("b").toList(),
				ListDictionary.class, new ListDictionary(Arrays.asList("a", "b"))
			},
			new Object[] {
				"Set to ListDictionary", new ListInit<String>().add("a").add("b").toSet(),
				ListDictionary.class, new ListDictionary(Arrays.asList("a", "b"))
			},
			new Object[] {
				"Array to ListDictionary", new String[] {"a", "b"},
				ListDictionary.class, new ListDictionary(Arrays.asList("a", "b"))
			},
			/** MAPS **/
			// map
			new Object[] {
				"Map to Map", MapInit.create().append("a", "1").append("b", "2").toMap(),
				Map.class, MapInit.create().append("a", "1").append("b", "2").toMap()
			},
			new Object[] {
				"MapDictionary to Map", MapDictionary.hashMap().put("a", "1").put("b", "2"),
				Map.class, MapInit.create().append("a", "1").append("b", "2").toMap()
			},
			new Object[] {
				"SortedMap to Map", new SortedMap<>().append("a", "1").append("b", "2"),
				Map.class, MapInit.create().append("a", "1").append("b", "2").toMap()
			},
			// mapDictionary
			new Object[] {
				"MapDictionary to MapDictionary", MapDictionary.hashMap().put("a", "1").put("b", "2"),
				MapDictionary.class, MapDictionary.hashMap().put("a", "1").put("b", "2")
			},
			new Object[] {
				"Map to MapDictionary", MapInit.create().append("a", "1").append("b", "2").toMap(),
				MapDictionary.class, MapDictionary.hashMap().put("a", "1").put("b", "2")
			},
			new Object[] {
				"SortedMap to MapDictionary", new SortedMap<>().append("a", "1").append("b", "2"),
				MapDictionary.class, MapDictionary.hashMap().put("a", "1").put("b", "2")
			},
			// sortedMap
			new Object[] {
				"SortedMap to SortedMap", new SortedMap<>().append("a", "1").append("b", "2"),
				SortedMap.class, new SortedMap<>().append("a", "1").append("b", "2")
			},
			new Object[] {
				"Map to SortedMap", MapInit.create().append("a", "1").append("b", "2").toMap(),
				SortedMap.class, new SortedMap<>().append("a", "1").append("b", "2")
			},
			new Object[] {
				"MapDictonary to SortedMap", MapDictionary.hashMap().put("a", "1").put("b", "2"),
				SortedMap.class, new SortedMap<>().append("a", "1").append("b", "2")
			},
			/** MAPS to LISTS **/
			new Object[] {
				"MapDictionary to List", MapDictionary.hashMap().put("a", "1").put("b", "2"),
				List.class, Arrays.asList("1", "2")
			},
			new Object[] {
				"Map to List", MapInit.create().append("a", "1").append("b", "2").toMap(),
				List.class, Arrays.asList("1", "2")
			},
			new Object[] {
				"SortedMap to List", new SortedMap<>().append("a", "1").append("b", "2"),
				List.class, Arrays.asList("1", "2")
			},
		};
	}

	@Test
	@Parameters(method = "dataArray")
	public void testArray(String message, Object value, String[] expected) {
		DictionaryValue dictionaryValue = new DictionaryValue(value);
		assertArrayEquals(message, expected, dictionaryValue.getArray());
	}
	
	public Object[] dataArray() {
		return new Object[] {
			new Object[] {
				"Array to Array",
				new String[] {"a", "b"}, 
				new String[] {"a", "b"}
			},
			new Object[] {
				"List to Array", 
				new ListInit<>().add("a").add("b").toList(), 
				new String[] {"a", "b"}
			},
			new Object[] {
				"Set to Array",
				new ListInit<>().add("a").add("b").toSet(), 
				new String[] {"a", "b"}
			},
			new Object[] {
				"ListDictionary to Array", 
				new ListDictionary(Arrays.asList("a", "b")),
				new String[] {"a", "b"}
			},
		};
	}
	
}
