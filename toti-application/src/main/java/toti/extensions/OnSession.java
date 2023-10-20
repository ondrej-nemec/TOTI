package toti.extensions;

import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.structures.RequestParameters;
import toti.answers.Headers;
import toti.answers.request.Identity;

public interface OnSession extends Extension {

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