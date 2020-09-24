package mvc.response;

import mvc.ResponseHeaders;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class RedirectResponse implements Response {

	private final String url;
	private final StatusCode code;
	
	public RedirectResponse(StatusCode code, String url) {
		this.url = url;
		this.code = code;
	}
	
	@Override
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String templateDir, String charset) {
		return RestApiResponse.textResponse(code, header.getHeaders("Location: " + url), (bw)->{});
	}

	@Override
	public void addParam(String name, Object value) {}

}
