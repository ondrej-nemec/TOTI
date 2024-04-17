package toti.validation;

import ji.socketCommunication.http.structures.RequestParameters;
import toti.extensions.Translator;
import toti.answers.request.Identity;
import toti.answers.request.Request;

public interface GlobalFunction {

	void apply(Request request, RequestParameters params, ValidationResult result, Translator translator, Identity identity);
	
}
