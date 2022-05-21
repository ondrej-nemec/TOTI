package toti.validation;

import java.util.Set;

import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;

public interface GlobalFunction {

	Set<String> apply(RequestParameters requestParams, RequestParameters validatorParams, Translator translator);
	
}
