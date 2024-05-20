package toti.application.register;

import ji.common.functions.Env;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.structures.RequestParameters;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.extensions.Extension;

public class Extension2 implements Extension {

	@Override
	public String getIdentifier() {
		return "test2";
	}

	@Override
	public void init(Env appEnv, Register register) {}

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
