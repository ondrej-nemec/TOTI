package toti.application.answers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import toti.application.ServerException;
import toti.application.answers.request.Identity;
import toti.application.answers.request.IdentityFactory;
import toti.application.answers.request.Request;
import toti.application.answers.response.FinalResponse;
import toti.application.answers.response.Response;
import toti.application.answers.response.ResponseContainer;
import toti.application.application.register.MappedAction;
import toti.application.extensions.TemplateExtension;
import toti.application.extensions.TotiExtension;
import toti.application.extensions.TranslatorExtension;
import toti.application.logging.Page;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.tcpip.enums.StatusCode;

public class TotiAnswer {
	
	private final Function<String, Boolean> isDevelop;
	private final TemplateExtension templateExtension;
	private final TranslatorExtension translatorExtension;
	
	private final IdentityFactory identityFactory;
	private final Map<String, TotiExtension> extensions = new HashMap<>();
	
	public TotiAnswer(
			Function<String, Boolean> isDevelop, TemplateExtension templateExtension, TranslatorExtension translatorExtension,
			IdentityFactory identityFactory, List<TotiExtension> extensions) {
		this.isDevelop = isDevelop;
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
		boolean isDevelopReqeust = isDevelop.apply(identity.getIP());
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
		//return Response.create(StatusCode.OK).getFile("toti/assets/index.html");
		return Response.create(StatusCode.OK)
			.addHeader("Content-Type", "text/html")
			.getText(Page.primary(
				"Welcome",
				b->{
					b.addH1("Welcome");
					b.addH2("Hello and welcome in TOTI framework");
					b.addParagraph("Your application is running successfully");
				}
			).create());
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
