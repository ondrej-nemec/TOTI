package toti.registr;

import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.Identity;
import translator.Translator;

public interface ControllerFactory {


	Object apply(Translator translator, Identity identity, Authorizator authorizator, Authenticator authenticator) throws Throwable;
	
}
