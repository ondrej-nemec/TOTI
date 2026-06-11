package toti.extension.validation.rules;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.tcpip.structures.UploadedFile;

public class FileAllowedTypeTest {

	@ParameterizedTest
	@MethodSource
	public void testCheck(Object rawValue, Set<Object> expectedErrors) {
		// rule use raw value only and not change value
		RuleTest.test(
			onError->new FileAllowedTypesRule(Arrays.asList("type1", "type2"), onError),
			rawValue, "not a file", // parsed not use
			expectedErrors, true, "not a file"
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] { null, RuleTest.empty() },
			new Object[] { createFile("type1", "xxx"), RuleTest.filled() },
			new Object[] { createFile("xxx", "type1"), RuleTest.empty() },
			new Object[] { createFile("type1", "type1"), RuleTest.empty() },
		};
	}
	
	private static UploadedFile createFile(String type, String bom) {
		return new UploadedFile("fileName", type, bom, new byte[10]);
	}

}
