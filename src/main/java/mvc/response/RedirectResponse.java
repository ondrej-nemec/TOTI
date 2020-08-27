package mvc.response;

import java.util.LinkedList;
import java.util.List;

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
	public RestApiResponse getResponse(List<String> header, TemplateFactory templateFactory, Translator translator, String charset) {
		List<String> h = new LinkedList<>(header);
		h.add("Location: " + url);
		return RestApiResponse.textResponse(code, h, (bw)->{});
	}

}
