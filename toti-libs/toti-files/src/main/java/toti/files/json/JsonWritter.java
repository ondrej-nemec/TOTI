package toti.files.json;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.time.DateFormatUtils;

import toti.files.json.providers.OutputStringProvider;
import toti.lib.common.functions.Mapper;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.ListDictionary;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.SortedMap;
import toti.lib.common.structures.Tuple2;
import toti.lib.common.structures.Tuple3;

public class JsonWritter {
	
	public String write(Object json) {
		OutputStringProvider provider = new OutputStringProvider();
		try (OutputJsonStream stream = new OutputJsonStream(provider);) {
			write(stream, json);
		} catch (IOException e) {
			// ignored - no reason for it
		}
		return provider.getJson();
	}

	public void write(OutputJsonStream stream, Object json) throws JsonStreamException {
		writeObject(stream, json, null);
	}

	public void writeJson(OutputJsonStream stream, Map<String, Object> json) throws JsonStreamException {
		//stream.startDocument();
		stream.writeObjectStart();
		for (String key : json.keySet()) {
			writeObject(stream, json.get(key), key);
		}
		//stream.endDocument();
		stream.writeObjectEnd();
	}
	
	/******************/
	
	private void write(OutputJsonStream stream, Map<String, Object> objects, String name) throws JsonStreamException {
		if (name == null) {
			stream.writeObjectStart();
		} else {
			stream.writeObjectStart(name);
		}
		for (Object key : objects.keySet()) {
			writeObject(stream, objects.get(key), key + "");
		}
		stream.writeObjectEnd();
	}

	private void writeSortedMap(OutputJsonStream stream, SortedMap<String, Object> objects, String name) throws JsonStreamException {
		if (name == null) {
			 stream.writeObjectStart();
		} else {
			 stream.writeObjectStart(name);
		}
		objects.forEach((key, value)->{
			 writeObject(stream, value, key + "");
		}, JsonStreamException.class);
		stream.writeObjectEnd();
	}
	
	private void write(OutputJsonStream stream, Iterable<Object> list, String name) throws JsonStreamException {
		if (name == null) {
			stream.writeListStart();
		} else {
			stream.writeListStart(name);
		}
		for (Object o : list) {
			writeObject(stream, o, null);
		}
		stream.writeListEnd();
	}
	
	
	@SuppressWarnings("unchecked")
	private void writeObject(OutputJsonStream stream, Object value, String name) throws JsonStreamException {
		if (value == null) {
			if (name == null) {
				stream.writeListValue(value);
			} else {
				stream.writeObjectValue(name, value);
			}
		} else if (value instanceof SortedMap<?, ?>) {
			writeSortedMap(stream, SortedMap.class.cast(value), name);
		} else if (value instanceof MapDictionary<?>) {
			write(stream, MapDictionary.class.cast(value).toMap(), name);
		} else if (value instanceof ListDictionary) {
			write(stream, ListDictionary.class.cast(value).toList(), name);
		} else if (value instanceof Iterable) {
			write(stream, (Iterable<Object>)value, name);
		} else if (value instanceof Map) {
			write(stream, (Map<String, Object>)value, name);
		} else if (value instanceof Jsonable) {
			writeObject(stream, ((Jsonable)value).toJson(), name);
		} else if (value instanceof Tuple2<?, ?> || value instanceof Tuple3<?, ?, ?>) {
			writeObject(stream, Mapper.get().serialize(value), name);
		} else if (value instanceof Date) {
			String date = DateFormatUtils.format(Date.class.cast(value).getTime(), "yyyy-MM-dd HH:mm:ss.SSS");
			if (name == null) {
				stream.writeListValue(date);
			} else {
				stream.writeObjectValue(name, date);
			}
		} else if (value instanceof DictionaryValue) {
			writeObject(stream, DictionaryValue.class.cast(value).getValue(), name);
		} else if (value instanceof Optional) {
			writeObject(stream, Optional.class.cast(value).orElse(null), name);
		} else {
			if (name == null) {
				stream.writeListValue(value);
			} else {
				stream.writeObjectValue(name, value);
			}
		}	
	}
	
}
