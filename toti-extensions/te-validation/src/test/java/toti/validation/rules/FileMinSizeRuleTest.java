package toti.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.tcpip.structures.UploadedFile;
import toti.validation.ValidationItem;

public class FileMinSizeRuleTest {
	
	@ParameterizedTest
	@MethodSource("dataIsErrorToShow")
	public void testIsErrorToShow(Object value, Integer bond, boolean expected) {
		FileMinSizeRule rule = new FileMinSizeRule(null, null);
		assertEquals(expected, rule.isErrorToShow(bond, value));
	}
	
	public static Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { createFile(10), 12, true },
			new Object[] { createFile(10), 10, false },
			new Object[] { createFile(12), 10, false },
		};
	}
	
	private static UploadedFile createFile(int size) {
		return new UploadedFile("fileName", "type", "bom", new byte[size]);
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null, null);
		item.setNewValue("newValue");
		
		FileMinSizeRule rule = new FileMinSizeRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}

}
