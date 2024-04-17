package toti.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ji.common.functions.Env;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.RequestParameters;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.application.register.Register;
import toti.extension.templating.TemplateExtension;
import toti.extensions.Extension;
import toti.extensions.TotiExtension;
import toti.templating.Tag;
import toti.ui.tags.ControlTag;
import toti.ui.tags.FormTag;
import toti.ui.tags.GridTag;

public class UiExtension implements Extension, TotiExtension {

	@Override
	public String getIdentifier() {
		return "toti-ui";
	}
/*
	// TODO
	@Override
	public String getTranslationPath() {
		return "toti/ui/translations";
	}
*/
	@Override
	public List<String> getListeningUri() {
		return Arrays.asList(".js", ".css");
	}

	@Override
	public Response getResponse(String uri, Request request, Identity identity, MapDictionary<String> space,
			Headers responseHeaders, boolean isDeveloperRequest) {
		switch (uri) {
			case ".js":
				return Response.create(StatusCode.OK)
					.addHeader("Content-Type", "text/javascript")
					.getTemplate("/ui/assets/js.jsp", new HashMap<>());
			case ".css":
				return Response.create(StatusCode.OK)
					.addHeader("Content-Type", "text/css")
					.getTemplate("/ui/assets/css.css", new HashMap<>());
			default:
				return Response.create(StatusCode.NOT_FOUND).getEmpty();
		}
	}

	@Override
	public void init(Env appEnv, Register register) {
		TemplateExtension templateExtension = register.getExtension(TemplateExtension.class);
		if (templateExtension == null) {
			throw new RuntimeException("TOTI-UI Extension requires TOTI-Templating Extension.");
		}
		templateExtension.registerTags(getTags());
		// TODO some special only for this
		// module path is empty - files are in classpath
		templateExtension.registerModule("toti", "", "toti");
	}
	
	protected List<Tag> getTags() {
		return Arrays.asList(
			new ControlTag(),
			new FormTag(),
			new GridTag()
		);
	}

	@Override
	public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
		MapDictionary<String> queryParams, RequestParameters requestBody) {}

	@Override
	public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {}

	@Override
	public void onApplicationStart() throws Exception {}

	@Override
	public void onApplicationStop() throws Exception {}

}
