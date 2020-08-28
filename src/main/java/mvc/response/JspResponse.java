package mvc.response;

import java.util.Map;

import mvc.ResponseHeaders;
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
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String charset) {
		params.put("nonce", header.getNonce());
		return RestApiResponse.textResponse(
			code,
			header.getHeaders("Content-Type: text/html; charset=" + charset),
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
