package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.socketCommunication.http.structures.UploadedFile;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ui.validation.ValidationItem;

@RunWith(JUnitParamsRunner.class)
public class FileMaxSizeRuleTest {
	
	@Test
	@Parameters(method="dataIsErrorToShow")
	public void testIsErrorToShow(Object value, Integer bond, boolean expected) {
		FileMaxSizeRule rule = new FileMaxSizeRule(null, null);
		assertEquals(expected, rule.isErrorToShow(bond, value));
	}
	
	public Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { createFile(10), 12, false },
			new Object[] { createFile(10), 10, false },
			new Object[] { createFile(12), 10, true },
		};
	}
	
	private UploadedFile createFile(int size) {
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