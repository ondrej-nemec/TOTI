package mvc.response;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import mvc.ResponseHeaders;
import mvc.templating.Template;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
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
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String charset) {
		params.put("nonce", RandomStringUtils.randomAlphanumeric(50));
		header.addHeader("Content-Type: text/html; charset=" + charset);
		return RestApiResponse.textResponse(
			code,
			header.getHeaders(),
			(bw)->{
				try {
					Template template = templateFactory.getTemplate(fileName);
					bw.write(template.create(templateFactory, params, translator));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		});
	}


}
