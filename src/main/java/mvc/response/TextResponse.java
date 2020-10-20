package mvc.response;

import java.util.List;

import mvc.ResponseHeaders;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class TextResponse implements Response {

	private final String text;
	private final StatusCode code;
	
	public TextResponse(StatusCode code, String text) {
		this.text = text;
		this.code = code;
	}

	@Override
	public void addParam(String name, Object value) {}
	
	@Override
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String charset) {
		List<String> h = header.getHeaders();
		return RestApiResponse.textResponse(code, h, (bw)->{
			bw.write(text);
		});
	}

}
