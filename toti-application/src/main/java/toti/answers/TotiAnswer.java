package toti.answers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import toti.ServerException;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.answers.response.ResponseContainer;
import toti.extensions.TotiResponse;
import toti.templating.TemplateFactory;

public class TotiAnswer {
	
	private final List<String> developIps;
	private final TemplateFactory templateFactory;
	private final Translator translator;
	
	private final IdentityFactory identityFactory;
	private final Map<String, TotiResponse> extensions = new HashMap<>();
	
	public TotiAnswer(
			List<String> developIps, TemplateFactory templateFactory, Translator translator,
			IdentityFactory identityFactory, List<TotiResponse> extensions) {
		this.developIps = developIps;
		this.templateFactory = templateFactory;
		this.translator = translator;
		this.identityFactory = identityFactory;
		extensions.forEach(e->e.getListeneringUri().forEach(u->this.extensions.put(u, e)));
	}
	
	public ji.socketCommunication.http.structures.Response answer(
			ji.socketCommunication.http.structures.Request request, Headers requestHeaders,
			Identity identity, Headers responseHeaders, String charset
		) throws ServerException {
		String uri = request.getPlainUri().substring(5);
		return getResponse(uri, Request.fromRequest(request, requestHeaders), identity, responseHeaders)
				.getResponse(
					request.getProtocol(), responseHeaders, identity,
					new ResponseContainer(
						translator.withLocale(identity.getLocale()), null, null, templateFactory, null
					), 
					charset
				);
	}
	
	protected Response getResponse(String url, Request request, Identity identity, Headers responseHeaders) {
		boolean isDevelopReqeust = developIps.contains(identity.getIP());
		switch (url.toLowerCase()) {
			case "":
			case "/":
			case "/index":
			case "/index.html":
				if (isDevelopReqeust) {
					return getWelcomePage();
				}
				break;
		}
		if (extensions.containsKey(url)) {
			TotiResponse extension = extensions.get(url);
			return extension.getResponse(
				url, request, 
				identity, identityFactory.getSpace(extension.getIdentifier(), identity),
				responseHeaders, isDevelopReqeust
			);
		}
		return Response.create(StatusCode.NOT_FOUND).getEmpty();
	}
	
	private Response getWelcomePage() {
		return Response.create(StatusCode.OK).getFile("toti/assets/index.html");
	}
	
	/*
	
	private Response getProfiler(HttpMethod method, RequestParameters params, Identity identity) {
		if (profiler.isUse() && developIps.contains(identity.getIP())) {
			return profiler.getResponse(method, params);
		}
		return Response.getText(StatusCode.FORBIDDEN, "");
	}
	
	private Response getTotiFiles(String url, Identity identity) {
		return Response.getTemplate(
			"/assets" + url, 
			new MapInit<String, Object>()
			.append("useProfiler", profiler.isUse() && developIps.contains(identity.getIP()))
			.toMap()
		);
	}
	
	private Response getDbViewer(HttpMethod method, String url, RequestParameters params, Identity identity, Headers responseHeaders) {
		throw new NotImplementedYet();
		//  return dbViewer.getResponse(method, url.substring(8), params, identity, headers);
	}
	
	private Response getGenerate(HttpMethod method, String url, RequestParameters params) {
		if (url.equals("/do") && method == HttpMethod.POST) {
			return generator.generate(params);
		}
		return generator.getPage();
	}

	*/
}
