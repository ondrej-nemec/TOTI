package toti.extensions;

import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.structures.RequestParameters;
import toti.Headers;
import toti.answers.request.Identity;

public interface Session extends Extension {

	void onRequestStart(
		Identity identity,
		MapDictionary<String> sessionSpace,
		Headers requestHeaders,
		MapDictionary<String> queryParams,
		RequestParameters requestBody
	);
	
	void onRequestEnd(
		Identity identity,
		MapDictionary<String> sessionSpace,
		Headers responseHeaders
	);
	
}
