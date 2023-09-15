package toti.answers.action;

import toti.security.Action;

public interface Step2 extends Step3 {

	Step3 authrorize(Authorize authrorize);
	
	Step3 authorize(String domain, Action action);

}
