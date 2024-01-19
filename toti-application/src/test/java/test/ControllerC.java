package test;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;

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
