package toti.extensions;

import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.application.register.Register;
import toti.common.structures.MapDictionary;
import toti.env.Env;
import toti.http.structures.RequestParameters;

public interface Extension {

	// used: sessionSpace
	String getIdentifier();
	
	// called during ApplicationFactory.create
	void init(Env appEnv, Register register);

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

	void onApplicationStart() throws Exception;
	
	void onApplicationStop() throws Exception;

}
