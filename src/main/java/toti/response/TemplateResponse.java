package toti.response;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import translator.Translator;

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
	public void addParam(String name, Object value) {
		params.put(name, value);
	}

	@Override
	public RestApiResponse getResponse(
			ResponseHeaders header, 
			TemplateFactory templateFactory, 
			Translator translator,
			Authorizator authorizator,
			Identity identity,
			String charset) {
		String nonce = RandomStringUtils.randomAlphanumeric(50);
		params.put("nonce", nonce);
		params.put("totiUser", identity.getUser());
		header.getHeaders().forEach((head)->{
			head.replace("{nonce}", nonce);
		});
		header.addHeader(getContentType(fileName, charset));
		return RestApiResponse.textResponse(
			code,
			header.getHeaders(),
			(bw)->{
				try {
					Template template = templateFactory.getTemplate(fileName);
					bw.write(template.create(templateFactory, params, translator, authorizator));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		});
	}


}
