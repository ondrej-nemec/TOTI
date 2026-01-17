package toti.lib.tcpip.parsers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;

import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.MapInit;
import toti.lib.common.tests.Log4j2LoggerTestImpl;
import toti.lib.tcpip.client.Exchange;
import toti.lib.tcpip.client.ExchangeFactory;
import toti.lib.tcpip.client.ExchangeRequest;
import toti.lib.tcpip.client.ExchangeResponse;
import toti.lib.tcpip.client.FirstLine;
import toti.lib.tcpip.client.Headers;
import toti.lib.tcpip.client.Protocol;
import toti.lib.tcpip.enums.HttpMethod;
import toti.lib.tcpip.enums.StatusCode;
import toti.lib.tcpip.structures.RequestParameters;
import toti.lib.tcpip.structures.UploadedFile;

public class ExchangeFactoryTest {
	
	private class BufferedInputStremMock extends BufferedInputStream {
		private final String data;
		int index = 0;
		public BufferedInputStremMock(String data) {
			super(mock(InputStream.class));
			this.data = data;
		}
		@Override
		public synchronized int read() throws IOException {
			if (index == data.length()) {
				return -1;
			}
			return data.charAt(index++);
		}
		@Override
		public synchronized int available() throws IOException {
			return data.length() - index;
		}
	}
	
	private class BufferedOutputStreamMock extends BufferedOutputStream {
		private final ByteArrayOutputStream data = new ByteArrayOutputStream();
		public BufferedOutputStreamMock() {
			super(mock(OutputStream.class));
		}
		@Override 
		public void write(int b) throws IOException {
			if (b == '\n') {
				data.write('\r');
			}
			data.write(b);
		}
		@Override
		public void write(byte[] b) throws IOException {
			if (Arrays.equals("\n".getBytes(), b)) {
				data.write('\r');
			}
			data.write(b);
		}
		
		public ByteArrayOutputStream get() { 
			return data; 
		}
	}
	
	@ParameterizedTest
	@MethodSource("getData")
	public void testWriteRequest(String filename, Map<String, List<Object>> headers, Function<Exchange, Object> setBody) throws IOException {
		ExchangeFactory parser = createParser();
		// String expected = Text.get().read(b->ReadText.get().asString(b), getClass().getResource("/parser/requests/" + filename));
		ByteArrayOutputStream expected = readFile("/parser/requests/" + filename);
		try (BufferedOutputStreamMock bos = new BufferedOutputStreamMock()) {
			ExchangeRequest request = new ExchangeRequest(HttpMethod.POST, "/some/url", Protocol.HTTP_1_1);
			request.setHeaders(headers);
			
			//request.setBody(body, type);
			setBody.apply(request);
			
			parser.write(request, bos);

			try {
				assertTrue(Arrays.equals(expected.toByteArray(), bos.get().toByteArray()));
			} catch (Error e) {
				assertEquals(toString(expected.toByteArray()), toString(bos.get().toByteArray()));
				throw e;
			}
		}
	}

	@ParameterizedTest
	@MethodSource("getData")
	public void testWriteResponse(String filename, Map<String, List<Object>> headers, Function<Exchange, Object> setBody) throws IOException {
		// BodyType type, Object body
		ExchangeFactory parser = createParser();
		ByteArrayOutputStream expected = readFile("/parser/responses/" + filename);
		try (BufferedOutputStreamMock bos = new BufferedOutputStreamMock()) {
			ExchangeResponse response = new ExchangeResponse(StatusCode.OK, Protocol.HTTP_1_1);
			response.setHeaders(headers);
			
			setBody.apply(response);
			// response.setBody(body, type);

			parser.write(response, bos);
			
			try {
				assertTrue(Arrays.equals(expected.toByteArray(), bos.get().toByteArray()));
			} catch (Error e) {
				assertEquals(toString(expected.toByteArray()), toString(bos.get().toByteArray()));
				throw e;
			}
		}
	}

	private String toString(byte[] bytes) {
		return new String(bytes)
			.replace("\r", "\r")
			//.replace("\r", "")
			.replace("\n", "\n\n");
	}

	@ParameterizedTest
	@MethodSource("getData")
	public void testReadRequest(String filename, Object headers, Function<Exchange, Object> setBody) throws IOException {
		ExchangeFactory parser = createParser();
		try (InputStream is = getClass().getResourceAsStream("/parser/requests/" + filename);
				BufferedInputStream bis = new BufferedInputStream(is)) {
			ExchangeRequest request= parser.readRequest(bis);
			assertEquals(HttpMethod.POST, request.getMethod());
			assertEquals("/some/url", request.getUri());
			assertEquals(Protocol.HTTP_1_1, request.getProtocol());
			
			assertEquals(headers, request.getHeaders());
			assertBody(setBody.apply(null), request);
			// assertEquals(setBody.apply(null), request.getBody());
		}
	}

