package test;

import toti.application.annotations.Action;
import toti.application.annotations.Controller;
import toti.application.answers.action.ResponseAction;

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
