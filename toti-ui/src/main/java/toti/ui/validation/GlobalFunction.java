package toti.ui.validation;

import ji.socketCommunication.http.structures.RequestParameters;

public interface GlobalFunction {

	// TODO maybe send translator and request too
	void apply(RequestParameters requestParams, ValidationResult result);
	
}
