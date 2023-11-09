package toti.ui.validation;

import ji.socketCommunication.http.structures.RequestParameters;
import toti.answers.request.Request;

public interface GlobalFunction {

	// TODO maybe send translator and request too
	void apply(Request request, RequestParameters params, ValidationResult result);
	
}
