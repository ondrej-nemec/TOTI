package toti.http.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Function;

import toti.http.structures.RequestParameters;

public class Urlencode {
	
	private final Payload payload;
	private final StreamReader reader;
	
	public Urlencode(Payload payload, StreamReader reader) {
		this.payload = payload;
		this.reader = reader;
	}

	public String encode(RequestParameters params) {
		StringBuilder b = new StringBuilder();
		payload.writeItem((name, value)->{
			 try {
				if (!b.toString().isEmpty()) {
					b.append("&");
				}
				// http://localhost/params?someText=aaa&list%5B%5D=bbb&list%5B%5D%22%22=ccc&map%5Ba%5D=eee&map%5Bb%5D=ffff&maplist%5Ba%5D%5B%5D=ggg&maplist%5Ba%5D%5B%5D=hhh&maplist%5Bb%5D%5B%5D=iii&maplist%5Bb%5D%5B%5D=jjjjj
				b.append(String.format(
					"%s=%s",
					URLEncoder.encode(name + "", StandardCharsets.UTF_8.toString()),
					URLEncoder.encode(value + "", StandardCharsets.UTF_8.toString()) // + "" is fix, varialbe could be null
				));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}, null, params);
		return b.toString();
	}
	
	/**************/
	
	public RequestParameters decode(InputStream is, Integer lenght) throws UnsupportedEncodingException, IOException {
		byte[] readed = reader.readData(lenght, is, 0, (ac)->false, false);
		if (readed.length > 0) {
			return decode(new String(readed));
		}
		return new RequestParameters();
	}
	
	public RequestParameters decode(String payload) throws UnsupportedEncodingException {
		RequestParameters data = new RequestParameters();
		if (payload == null || payload.isEmpty()) {
			return data;
		}
		BiConsumer<String, Object> addParameter = (name, value)->data.put(name, value);
		Function<String, Object> getParameter = name->data.get(name);
		String[] params = payload.split("\\&");
		for (String param : params) {
			String[] keyValue = param.split("=");
			this.payload.readItem(
				addParameter, getParameter, 
				URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.toString()),
				keyValue.length == 1 ? "" : URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.toString())
			);
		}
		return data;
	}
}
