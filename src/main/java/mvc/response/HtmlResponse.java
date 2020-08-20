package mvc.response;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import mvc.templating.Template;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class HtmlResponse implements Response {
	
	private final Map<String, Object> params;
	private final StatusCode code;
	private final String fileName;

	public HtmlResponse(StatusCode code, String fileName, Map<String, Object> params) {
		this.params = params;
		this.code = code;
		this.fileName = fileName;
	}

	@Override
	public RestApiResponse getResponse(List<String> header, TemplateFactory templateFactory, Translator translator) {
		header.add("Content-Type: text/html; charset=UTF-8");
		params.put("nonce", RandomStringUtils.randomAlphanumeric(50));
		return RestApiResponse.textResponse(code, header, (bw)->{
			try {
				Template template = templateFactory.getTemplate(fileName);
				bw.write(template.create(templateFactory, params, translator));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}


}
