package toti.lib.tcpip.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import org.apache.logging.log4j.Logger;

import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.ThrowingSupplier;
import toti.lib.tcpip.parsers.Form;
import toti.lib.tcpip.parsers.Payload;
import toti.lib.tcpip.parsers.StreamReader;
import toti.lib.tcpip.parsers.Urlencode;

public class ExchangeFactory {

	private final FirstLine firstLine;
	private final Headers headers;
	private final Form form;
	private final Urlencode urlencode;
	private final StreamReader stream;
	
	private final Supplier<String> createBoundary;
	
	private final Logger logger;
	
	public static ExchangeFactory create(Integer maxBodySize, Logger logger) {
		StreamReader reader = new StreamReader(maxBodySize);
		Payload payload = new Payload();
		return new ExchangeFactory(
			new FirstLine(reader), 
			new Headers(reader),
			new Form(payload, reader), 
			new Urlencode(payload, reader),
			reader,
			()->RandomStringUtils.randomAlphanumeric(50),
			logger
		);
	}
	
	public ExchangeFactory(FirstLine firstLine, Headers headers, Form form, Urlencode urlencode, StreamReader stream,
			Supplier<String> createBoundary, Logger logger) {
		this.firstLine = firstLine;
		this.headers = headers;
		this.form = form;
		this.urlencode = urlencode;
		this.stream = stream;
		this.createBoundary = createBoundary;
		this.logger = logger;
	}
	
	/******************/
	
	public ExchangeResponse readResponse(InputStream bis) throws IOException {
		return read(bis, ()->firstLine.createResponse(bis));
	}
	
	public ExchangeRequest readRequest(InputStream bis) throws IOException {
		return read(bis, ()->{
			ExchangeRequest r = firstLine.createRequest(bis);
			String[] uri = r.getUri().split("\\?");
			if (uri.length > 1) {
				r.setUriParams(uri[0], urlencode.decode(uri[1]));
			} else {
				r.setUriParams(uri[0], MapDictionary.hashMap());
			}
			return r;
		});
	}
	
	private <T extends Exchange> T read(InputStream bis, ThrowingSupplier<T, IOException> create) throws IOException {
		try {
			T exchange = create.get();
			if (exchange == null) {
				return null;
			}
			List<String> errors = headers.read(exchange, bis);
			if (errors.size() > 0) {
				errors.forEach((err)->{
					logger.warn(err);
				});
				return null;
			}
			
			Object type = exchange.getHeader("Content-Type");
			Integer length = exchange.getHeader("Content-Length", Integer.class);
			if (type == null || type.equals("") || type.toString().toLowerCase().startsWith("application/x-www-form-urlencoded")) {
				exchange.setBodyUrlencoded(urlencode.decode(bis, length));
			} else if (type != null && type.toString().toLowerCase().startsWith("multipart/form-data")) {
				exchange.setBodyFormData(form.read(type.toString(), length, bis));
			/*} else if (type != null && type.toString().toLowerCase().startsWith("application/json")) {
				exchange.setBodyUrlencoded(new DictionaryValue(new JsonReader().read(new String(data.toByteArray()))));*/
			} else {
				exchange.setBody(stream.readData(length, bis, 0, (a)->false, false));
			}
			return exchange;
		} catch (ParsingException e) {
			logger.warn(e.getMessage());
			return null;
		}
	}
	
	/******************/

	public void write(Exchange exchange, OutputStream bos) throws IOException {
		firstLine.write(exchange, bos);
		
		switch (exchange.getType()) {
			case FORM_DATA:
				String boundary = createBoundary.get();
				ByteArrayOutputStream formData = form.write(boundary, exchange.getBodyInParameters());
				byte[] formbody = formData.toByteArray();
				addHeaders(exchange, formbody.length, "multipart/form-data; boundary=" + boundary);
				
				headers.write(exchange, bos);
				if (formbody.length > 0) {
				//	stream.write('\n', bos); // end of header section or first line
				//	stream.write('\n', bos); // separator
					stream.write(formbody, bos);
				}
				break;
			case URLENCODED_DATA:
				byte[] body = urlencode.encode(exchange.getBodyInParameters()).getBytes();
				addHeaders(exchange, body.length, "application/x-www-form-urlencoded");
				
				headers.write(exchange, bos);
				if (body.length > 0) {
				//	stream.write('\n', bos); // end of header section or first line
				//	stream.write('\n', bos); // separator
					stream.write(body, bos);
				}
				break;
			case WEBSOCKET:
				WebSocket websocket = exchange.getBodyWebsocket();
				
				exchange.addHeader("Upgrade", "websocket");
				exchange.addHeader("Connection", "Upgrade");
				exchange.addHeader("Sec-WebSocket-Accept",  new String(
			    	Base64.getEncoder().encode(DigestUtils.sha1(
				    	websocket.getKey() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
				    ))
				));
				
				headers.write(exchange, bos);
			//	stream.write('\n', bos); // end of header section or first line
			//	stream.write('\n', bos); // separator
				websocket.waitOnMessages();
				break;
			case BASIC:
			default:
				// addHeaders(exchange, exchange.getBody().length, null);
				headers.write(exchange, bos);
				if (exchange.getBody() != null && exchange.getBody().length > 0) {
				//	stream.write('\n', bos); // end of header section or first line
				//	stream.write('\n', bos); // separator
					stream.write(exchange.getBody(), bos);
				}
				break;
		}
		bos.flush();
	}
	
	private void addHeaders(Exchange exchange, int length, String type) {
		try {
			if (!exchange.containsHeader("Content-Length")) {
				exchange.addHeader("Content-Length", length);
			}
			if (type != null && !exchange.containsHeader("Content-Type")) {
				exchange.addHeader("Content-Type", type);
			}
		} catch (UnsupportedOperationException e) {
			logger.warn("Cannot add Content-Length and Content-Type headers", e);
		}
		
	}
	
}