	@ParameterizedTest
	@MethodSource("getData")
	public void testReadResponse(String filename, Map<String, List<Object>> headers, Function<Exchange, Object> setBody) throws IOException {
		ExchangeFactory parser = createParser();
		try (InputStream is = getClass().getResourceAsStream("/parser/responses/" + filename);
				BufferedInputStream bis = new BufferedInputStream(is)) {
			ExchangeResponse response = parser.readResponse(bis);
			assertEquals(StatusCode.OK, response.getCode());
			assertEquals(Protocol.HTTP_1_1, response.getProtocol());
			
			assertEquals(headers, response.getHeaders());

			assertBody(setBody.apply(null), response);
		}
	}
	
	@Test
	public void testParseRequestUrlParameters() throws IOException {
		ExchangeFactory parser = createParser();
		try (BufferedInputStream bis = new BufferedInputStremMock("PUT /my/uri?param=val&another=aaa HTTP/1.1")) {
			ExchangeRequest request= parser.readRequest(bis);
			assertEquals(HttpMethod.PUT, request.getMethod());
			assertEquals("/my/uri?param=val&another=aaa", request.getUri());
			assertEquals(Protocol.HTTP_1_1, request.getProtocol());
			
			assertEquals("/my/uri", request.getPlainUri());
			assertEquals(
				MapDictionary.hashMap()
				.put("param", "val")
				.put("another", "aaa"),
				request.getQueryParameters()
			);
			
			assertEquals(new HashMap<>(), request.getHeaders());
			assertNull(request.getBody());
		}
	}

	private static ByteArrayOutputStream fileContent() {
		ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
		for (int b : new int[] {
				137, 80, 78, 71, 13, 10, 26, 10,
				0, 0, 0, 13, 73, 72, 68, 
				82, 0, 0, 0, 32, 0, 0, 0, 32, 8, 2, 0, 0, 0, 252, 
				24, 237, 163, 0, 0, 0, 1, 115, 82, 71, 66, 0, 174, 206, 28, 
				233, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, 177, 143, 11, 252, 
				97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, 195, 0, 
				0, 14, 195, 1, 199, 111, 168, 100, 0, 0, 0, 210, 73, 68, 65, 
				84, 72, 75, 237, 148, 49, 14, 194, 48, 12, 69, 29, 142, 208, 133, 
				137, 9, 113, 156, 114, 3, 78, 199, 13, 224, 56, 168, 19, 19, 11, 
				87, 8, 30, 220, 246, 211, 196, 73, 149, 200, 145, 42, 245, 45, 253, 
				170, 37, 127, 187, 249, 169, 243, 222, 147, 37, 7, 121, 154, 177, 220, 
				192, 221, 63, 162, 136, 252, 237, 40, 170, 130, 217, 0, 91, 35, 89, 
				155, 239, 233, 34, 138, 168, 123, 191, 68, 141, 84, 125, 34, 110, 141, 
				221, 153, 240, 141, 24, 104, 227, 51, 137, 210, 26, 202, 55, 88, 76, 
				138, 96, 201, 60, 69, 173, 12, 18, 81, 169, 12, 107, 249, 6, 97, 
				34, 39, 176, 84, 117, 209, 180, 115, 78, 25, 20, 128, 54, 225, 90, 
				219, 255, 217, 237, 6, 89, 204, 13, 34, 41, 114, 206, 137, 226, 171, 
				176, 34, 99, 231, 71, 47, 138, 104, 184, 62, 69, 141, 252, 25, 96, 
				107, 68, 179, 193, 214, 8, 218, 52, 60, 3, 109, 124, 38, 90, 210, 
				198, 103, 176, 180, 199, 52, 203, 108, 144, 72, 100, 180, 20, 38, 114, 
				162, 105, 138, 218, 94, 52, 11, 182, 158, 34, 162, 31, 130, 213, 94, 
				217, 43, 94, 199, 18, 0, 0, 0, 0, 73, 69, 78, 68, 174, 66, 
				96, 130}) {
			binaryData.write(b);
		}
		return binaryData;
	}
	
