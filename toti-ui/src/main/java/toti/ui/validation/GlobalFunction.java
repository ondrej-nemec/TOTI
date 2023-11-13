package toti.ui.validation;

import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;
import toti.answers.request.Request;

public interface GlobalFunction {

	void apply(Request request, RequestParameters params, ValidationResult result, Translator translator);
	
}
