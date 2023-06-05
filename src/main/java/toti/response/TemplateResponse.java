package toti.response;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import toti.Headers;
import toti.security.Identity;
import toti.templating.Template;

public class TemplateResponse implements Response {
	
	private final Map<String, Object> params;
	private final StatusCode code;
	private final String fileName;

	public TemplateResponse(StatusCode code, String fileName, Map<String, Object> params) {
		this.params = params;
		this.code = code;
		this.fileName = fileName;
	}

	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			Protocol protocol,
			Headers header, 
			Identity identity, 
			ResponseContainer container,
			String charset) {
		String nonce = RandomStringUtils.randomAlphanumeric(50);
		params.put("nonce", nonce);
		params.put("totiIdentity", identity);
		
		/* Headers resHeaders = new Headers(new HashMap<>());
		header.getHeaders().forEach((n, l)->{
			l.forEach(v->{
				if (v != null && v instanceof String) {
					resHeaders.addHeader(n, v.toString().replace("{nonce}", nonce));
				} else {
					resHeaders.addHeader(n, v);
				}
			});
		});
		setContentType(fileName, charset, resHeaders);*/
		ji.socketCommunication.http.structures.Response response = new ji.socketCommunication.http.structures.Response(code, protocol);
		
		// response.setHeaders(resHeaders.getHeaders());
		header.getHeaders().forEach((n, l)->{
			l.forEach(v->{
				if (v != null && v instanceof String) {
					response.addHeader(n, v.toString().replace("{nonce}", nonce));
				} else {
					response.addHeader(n, v);
				}
			});
		});
		response.addHeader("Content-Type", getContentType(fileName, charset));
		
		
		response.setBody(createResponse(container).getBytes());
		return response;
	}
	
	public String createResponse(ResponseContainer container) {
		try {
			Template template = container.getTemplateFactory().getTemplate(fileName);
			return template.create(container.getTemplateFactory(), params, container);
		} catch (Exception e) {
			throw new ResponseException(e);
		}
	}
}
