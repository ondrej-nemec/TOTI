package toti.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ji.socketCommunication.http.StatusCode;
import toti.Headers;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;
import ji.translator.Translator;

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
			String protocol,
			Headers header, 
			TemplateFactory templateFactory, 
			Translator translator,
			Authorizator authorizator,
			Identity identity, 
			MappedUrl current,
			String charset) {
		String nonce = RandomStringUtils.randomAlphanumeric(50);
		params.put("nonce", nonce);
		params.put("totiIdentity", identity);
		
		Headers resHeaders = new Headers(new HashMap<>());
		header.getHeaders().forEach((n, l)->{
			l.forEach(v->{
				if (v != null && v instanceof String) {
					resHeaders.addHeader(n, v.toString().replace("{nonce}", nonce));
				} else {
					resHeaders.addHeader(n, v);
				}
			});
		});
		setContentType(fileName, charset, header);
		ji.socketCommunication.http.structures.Response response = new ji.socketCommunication.http.structures.Response(code, protocol);
		response.setHeaders(resHeaders.getHeaders());
		response.setBody(createResponse(templateFactory, translator, authorizator, current).getBytes());
		return response;
	}
	
	public String createResponse(
			TemplateFactory templateFactory,
			Translator translator,
			Authorizator authorizator,
			MappedUrl current) {
		try {
			Template template = templateFactory.getTemplate(fileName);
			return template.create(templateFactory, params, translator, authorizator, current);
		} catch (Exception e) {
			throw new ResponseException(e);
		}
	}


}
