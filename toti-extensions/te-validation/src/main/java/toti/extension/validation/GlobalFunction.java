package toti.extension.validation;

import toti.lib.tcpip.structures.RequestParameters;

public interface GlobalFunction {

	void apply(RequestParameters params, ValidationResult result);
	
}
