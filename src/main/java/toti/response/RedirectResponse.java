package toti.response;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.Link;
import toti.url.MappedUrl;
import ji.translator.Translator;

public class RedirectResponse implements Response {

	private final String url;
	private final StatusCode code;
	
	public RedirectResponse(StatusCode code, String url, boolean allowOutOfAppRedirect) {
		if (!allowOutOfAppRedirect && !Link.isRelative(url)) {
			throw new RuntimeException("Open redirection is not allowed");
		}
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

}
