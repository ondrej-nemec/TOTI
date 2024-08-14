package ji.common.functions;

import ji.common.structures.Dictionary;

public interface Env extends Dictionary<String> {
	
	Env getModule(String key);
	
	// TODO
	// Env merge(Env env);
	
}
