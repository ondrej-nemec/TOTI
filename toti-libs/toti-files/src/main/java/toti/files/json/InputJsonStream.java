package toti.files.json;

import java.io.Closeable;
import java.io.IOException;

import toti.files.json.event.Event;
import toti.files.json.event.EventType;
import toti.files.json.event.ValueParser;
import toti.files.json.providers.InputProvider;
import toti.files.json.providers.InputStringProvider;

public class InputJsonStream implements Closeable {
	
	public static InputJsonStream fromString(String json) {
		return new InputJsonStream(new InputStringProvider(json));
	}
	
	private final static char CHAR_DEFAULT = '\u0000';
	
	private final InputProvider provider;
	
	private char actualCache = CHAR_DEFAULT;
	private char previousChar = CHAR_DEFAULT;
	private int level = 0;
	
	public InputJsonStream(InputProvider provider) {
		this.provider = provider;
	}
	
	public Event next() throws JsonStreamException {
		String name = "";
		String value = "";
		boolean isInQuots = false;
		boolean isWaitingForValue = false;
		boolean isValueQuoted = false;
		boolean isKeyQuoted = false;
		
		char actual = (actualCache == CHAR_DEFAULT) ? provider.getNext() : actualCache;
		while(actual != (char)-1) {
			/*System.out.println(String.format("a: '%s', p: '%s', c: '%s'", actual, previousChar, actualCache));
			System.out.println(String.format("name: '%s', value: '%s'", name, value));
			System.out.println(String.format(
					"Quotes %s, Waiting for value: %s",
					isInQuots, isWaitingForValue
			));
			System.out.println();*/
			/***** if char is in quotes *******/
			if (actual != '"' && isInQuots) {
				if (isWaitingForValue) {
					value += actual;
				} else {
					name += actual;
				}
				//////
					previousChar = actual;
					actual = provider.getNext();
				//////
				continue;
			}
			/******** whitespace chars outside of quotes are ignored **********/
			if (Character.isWhitespace(actual)) {
				//////
					previousChar = actual;
					actual = provider.getNext();
				//////
				continue;
			}
			/*********  cache check  *************/
			if (actualCache != CHAR_DEFAULT) {
				char cache = actualCache;
				actualCache = CHAR_DEFAULT;
				switch (cache) {
					case '}':
						level--;
					//	if (level == 0) {
					//		provider.close();
							//return new Event(EventType.DOCUMENT_END, name, ValueParser.parse(value, isValueQuoted), level);
					//	}
						return new Event(EventType.OBJECT_END, name, ValueParser.parse(value, isValueQuoted), level);
					case ']':
						level--;
					//	if (level == 0) {
					//		provider.close();
					//	}
						return new Event(EventType.LIST_END, name, ValueParser.parse(value, isValueQuoted), level);
				}
				continue;
			}
			/******** chars with special meaning **************/
			switch (actual) {
				case '"':
					if (!isInQuots) {
						isInQuots = true;
						isValueQuoted = isWaitingForValue; // IMPORTANT
						isKeyQuoted = !isWaitingForValue; // IMPORTANT
					} else if (previousChar != '\\') {
						isInQuots = false;
					} else if (isWaitingForValue) {
						value += actual;
					} else {
						name += actual;
					}
					break;
				case '{': 
					/*if (level == 0) {
						return new Event(EventType.DOCUMENT_START, name, ValueParser.parse(value, isValueQuoted), level++);
					}*/
					return new Event(EventType.OBJECT_START, name, ValueParser.parse(value, isValueQuoted), level++);
				case '}':
					actualCache = actual;
					if (!name.isEmpty() && (isValueQuoted || !value.isEmpty())) {
						return new Event(EventType.OBJECT_ITEM, name, ValueParser.parse(value, isValueQuoted), level);
					}
					break;
				case '[': return new Event(EventType.LIST_START, name, ValueParser.parse(value, isValueQuoted), level++);
				case ']':
					actualCache = actual;					
					if (!name.isEmpty()) { // list has not name, value is in name
						return new Event(EventType.LIST_ITEM, value, ValueParser.parse(name, isKeyQuoted), level);
					}
					if (!value.isEmpty()) { // why this happend??
						return new Event(EventType.LIST_ITEM, name, ValueParser.parse(value, isValueQuoted), level);
					}
					break;
				case ',':
					if (isWaitingForValue) {
						return new Event(EventType.OBJECT_ITEM, name, ValueParser.parse(value, isValueQuoted), level);
					}
					if (!value.isEmpty()) {
						return new Event(EventType.LIST_ITEM, name, ValueParser.parse(value, isValueQuoted), level);
					}
					if (!name.isEmpty()) {
						return new Event(EventType.LIST_ITEM, name, ValueParser.parse(name, true), level);
					}
					break;
				case ':': isWaitingForValue = true; break;
				/********* chars outside of quotes without special meaning **********/
				default:
					value += actual;
			}		

			//////
				previousChar = actual;
				actual = (actualCache == CHAR_DEFAULT) ? provider.getNext() : actualCache;
			//////
		}
	//	provider.close();
		return new Event(EventType.EMPTY, name, ValueParser.parse(value, isValueQuoted), -1);
	}

	@Override
	public void close() throws IOException {
		provider.close();
	}
	
}
