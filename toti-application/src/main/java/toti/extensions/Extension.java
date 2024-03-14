package toti.extensions;

import ji.common.functions.Env;
import toti.application.register.Register;

public interface Extension {

	String getIdentifier();
	
	void init(Env appEnv, Register register);

}
