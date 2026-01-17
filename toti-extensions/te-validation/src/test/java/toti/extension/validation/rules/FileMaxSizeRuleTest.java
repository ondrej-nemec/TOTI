package toti.extension.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.extension.validation.ValidationItem;
import toti.extension.validation.rules.FileMaxSizeRule;
import toti.lib.tcpip.structures.UploadedFile;

public class FileMaxSizeRuleTest {
	
	@ParameterizedTest
	@MethodSource("dataIsErrorToShow")
	public void testIsErrorToShow(Object value, Integer bond, boolean expected) {
		FileMaxSizeRule rule = new FileMaxSizeRule(null, null);
		assertEquals(expected, rule.isErrorToShow(bond, value));
	}
	
	public static Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { createFile(10), 12, false },
			new Object[] { createFile(10), 10, false },
			new Object[] { createFile(12), 10, true },
		};
	}
	
	private static UploadedFile createFile(int size) {
		return new UploadedFile("fileName", "type", "bom", new byte[size]);
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null, null);
		item.setNewValue("newValue");
		
		FileMaxSizeRule rule = new FileMaxSizeRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
	
}
