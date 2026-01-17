package toti.application.extensions;

import java.util.List;

import toti.application.answers.Headers;
import toti.application.answers.request.Identity;
import toti.application.answers.request.Request;
import toti.application.answers.response.Response;
import toti.lib.common.structures.MapDictionary;

public interface TotiExtension extends Extension {
	
	List<String> getListeningUri();
	
	Response getResponse(
		String uri, Request request,
		Identity identity, MapDictionary<String> space,
		Headers responseHeaders, boolean isDeveloperRequest
	);
	
}
