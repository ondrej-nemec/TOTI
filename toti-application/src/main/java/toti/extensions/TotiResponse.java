package toti.extensions;

import java.util.List;

import ji.common.structures.MapDictionary;
import toti.Headers;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;

public interface TotiResponse extends Extension {
	
	List<String> getListeneringUri();
	
	Response getResponse(
		String uri, Request request,
		Identity identity, MapDictionary<String> space,
		Headers responseHeaders, boolean isDeveloperRequest
	);
	
}
