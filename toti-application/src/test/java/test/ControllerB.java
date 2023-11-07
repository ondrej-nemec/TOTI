package test;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;

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