	public static Object[] getData() {
		ByteArrayOutputStream binaryData = fileContent();
		
		return new Object[] {
			new Object[] {
				"body-empty.txt",
				new MapInit<String, Object>()
				.append("content-length", Arrays.asList("0"))
				.append("content-type", Arrays.asList("application/x-www-form-urlencoded"))
				.append("some-header", Arrays.asList("my header value"))
				.toMap(),
				createFunction((ex)->{
					if (ex != null) {
						ex.setBodyUrlencoded(new RequestParameters());
					}
					return new RequestParameters();
				})
			},
			new Object[] {
				"body-plain-text.txt",
				new MapInit<String, Object>()
				.append("some-header", Arrays.asList("my header value"))
				.append("content-type", Arrays.asList("plain/text"))
				.append("content-length", Arrays.asList("59"))
				.toMap(),
				createFunction((ex)->{
					String res = "Some UTF-8 text: ěščř Сайн уу 你好 أأهلاً";
					if (ex == null) {
						return res;
					}
					ex.setBody(res.getBytes());
					return null;
				})
			},
			new Object[] {
				"body-urlencode.txt",
				new MapInit<String, Object>()
				.append("some-header", Arrays.asList("my header value"))
				.append("content-type", Arrays.asList("application/x-www-form-urlencoded"))
				.append("content-length", Arrays.asList("273"))
				.toMap(),
				createFunction((ex)->{
					RequestParameters data = (RequestParameters) new RequestParameters()
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
					if (ex == null) {
						return data;
					}
					ex.setBodyUrlencoded(data);
					return null;
				})
				
			},
			new Object[] {
				"body-binary.txt",
				new MapInit<String, Object>()
				.append("some-header", Arrays.asList("my header value"))
				.append("content-type", Arrays.asList("image/x-icon"))
				.append("content-length", Arrays.asList("317"))
				.toMap(),
				createFunction((ex)->{
					byte[] res = binaryData.toByteArray();
					if (ex == null) {
						return res;
					}
					ex.setBody(res);
					return null;
				})
			},
			new Object[] {
				"body-multipart-no-file.txt",
				new MapInit<String, Object>()
				.append("some-header", Arrays.asList("my header value"))
				.append("content-type", Arrays.asList("multipart/form-data; boundary=item-separator"))
				.append("content-length", Arrays.asList("789"))
				.toMap(),
				createFunction((ex)->{
					RequestParameters res = (RequestParameters) new RequestParameters()
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
					if (ex == null) {
						return res;
					}
					ex.setBodyFormData(res);
					return null;
				})
			},
			new Object[] {
				"body-multipart-with-file.txt",
				new MapInit<String, Object>()
				.append("some-header", Arrays.asList("my header value"))
				.append("content-type", Arrays.asList("multipart/form-data; boundary=item-separator"))
				.append("content-length", Arrays.asList("526"))
				.toMap(),
				createFunction((ex)->{
					RequestParameters res = (RequestParameters) new RequestParameters()
					.put("another", "value")
					.put("fileinput", new UploadedFile("filename.xyz", "IMG", "PNG", binaryData.toByteArray()));
					if (ex == null) {
						return res;
					}
					ex.setBodyFormData(res);
					return null;
				})
				
			},
			// TODO websocket test
			/*new Object[] {
				"websocket.txt",
				new MapInit<String, Object>()
				.append("some-header", Arrays.asList("my header value"))
				.toMap(),
				"TODO"
			}*/
		};
	}
	
	private void assertBody(Object expected, Exchange actual) {
		switch (actual.getType()) {
			case BASIC:
				if (expected == null) {
					assertNull(expected);
					assertNull(actual);
				} else if (expected instanceof String) {
					assertEquals(expected, new String(actual.getBody()));
				} else {
					assertTrue(Arrays.equals((byte[])expected, actual.getBody()));
				}
				break;
			case FORM_DATA:
			case URLENCODED_DATA:
			default:
				assertEquals(expected, actual.getBodyInParameters());
		}
	}
	
	private static Object createFunction(Function<Exchange, Object> setBody) {
		return setBody;
	}
	
	private ByteArrayOutputStream readFile(String file) throws IOException {
		try (InputStream is = getClass().getResourceAsStream(file)) {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			int v;
			while((v = is.read()) != -1) {
				data.write(v);
			}
			return data;
		}
	}

	private ExchangeFactory createParser() {
		Logger logger = new Log4j2LoggerTestImpl(null);
		StreamReader reader = new StreamReader(1024) { // max size is 1 kB
			@Override public void write(int b, OutputStream os) throws IOException {
				if (os instanceof BufferedOutputStreamMock) {
					super.write(b, os);
				} else if (os != null) {
					if (b == '\n') {
						os.write('\r');
					}
					os.write(b);
				}
			}
		};
		Payload payload = new Payload();
		return new ExchangeFactory(
				new FirstLine(reader), new Headers(reader),
				new Form(payload, reader), new Urlencode(payload, reader),
				reader,
				()->"item-separator",
				logger
		);
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Start");
		byte[] file = fileContent().toByteArray();
		writeFiles("body-binary.txt", bodyBinary(), file);
		writeFiles("body-empty.txt", bodyEmpty(), file);
		writeFiles("body-multipart-no-file.txt", bodyMultipartNoFile(), file);
		writeFiles("body-multipart-with-file.txt", bodyMultipartWithFile(), file);
		writeFiles("body-plain-text.txt", bodyPlainText(), file);
		writeFiles("body-urlencode.txt", bodyUrlencode(), file);
		writeFiles("websocket.txt", websocket(), file);
		System.out.println("End");
	}

