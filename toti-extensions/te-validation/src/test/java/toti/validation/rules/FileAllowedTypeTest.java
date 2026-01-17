package toti.validation.rules;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.tcpip.structures.UploadedFile;
import toti.validation.ValidationItem;

public class FileAllowedTypeTest {

	@ParameterizedTest
	@MethodSource("dataIsErrorToShow")
	public void testIsErrorToShow(Object value, boolean expected) {
		FileAllowedTypesRule rule = new FileAllowedTypesRule(null, null);
		assertEquals(expected, rule.isErrorToShow(Arrays.asList("type1", "type2"), value));
	}
	
	public static Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { createFile("type1", "xxx"), true },
			new Object[] { createFile("xxx", "type1"), false },
			new Object[] { createFile("type1", "type1"), false },
		};
	}
	
	private static UploadedFile createFile(String type, String bom) {
		return new UploadedFile("fileName", type, bom, new byte[10]);
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null, null);
		item.setNewValue("newValue");
		
		FileAllowedTypesRule rule = new FileAllowedTypesRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
	
}
