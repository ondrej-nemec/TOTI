package toti.lib.translator;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class XmlMessagesFileTest {

	@Test
	public void testGetMessages() throws Exception {
		Map<Object, Object> expected = new HashMap<>();
		expected.put("a", "A");
		expected.put("a.b", "B2");
		expected.put("x.y.z1", "XYZ1");
		expected.put("x.y.z2", "XYZ2");
		expected.put("x.z", "Z");

		XmlMessagesFile file = new XmlMessagesFile();
		Map<Object, Object> actual = file.getMessages("MessagesFile/messages.xml");

		assertEquals(expected, actual);
	}

}
