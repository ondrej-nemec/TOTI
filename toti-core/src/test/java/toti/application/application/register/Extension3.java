package toti.application.application.register;

import toti.application.answers.Headers;
import toti.application.answers.request.Identity;
import toti.application.extensions.Extension;
import toti.lib.common.structures.MapDictionary;
import toti.lib.files.env.Env;
import toti.lib.tcpip.structures.RequestParameters;

public class Extension3 implements Extension {

	@Override
	public String getIdentifier() {
		return "test3";
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
