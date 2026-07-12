package toti.lib.tcpip.parsers;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.structures.RequestParameters;

public class UrlencodeTest {
	
	private final String url = 
			   "maplist%5Ba%5D%5B%5D=value-maplist-a-1&maplist%5Ba%5D%5B%5D=value-maplist-a-2"
			+ "&maplist%5Bb%5D%5B%5D=value-maplist-b-1&maplist%5Bb%5D%5B%5D=value-maplist-b-2"
			+ "&list%5B%5D=value-list-1&list%5B%5D=value-list-2&list%5B%5D=value-list-3"
			+ "&map%5Ba%5D=value-map-a&map%5Bb%5D=value-map-b";
	private final RequestParameters parameters = 
			(RequestParameters) new RequestParameters()
				.put(
					"list", 
					Arrays.asList(
						"value-list-1", 
						"value-list-2", 
						"value-list-3"
					)
				)
				.put(
					"map",
					new MapInit<>()
					.append("a", "value-map-a")
					.append("b", "value-map-b")
					.toMap()
				)
				.put(
					"maplist",
					new MapInit<>()
					.append(
						"a", 
						Arrays.asList(
							"value-maplist-a-1", 
							"value-maplist-a-2"
						)
					)
					.append(
						"b", 
						Arrays.asList(
							"value-maplist-b-1", 
							"value-maplist-b-2"
						)
					)
					.toMap()
				);
	
	@Test
	public void testDecode() throws UnsupportedEncodingException {
		Urlencode encode = getEncode();
		assertEquals(parameters.toString(), encode.decode(url).toString());
		assertEquals(parameters, encode.decode(url));
	}
	
	@Test
	public void testEncode() {
		Urlencode encode = getEncode();
		assertEquals(url, encode.encode(parameters));
	}
	
	private Urlencode getEncode() {
		return new Urlencode(new Payload(), mock(StreamReader.class));
	}
	
}