	private static void writeFiles(String fileName, List<String> data, byte[] file) throws Exception {
		System.out.println("--> write " + fileName);
		try (OutputStream req = new FileOutputStream("toti-libs/toti-tcpip/src/test/resources/parser/requests/" + fileName);
			OutputStream res = new FileOutputStream("toti-libs/toti-tcpip/src/test/resources/parser/responses/" + fileName)
		) {
			req.write("POST /some/url HTTP/1.1".getBytes());

			res.write("HTTP/1.1 200 OK".getBytes());
			for (String message : data) {
				req.write('\r');
				req.write('\n');

				res.write('\r');
				res.write('\n');
				if (message.equals("--file--")) {
					req.write(file);
					res.write(file);
				} else {
					req.write(message.getBytes());
					res.write(message.getBytes());
				}
			}
		}
	}

	private static List<String> bodyEmpty() {
		return Arrays.asList(
			"content-length: 0",
			"some-header: my header value",
			"content-type: application/x-www-form-urlencoded",
			"",
			""
		);
	}

	private static List<String> bodyBinary() {
		return Arrays.asList(
			"content-length: 317",
			"some-header: my header value",
			"content-type: image/x-icon",
			"",
			"--file--"
		);
	}

	private static List<String> bodyMultipartNoFile() {
		return Arrays.asList(
			"content-length: 789",
			"some-header: my header value",
			"content-type: multipart/form-data; boundary=item-separator",
			"",
			"--item-separator",
			"Content-Disposition: form-data; name=\"maplist[a][]\"",
			"",
			"value-maplist-a-1",
			"--item-separator",
			"Content-Disposition: form-data; name=\"maplist[a][]\"",
			"",
			"value-maplist-a-2",
			"--item-separator",
			"Content-Disposition: form-data; name=\"maplist[b][]\"",
			"",
			"value-maplist-b-1",
			"--item-separator",
			"Content-Disposition: form-data; name=\"maplist[b][]\"",
			"",
			"value-maplist-b-2",
			"--item-separator",
			"Content-Disposition: form-data; name=\"list[]\"",
			"",
			"value-list-1",
			"--item-separator",
			"Content-Disposition: form-data; name=\"list[]\"",
			"",
			"value-list-2",
			"--item-separator",
			"Content-Disposition: form-data; name=\"list[]\"",
			"",
			"value-list-3",
			"--item-separator",
			"Content-Disposition: form-data; name=\"map[a]\"",
			"",
			"value-map-a",
			"--item-separator",
			"Content-Disposition: form-data; name=\"map[b]\"",
			"",
			"value-map-b",
			"--item-separator--"
		);
	}

	private static List<String> bodyPlainText() {
		return Arrays.asList(
			"content-length: 59",
			"some-header: my header value",
			"content-type: plain/text",
			"",
			"Some UTF-8 text: ěščř Сайн уу 你好 أأهلاً"
		);
	}

	private static List<String> bodyUrlencode() {
		return Arrays.asList(
			"content-length: 273",
			"some-header: my header value",
			"content-type: application/x-www-form-urlencoded",
			"",
			"maplist%5Ba%5D%5B%5D=value-maplist-a-1&maplist%5Ba%5D%5B%5D=value-maplist-a-2&maplist%5Bb%5D%5B%5D=value-maplist-b-1&maplist%5Bb%5D%5B%5D=value-maplist-b-2&list%5B%5D=value-list-1&list%5B%5D=value-list-2&list%5B%5D=value-list-3&map%5Ba%5D=value-map-a&map%5Bb%5D=value-map-b"
		);
	}

	private static List<String> websocket() {
		return Arrays.asList(
			"some-header: my header value",
			"Upgrade: websocket",
			"Origin: me",
			"Sec-WebSocket-Key: myRequest"
		);
	}

	private static List<String> bodyMultipartWithFile() {
		return Arrays.asList(
			"content-length: 526",
			"some-header: my header value",
			"content-type: multipart/form-data; boundary=item-separator",
			"",
			"--item-separator",
			"Content-Disposition: form-data; name=\"another\"",
			"",
			"value",
			"--item-separator",
			"Content-Disposition: form-data; name=\"fileinput\"; filename=\"filename.xyz\"",
			"Content-Type: IMG",
			"",
			"--file--",
			"--item-separator--"
		);
	}
	
}
