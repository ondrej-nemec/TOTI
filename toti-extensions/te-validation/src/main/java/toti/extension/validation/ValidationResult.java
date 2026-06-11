package toti.extension.validation;

import java.util.Map;

import toti.lib.tcpip.structures.RequestParameters;

public interface ValidationResult {

	boolean isValid();

	Map<String, Object> getErrors();

	RequestParameters getValues();

	

}
