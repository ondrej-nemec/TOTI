package toti.lib.files.xml;

import javax.xml.stream.XMLStreamException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class XmlReaderTest {

	@ParameterizedTest
	@MethodSource("dataReadWorks")
	public void testReadWorks(String xml, XmlObject expected) throws XMLStreamException {
		XmlReader reader = new XmlReader();
		assertEquals(expected, reader.read(xml));
	}

	public static Object[] dataReadWorks() {
		return new Object[] {
			new Object[] {
				"""
				<?xml version="1.0" encoding="UTF-8"?>
				<root>
					<name>Test</name>
					<value>123</value>
				</root>
				""",
				new XmlObject("root")
				.addReference(new XmlObject("name").addValue("Test"))
				.addReference(new XmlObject("value").addValue(123))
			},
			new Object[] {
				"""
				<user id="42" active="true">
					<name>Jan</name>
					<role>admin</role>
				</user>
				""",
				new XmlObject("user")
				.addAtribute("id", 42)
				.addAtribute("active", true)
				.addReference(new XmlObject("name").addValue("Jan"))
				.addReference(new XmlObject("role").addValue("admin"))
			},
			new Object[] {
				"""
				<order>
					<customer>
						<name>Petr</name>
						<address>
							<city>Praha</city>
							<zip>11000</zip>
						</address>
					</customer>
				</order>
				""",
				new XmlObject("order")
				.addReference(
					new XmlObject("customer")
					.addReference(new XmlObject("name").addValue("Petr"))
					.addReference(
						new XmlObject("address")
						.addReference(new XmlObject("city").addValue("Praha"))
						.addReference(new XmlObject("zip").addValue(11000))
					)
				)
			},
			new Object[] {
				"""
				<items>
					<item>Apple</item>
					<item>Banana</item>
					<item>Orange</item>
				</items>
				""",
				new XmlObject("items")
				.addReference(new XmlObject("item").addValue("Apple"))
				.addReference(new XmlObject("item").addValue("Banana"))
				.addReference(new XmlObject("item").addValue("Orange"))
			},
			new Object[] {
				"""
				<data>
					<empty />
					<null></null>
				</data>
				""",
				new XmlObject("data")
				.addReference(new XmlObject("empty"))
				.addReference(new XmlObject("null"))
			},
			new Object[] {
				"""
				<text>
					Hello <b>world</b> !
				</text>
				""",
				new XmlObject("text")
				.addValue("Hello ")
				.addReference(new XmlObject("b").addValue("world"))
				.addValue(" !")
			},
			new Object[] {
				"""
				<message>
					&lt;test&gt; &amp; &quot;quotes&quot;
				</message>
				""",
				new XmlObject("message")
				.addValue("<test> & \"quotes\"")
			},
			new Object[] {
				"""
				<ns:book xmlns:ns="http://example.com/book">
					<ns:title>XML Guide</ns:title>
				</ns:book>
				""",
				new XmlObject("book")
				.addReference(new XmlObject("title").addValue("XML Guide"))
			},
			new Object[] {
				"""
				<root>
					<!-- this is comment -->
					<value>10</value>
				</root>
				""",
				new XmlObject("root")
				.addReference(new XmlObject("value").addValue(10))
			}
		};
	}

}
