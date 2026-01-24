package toti.lib.files.xml;


import javax.xml.stream.XMLStreamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class XmlWritterTest {

	@ParameterizedTest
	@MethodSource("dataWriteWorks")
	public void testWriteWorks(XmlObject data, String expected) throws XMLStreamException {
		XmlWritter writter = new XmlWritter();
		assertEquals(expected, writter.write(data));
	}

	public static Object[] dataWriteWorks() {
		return new Object[] {
			new Object[] {
				new XmlObject("root")
				.addReference(new XmlObject("name").addValue("Test"))
				.addReference(new XmlObject("value").addValue(123)),
				"<?xml version=\"1.0\" ?>"
				+ "<root>"
					+ "<name>Test</name>"
					+ "<value>123</value>"
				+ "</root>"
			},
			new Object[] {
				new XmlObject("user")
				.addAtribute("id", 42)
				.addAtribute("active", true)
				.addReference(new XmlObject("name").addValue("Jan"))
				.addReference(new XmlObject("role").addValue("admin")),
				"<?xml version=\"1.0\" ?>"
				+ "<user active=\"true\" id=\"42\">"
					+ "<name>Jan</name>"
					+ "<role>admin</role>"
				+ "</user>"
			},
			new Object[] {
				new XmlObject("order")
				.addReference(
					new XmlObject("customer")
					.addReference(new XmlObject("name").addValue("Petr"))
					.addReference(
						new XmlObject("address")
						.addReference(new XmlObject("city").addValue("Praha"))
						.addReference(new XmlObject("zip").addValue(11000))
					)
				),
				"<?xml version=\"1.0\" ?>"
				+ "<order>"
					+ "<customer>"
						+ "<name>Petr</name>"
						+ "<address>"
							+ "<city>Praha</city>"
							+ "<zip>11000</zip>"
						+ "</address>"
					+ "</customer>"
				+ "</order>"
			},
			new Object[] {
				new XmlObject("items")
				.addReference(new XmlObject("item").addValue("Apple"))
				.addReference(new XmlObject("item").addValue("Banana"))
				.addReference(new XmlObject("item").addValue("Orange")),
				"<?xml version=\"1.0\" ?>"
				+ "<items>"
					+ "<item>Apple</item>"
					+ "<item>Banana</item>"
					+ "<item>Orange</item>"
				+ "</items>"
			},
			new Object[] {
				new XmlObject("data")
				.addReference(new XmlObject("empty"))
				.addReference(new XmlObject("null")),
				"<?xml version=\"1.0\" ?>"
				+ "<data>"
					+ "<empty></empty>"
					+ "<null></null>"
				+ "</data>"
			},
			new Object[] {
				new XmlObject("text")
				.addValue("Hello ")
				.addReference(new XmlObject("b").addValue("world"))
				.addValue(" !"),
				"<?xml version=\"1.0\" ?>"
				+ "<text>"
					+ "Hello <b>world</b> !"
				+ "</text>"
			},
			new Object[] {
				new XmlObject("message")
				.addValue("<test> & \"quotes\""),
				"<?xml version=\"1.0\" ?>"
				+ "<message>"
					+ "&lt;test&gt; &amp; \"quotes\""
				+ "</message>"
			}/*,
			new Object[] {
				new XmlObject("book")
				.addReference(new XmlObject("title").addValue("XML Guide")),
				"""
				<ns:book xmlns:ns="http://example.com/book">
					<ns:title>XML Guide</ns:title>
				</ns:book>
				"""
			},
			new Object[] {
				new XmlObject("root")
				.addReference(new XmlObject("value").addValue(10)),
				"""
				<root>
					<!-- this is comment -->
					<value>10</value>
				</root>
				"""
			}*/
		};
	}
}
