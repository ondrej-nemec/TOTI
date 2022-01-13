package toti.response;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
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
	public RestApiResponse getResponse(
			ResponseHeaders header, 
			TemplateFactory templateFactory, 
			Translator translator,
			Authorizator authorizator,
			Identity identity, 
			MappedUrl current,
			String charset) {
		String nonce = RandomStringUtils.randomAlphanumeric(50);
		params.put("nonce", nonce);
		params.put("totiIdentity", identity);
		header.getHeaders().forEach((head)->{
			head.replace("{nonce}", nonce);
		});
		header.addHeader(getContentType(fileName, charset));
		String response = createResponse(templateFactory, translator, authorizator, current);
		return RestApiResponse.textResponse(
			code,
			header.getHeaders(),
			(bw)->{
				bw.write(response);
				/*try {
					Template template = templateFactory.getTemplate(fileName);
					bw.write(template.create(templateFactory, params, translator, authorizator, current));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}*/
		});
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
