package toti.extension.validation.rules;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.tcpip.structures.UploadedFile;

public class FileMinSizeRuleTest {
	
	@ParameterizedTest
	@MethodSource
	public void testCheck(int minSize, Object rawValue, Set<Object> expectedErrors) {
		RuleTest.test(
			onError->new FileMinSizeRule(minSize, onError), rawValue, "not a file", // parsed value is not used
			expectedErrors, true, "not a file"
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] { 12, null, RuleTest.empty() },
			new Object[] { 12, createFile(10), RuleTest.filled() },
			new Object[] { 10, createFile(10), RuleTest.empty() },
			new Object[] { 10, createFile(12), RuleTest.empty() },
		};
	}
	
	private static UploadedFile createFile(int size) {
		return new UploadedFile("fileName", "type", "bom", new byte[size]);
	}

}
