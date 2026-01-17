package toti.files.json;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.files.json.event.Event;
import toti.files.json.event.EventType;
import toti.files.json.event.Value;
import toti.files.json.event.ValueType;
import toti.files.json.providers.InputStringProvider;

public class InputJsonStreamTest {
	
	@Test
	public void testReadEmptyString() throws IOException {
		StringBuilder b = new StringBuilder();
		InputStringProvider provider = new InputStringProvider("") {
			@Override
			public void close() throws JsonStreamException {
				b.append("closed");
				super.close();
			}
		};
		try (InputJsonStream stream = new InputJsonStream(provider);) {
			Event e = stream.next();
			assertEquals(new Event(EventType.EMPTY, "", new Value<>("", ValueType.NULL), -1), e);
		}
		assertEquals("closed", b.toString());
	}

	@ParameterizedTest
	@MethodSource("dataNextTestEventTypes")
	public void testNextTestEventTypes(String json, Event[] events) throws IOException {
		StringBuilder b = new StringBuilder();
		InputStringProvider provider = new InputStringProvider(json) {
			@Override
			public void close() throws JsonStreamException {
				b.append("closed");
				super.close();
			}
		};
		try (InputJsonStream stream = new InputJsonStream(provider);) {
			Event e = stream.next();
			assertEquals(new Event(EventType.OBJECT_START, "", new Value<>("", ValueType.NULL), 0), e);
			for (Event expected : events) {
				assertEquals(expected, stream.next());
			}
			e = stream.next();
			assertEquals(new Event(EventType.OBJECT_END, "", new Value<>("", ValueType.NULL), 0), e);
		}
		assertEquals("closed", b.toString());
	}
	
	public static Object[] dataNextTestEventTypes() {
		return new Object[] {
			new Object[] {
				"{}",
				new Event[] {}
			},
			new Object[] {
				"{\"integer\": 123}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "integer", new Value<>(123, ValueType.INTEGER), 1) 
				}
			},
			new Object[] {
				"{\"negative\": -25}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "negative", new Value<>(-25, ValueType.INTEGER), 1)
				}
			},
			new Object[] {
				"{\"double\": 3.456}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "double", new Value<>(3.456, ValueType.DOUBLE), 1)
				}
			},
			new Object[] {
				"{\"bigNumber\": 4.0e+17}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "bigNumber", new Value<>(4.0e+17, ValueType.DOUBLE), 1)
				}
			},
			new Object[] {
				"{\"string\": \"abc\"}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "string", new Value<>("abc", ValueType.STRING), 1)
				}
			},
			new Object[] {
				"{ \"nullable\": null }",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "nullable", new Value<>(null, ValueType.NULL), 1)
				}
			},
			new Object[] {
				"{ \"booleanTrue\": true}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "booleanTrue", new Value<>(true, ValueType.BOOLEAN), 1)
				}
			},
			new Object[] {
				"{\"booleanFalse\": false}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "booleanFalse", new Value<>(false, ValueType.BOOLEAN), 1)
				}
			},
			new Object[] {
				"{\"empty\": \"\"}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "empty", new Value<>("", ValueType.STRING), 1)
				}
			},
			new Object[] {
				"{\"quote\\\"in\\\"key\": \"\"}",
				new Event[] {
					new Event(EventType.OBJECT_ITEM, "quote\\\"in\\\"key", new Value<>("", ValueType.STRING), 1)
				}
			},
			new Object[] {
				"{\"emptyList\": []}",
				new Event[] {
						new Event(EventType.LIST_START, "emptyList", new Value<>("", ValueType.NULL), 1),
						new Event(EventType.LIST_END, "", new Value<>("", ValueType.NULL), 1)
				}
			},
			new Object[] {
				"{\"emptyObject\": {}}",
				new Event[] {
						new Event(EventType.OBJECT_START, "emptyObject", new Value<>("", ValueType.NULL), 1),
						new Event(EventType.OBJECT_END, "", new Value<>("", ValueType.NULL), 1)
				}
			},
			// TODO more use cases
			new Object[] {
				"{\"list\": [ 1 ]}",
				new Event[] {
						new Event(EventType.LIST_START, "list", new Value<>("", ValueType.NULL), 1),
						new Event(EventType.LIST_ITEM, "", new Value<>(1, ValueType.INTEGER), 2),
						new Event(EventType.LIST_END, "", new Value<>("", ValueType.NULL), 1)
				}
			},
			new Object[] {
				"{\"object\": {\"a\": false}}",
				new Event[] {
						new Event(EventType.OBJECT_START, "object", new Value<>("", ValueType.NULL), 1),
						new Event(EventType.OBJECT_ITEM, "a", new Value<>(false, ValueType.BOOLEAN), 2),
						new Event(EventType.OBJECT_END, "", new Value<>("", ValueType.NULL), 1)
				}
			},
			new Object[] {
					"{\"escape\": \"-- \\ and \\\" --\"}",
					new Event[] {
						new Event(EventType.OBJECT_ITEM, "escape", new Value<>("-- \\ and \" --", ValueType.STRING), 1),
					}
				},
			new Object[] {
					"{\"escape\": \"-- \\n --\"}",
					new Event[] {
						new Event(EventType.OBJECT_ITEM, "escape", new Value<>("-- \n --", ValueType.STRING), 1),
					}
				}
		};
	}

	@Test
	public void testNextTestEventTypesWithList() throws IOException {
		StringBuilder b = new StringBuilder();
		InputStringProvider provider = new InputStringProvider("[1, 2, 3]") {
			@Override
			public void close() throws JsonStreamException {
				b.append("closed");
				super.close();
			}
		};
		Event[] events = new Event[] {
				new Event(EventType.LIST_START, "", new Value<>("", ValueType.NULL), 0),
				new Event(EventType.LIST_ITEM, "", new Value<>(1, ValueType.INTEGER), 1),
				new Event(EventType.LIST_ITEM, "", new Value<>(2, ValueType.INTEGER), 1),
				new Event(EventType.LIST_ITEM, "", new Value<>(3, ValueType.INTEGER), 1),
				new Event(EventType.LIST_END, "", new Value<>("", ValueType.NULL), 0),
			};
		try (InputJsonStream stream = new InputJsonStream(provider);) {
			for (Event expected : events) {
				assertEquals(expected, stream.next());
			}
			Event e = stream.next();
			assertEquals(new Event(EventType.EMPTY, "", new Value<>("", ValueType.NULL), -1), e);
		}
		assertEquals("closed", b.toString());
	//	assertEquals("closed", b.toString());
	//	stream.close();
	}
	
	@Test
	public void testNextIfTextIsEmpty() throws IOException {
		String testingJson = "";
		InputStringProvider provider = new InputStringProvider(testingJson);
		InputJsonStream stream = new InputJsonStream(provider);
		assertEquals(EventType.EMPTY, stream.next().getType());
		stream.close();
	}
	
}
