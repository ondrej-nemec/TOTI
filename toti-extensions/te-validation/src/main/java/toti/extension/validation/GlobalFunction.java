package toti.extension.validation;

import toti.application.extensions.Translator;
import toti.lib.tcpip.structures.RequestParameters;
import toti.application.answers.request.Identity;
import toti.application.answers.request.Request;

public interface GlobalFunction {

	void apply(
		Request request, RequestParameters params, ValidationResult result, Translator translator, Identity identity
	);
	
}
