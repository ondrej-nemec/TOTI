package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.socketCommunication.http.structures.UploadedFile;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ui.validation.ValidationItem;

@RunWith(JUnitParamsRunner.class)
public class FileAllowedTypeTest {

	@Test
	@Parameters(method="dataIsErrorToShow")
	public void testIsErrorToShow(Object value, boolean expected) {
		FileAllowedTypesRule rule = new FileAllowedTypesRule(null, null);
		assertEquals(expected, rule.isErrorToShow(Arrays.asList("type1", "type2"), value));
	}
	
	public Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { createFile("type1", "xxx"), true },
			new Object[] { createFile("xxx", "type1"), false },
			new Object[] { createFile("type1", "type1"), false },
		};
	}
	
	private UploadedFile createFile(String type, String bom) {
		return new UploadedFile("fileName", type, bom, new byte[10]);
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("origin", null, null);
		item.setNewValue("newValue");
		
		FileAllowedTypesRule rule = new FileAllowedTypesRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
	
}
