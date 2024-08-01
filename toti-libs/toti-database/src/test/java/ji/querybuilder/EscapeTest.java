package ji.querybuilder;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class EscapeTest {

	@Test
	@Parameters(method = "dataEscape")
	public void testEscape(String sql, String expected) {
		assertEquals(expected, Escape.escape(sql));
	}
	
	public Object[] dataEscape() {
		return new Object[] {
			new Object[] { null, "null" },
			new Object[] { false, "false" },
			new Object[] { Boolean.valueOf(true), "true" },
			new Object[] { 1, "1" },
			new Object[] { Double.valueOf(123.4), "123.4" },
			new Object[] { 'c', "'c'" },
			new Object[] { (byte)42, "42" },
			new Object[] { "", "''" },
			new Object[] { "some text", "'some text'" },
			new Object[] { Arrays.asList("a", "b", "c"), "'a','b','c'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18), "'2021-10-23 19:18'" },
			new Object[] { "not-escaped", "'not-escaped'" },
			new Object[] { "single'quote", "'single''quote'" }
		};
	}
}
