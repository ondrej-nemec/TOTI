package test;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;

@Controller("controllerC")
public class ControllerC {
	
	@Action(path="index")
	public ResponseAction index(int i) {
		return null;
	}

	@Action(path="index")
	public ResponseAction index(String s) {
		return null;
	}
	
}
