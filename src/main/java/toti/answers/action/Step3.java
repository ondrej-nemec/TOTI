package toti.answers.action;

import toti.validation.Validator;

public interface Step3 extends Step4 {

	Step4 validate(Validate validate);
	
	Step4 validate(Validator validator);
	
}
