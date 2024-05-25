package toti.validation.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.http.http.structures.UploadedFile;
import toti.validation.ValidationItem;

@RunWith(JUnitParamsRunner.class)
public class FileMinSizeRuleTest {
	
	@Test
	@Parameters(method="dataIsErrorToShow")
	public void testIsErrorToShow(Object value, Integer bond, boolean expected) {
		FileMinSizeRule rule = new FileMinSizeRule(null, null);
		assertEquals(expected, rule.isErrorToShow(bond, value));
	}
	
	public Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { createFile(10), 12, true },
			new Object[] { createFile(10), 10, false },
			new Object[] { createFile(12), 10, false },
		};
	}
	
	private UploadedFile createFile(int size) {
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
