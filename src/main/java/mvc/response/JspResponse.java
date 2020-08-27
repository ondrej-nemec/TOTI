package mvc.response;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import mvc.templating.Template;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class JspResponse implements Response {
	
	private final Map<String, Object> params;
	private final StatusCode code;
	private final String fileName;
	//private final String charset;

	public JspResponse(StatusCode code, String fileName, Map<String, Object> params/*, String charset*/) {
		this.params = params;
		this.code = code;
		this.fileName = fileName;
		//this.charset = charset;
	}

	@Override
	public RestApiResponse getResponse(List<String> header, TemplateFactory templateFactory, Translator translator, String charset) {
		List<String> h = new LinkedList<>(header);
		h.add("Content-Type: text/html; charset=" + charset);
		params.put("nonce", RandomStringUtils.randomAlphanumeric(50)); // TODO generated upper
		return RestApiResponse.textResponse(code, h, (bw)->{
			try {
				Template template = templateFactory.getTemplate(fileName);
				bw.write(template.create(templateFactory, params, translator));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}


}
