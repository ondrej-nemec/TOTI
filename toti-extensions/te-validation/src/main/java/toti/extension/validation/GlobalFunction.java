package toti.extension.validation;

import toti.extensions.Translator;
import toti.lib.tcpip.structures.RequestParameters;
import toti.answers.request.Identity;
import toti.answers.request.Request;

public interface GlobalFunction {

	void apply(
		Request request, RequestParameters params, ValidationResult result, Translator translator, Identity identity
	);
	
}
