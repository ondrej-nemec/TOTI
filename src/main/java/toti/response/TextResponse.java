package toti.response;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;
import ji.translator.Translator;

public class TextResponse implements Response {

	private final String text;
	private final StatusCode code;
	
	public TextResponse(StatusCode code, Object text) {
		this.text = text == null ? "" : text.toString();
		this.code = code;
	}
	
	@Override
	public RestApiResponse getResponse(
			ResponseHeaders header,
			TemplateFactory templateFactory, 
			Translator translator, 
			Authorizator authorizator,
			Identity identity, MappedUrl current,
			String charset) {
		return RestApiResponse.textResponse(code, header.getHeaders(), (bw)->{
			bw.write(text);
		});
	}

}
