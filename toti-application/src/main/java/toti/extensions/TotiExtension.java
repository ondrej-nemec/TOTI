package toti.extensions;

import java.util.List;

import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.common.structures.MapDictionary;

public interface TotiExtension extends Extension {
	
	List<String> getListeningUri();
	
	Response getResponse(
		String uri, Request request,
		Identity identity, MapDictionary<String> space,
		Headers responseHeaders, boolean isDeveloperRequest
	);
	
}
