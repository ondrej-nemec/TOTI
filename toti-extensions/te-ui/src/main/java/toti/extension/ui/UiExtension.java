package toti.extension.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import toti.core.answers.Headers;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.request.Identity;
import toti.core.answers.response.Response;
import toti.core.application.register.Register;
import toti.core.extensions.TotiExtension;
import toti.extension.ui.tags.ControlTag;
import toti.extension.ui.tags.FormTag;
import toti.extension.ui.tags.GridTag;
import toti.lib.common.structures.MapDictionary;
import toti.lib.files.env.Env;
import toti.lib.tcpip.enums.StatusCode;
import toti.lib.tcpip.structures.RequestParameters;
import toti.lib.templating.Tag;

public class UiExtension implements TotiExtension {

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
	public ResponseAction get(String uri, boolean isDeveloperRequest) {
		return (request, identity)->{
			return switch (uri) {
				case ".js"->Response.create(StatusCode.OK)
					.addHeader("Content-Type", "text/javascript")
					.getTemplate("/ui/assets/js.jsp", new HashMap<>());
				case ".css"->Response.create(StatusCode.OK)
					.addHeader("Content-Type", "text/css")
					.getTemplate("/ui/assets/css.css", new HashMap<>());
				default->Response.create(StatusCode.NOT_FOUND).getEmpty();
			};
		};
	}

	@Override
	public void init(Env appEnv, Register register) {
		// TODO
		/*
		TemplateExtension templateExtension = register.getExtension(TemplateExtension.class);
		if (templateExtension == null) {
			throw new RuntimeException("TOTI-UI Extension requires TOTI-Templating Extension.");
		}
		templateExtension.registerTags(getTags());
		templateExtension.registerModule(getIdentifier(), "", "toti");
		*/
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
