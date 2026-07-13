package test;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;

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
