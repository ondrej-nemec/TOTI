package toti.application.extensions;

import toti.application.answers.Headers;
import toti.application.answers.request.Identity;
import toti.application.application.register.Register;
import toti.lib.common.structures.MapDictionary;
import toti.lib.files.env.Env;
import toti.lib.tcpip.structures.RequestParameters;

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
