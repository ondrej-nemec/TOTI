package toti.core.answers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import toti.core.ServerException;
import toti.core.answers.request.Request;
import toti.core.answers.response.FinalResponse;
import toti.core.answers.response.Response;
import toti.core.answers.response.ResponseContainer;
import toti.core.answers.session.Identity;
import toti.core.application.register.MappedAction;
import toti.core.extensions.TemplateFactory;
import toti.core.extensions.TotiExtension;
import toti.core.logging.Page;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.tcpip.enums.StatusCode;

public class TotiAnswer {
	
	private final Function<String, Boolean> isDevelop;
	private final TemplateFactory templateExtension;
	
	private final Map<String, TotiExtension> extensions = new HashMap<>();
	
	public TotiAnswer(
			Function<String, Boolean> isDevelop, TemplateFactory templateExtension,
			List<TotiExtension> extensions) {
		this.isDevelop = isDevelop;
		this.templateExtension = templateExtension;
		extensions.forEach(e->e.getListeningUri().forEach(u->this.extensions.put(u, e)));
	}
	
	public FinalResponse answer(
			Request request,
			Identity identity, Headers responseHeaders, String charset
		) throws ServerException {
		ObjectBuilder<String> moduleName = new ObjectBuilder<>();
		String uri = request.getUri().substring(5);
		return getResponse(uri, request, identity, responseHeaders, moduleName)
		.prepare(new ResponseContainer(charset, responseHeaders, identity, null, MappedAction.totiAnswer(moduleName.get()), templateExtension));
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
			return extension.get(url, isDevelopReqeust).create(request, identity);
		}
		return Response.create(StatusCode.NOT_FOUND).getText("Not found");
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

}
