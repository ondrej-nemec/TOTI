package toti.answers.request;

import java.io.IOException;
import java.util.Collection;

import toti.answers.Headers;
import toti.extensions.Extension;
import toti.http.structures.RequestParameters;
import ji.common.structures.MapDictionary;

public class IdentityFactory {

//	private final static String PAGE_ID_HEADER_NAME = "PageId";
//	private final static String PAGE_ID_COOKIE_NAME = "PageId";
	
	private final Collection<Extension> sessions;
	
	public IdentityFactory(Collection<Extension> sessions) {
		this.sessions = sessions;
	}
	
	public MapDictionary<String> getSpace(String name, Identity identity) {
		return identity.getSessionSpace(name);
	}

	public Identity createIdentity(Headers requestHeaders, MapDictionary<String> queryParameters, RequestParameters bodyParameters, String ip) {
		Identity identity = new Identity(ip);
		sessions.forEach((session)->{
			session.onRequestStart(
				identity, identity.getSessionSpace(session),
				requestHeaders, queryParameters, bodyParameters
			);
		});
		return identity;
	}
	
	public void finalizeIdentity(Identity identity, Headers responseHeaders) throws IOException {
		sessions.forEach((session)->{
			session.onRequestEnd(
				identity, identity.getSessionSpace(session), responseHeaders
			);
		});
		/*if (identity.getPageId() != null) {
			responseHeaders.addHeader(
				"Set-Cookie", PAGE_ID_COOKIE_NAME + "=" + identity.getPageId()
				+ "; SameSite=Strict"
			);
		}*/
	}
	
/*
	private String getPageId(Headers headers) {
		Object pageHeader = headers.getHeader(PAGE_ID_HEADER_NAME);
		if (pageHeader == null) {
			return ("Page_" + new Random().nextDouble()).replace(".", "");
		}
		return pageHeader.toString();
	}
	*/
	
}
