package toti.answers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toti.ServerException;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.answers.request.Request;
import toti.answers.response.FinalResponse;
import toti.answers.response.Response;
import toti.answers.response.ResponseContainer;
import toti.application.register.MappedAction;
import toti.extensions.TotiExtension;
import toti.extensions.TranslatorExtension;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.tcpip.enums.StatusCode;
import toti.extensions.TemplateExtension;

public class TotiAnswer {
	
	private final List<String> developIps;
	private final TemplateExtension templateExtension;
	private final TranslatorExtension translatorExtension;
	
	private final IdentityFactory identityFactory;
	private final Map<String, TotiExtension> extensions = new HashMap<>();
	
	public TotiAnswer(
			List<String> developIps, TemplateExtension templateExtension, TranslatorExtension translatorExtension,
			IdentityFactory identityFactory, List<TotiExtension> extensions) {
		this.developIps = developIps;
		this.templateExtension = templateExtension;
		this.translatorExtension = translatorExtension;
		this.identityFactory = identityFactory;
		extensions.forEach(e->e.getListeningUri().forEach(u->this.extensions.put(u, e)));
	}
	
	public FinalResponse answer(
			Request request,
			Identity identity, Headers responseHeaders, String charset
		) throws ServerException {
		ObjectBuilder<String> moduleName = new ObjectBuilder<>();
		String uri = request.getUri().substring(5);
		return getResponse(uri, request, identity, responseHeaders, moduleName)
				.prepare(
					responseHeaders, identity,
					new ResponseContainer(
						translatorExtension.getTranslator(identity), null,
						MappedAction.totiAnswer(moduleName.get()),
						templateExtension, null
					), 
					charset
				);
	}
	
	protected Response getResponse(String url, Request request, Identity identity, Headers responseHeaders, ObjectBuilder<String> moduleName) {
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
			TotiExtension extension = extensions.get(url);
			moduleName.set(extension.getIdentifier());
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
