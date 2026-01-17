package test;

import toti.application.annotations.Action;
import toti.application.annotations.Controller;
import toti.application.answers.action.ResponseAction;

@Controller("controllerB")
public class ControllerB {

	@Action()
	public ResponseAction get(Integer id) {
		return null;
	}
	
	@Action(path="generate")
	public ResponseAction generate(Integer id) {
		return null;
	}
}
