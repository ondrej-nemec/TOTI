package test;

import ji.socketCommunication.http.HttpMethod;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;

@Controller("controllerA")
public class ControllerA {
	
	@Action(path = "index")
	public ResponseAction index() {
		return null;
	}
	
	@Action(path="list")
	public ResponseAction list() {
		return null;
	}
	
	@Action(path="get")
	public ResponseAction get(Integer id) {
		return null;
	}
	
	@Action(methods = {HttpMethod.POST, HttpMethod.PUT})
	public ResponseAction form() {
		return null;
	}
	
	public ResponseAction notAction() {
		return null;
	}

}
