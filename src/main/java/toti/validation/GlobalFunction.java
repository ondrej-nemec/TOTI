package toti.validation;

import java.util.Set;

import socketCommunication.http.server.RequestParameters;
import translator.Translator;

public interface GlobalFunction {

	Set<String> apply(RequestParameters requestParams, RequestParameters validatorParams, Translator translator);
	
}
