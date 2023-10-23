package toti.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.StatusCode;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.extensions.OnToti;
import toti.extensions.TranslatedExtension;

public class UiExtension implements TranslatedExtension, OnToti {

	@Override
	public String getIdentifier() {
		return "toti-ui";
	}

	@Override
	public String getTranslationPath() {
		return "toti/ui/translations";
	}

	@Override
	public List<String> getListeningUri() {
		return Arrays.asList(".js");
	}

	@Override
	public Response getResponse(String uri, Request request, Identity identity, MapDictionary<String> space,
			Headers responseHeaders, boolean isDeveloperRequest) {
		return Response.create(StatusCode.OK).getTemplate("/toti/ui/assets/toti.jsp", new HashMap<>());
	}

}
