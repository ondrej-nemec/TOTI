package toti.response;

import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;
import translator.Translator;

public class RedirectResponse implements Response {

	private final String url;
	private final StatusCode code;
	
	public RedirectResponse(StatusCode code, String url) {
		this.url = url;
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
		header.addHeader("Location: " + url);
		return RestApiResponse.textResponse(code, header.getHeaders(), (bw)->{});
	}

	@Override
	public void addParam(String name, Object value) {}

}
