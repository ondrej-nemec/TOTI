package toti.answers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class HeadersTest {
	
	// TODO isAsyncReques?
	
	@Test
	@Parameters(method="dataGetHeader")
	public void testGetHeader(String name, String expected) {
		Map<String, List<Object>> map = new HashMap<>();
		map.put("some-header", Arrays.asList("value", "value2"));
		Headers headers = new Headers(map);
		assertEquals(expected, headers.getHeader(name));
	}
	
	public Object[] dataGetHeader() {
		return new Object[] {
			new Object[] { "Some-Header", "value" },
			new Object[] { "some-header", "value" },
			new Object[] { "somevalue", null }
		};
	}
	
	@Test
	public void testAddHeaderAddNotExistingHeader() {
		Headers headers = new Headers();
		headers.addHeader("HeaderName", "HeaderValue");
		
		Map<String, List<Object>> expected = new HashMap<>();
		expected.put("headername", Arrays.asList("HeaderValue"));
		
		assertEquals(expected, headers.getHeaders());
	}
	
	@Test
	public void testAddHeaderAddExistingHeader() {
		List<Object> existingHeader = new LinkedList<>();
		existingHeader.add("first");
		Map<String, List<Object>> map = new HashMap<>();
		map.put("existing", existingHeader);
		Headers headers = new Headers();
		headers.setHeaders(map);
		
		headers.addHeader("Existing", "Second");
		
		Map<String, List<Object>> expected = new HashMap<>();
		expected.put("existing", Arrays.asList("first", "Second"));
		
		assertEquals(expected, headers.getHeaders());
	}
	
	@Test
	@Parameters(method="dataGetCookieValue")
	public void testGetCookieValue(String cookie, String cookieName, Optional<String> expected) {
		Map<String, List<Object>> map = new HashMap<>();
		if (cookie != null) {
			map.put("cookie", Arrays.asList(cookie));
		}
		Headers headers = new Headers(map);
		assertEquals(expected, headers.getCookieValue(cookieName));
	}
	
	public Object[] dataGetCookieValue() {
		return new Object[] {
			new Object[] {
				null, "auth", Optional.empty()
			},
			new Object[] {
				"", "auth", Optional.empty()
			},
			new Object[] {
				"key=value", "auth", Optional.empty()
			},
			new Object[] {
				"auth=val", "auth", Optional.of("val")
			},
			new Object[] {
				"key=value;auth=val", "auth", Optional.of("val")
			},
			new Object[] {
				"key=value; auth=val", "auth", Optional.of("val")
			},
			new Object[] {
				"key=value;auth =val", "auth", Optional.of("val")
			},
			new Object[] {
				"key=value;auth=", "auth", Optional.of("")
			},
			new Object[] {
				"key=value;auth", "auth", Optional.empty()
			},
			new Object[] {
				"key=value;auth=null", "auth", Optional.empty()
			},
			new Object[] {
				"key=value;auth= null", "auth", Optional.empty()
			},
			new Object[] {
				"key=value;auth=null ", "auth", Optional.empty()
			},
			new Object[] {
				"key=value;auth=NULL", "auth", Optional.empty()
			},
			new Object[] {
				"key=value;auth=val1=val2", "auth", Optional.of("val1=val2")
			}
		};
	}
	
}
