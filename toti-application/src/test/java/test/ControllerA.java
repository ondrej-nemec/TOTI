package test;

import ji.socketCommunication.http.HttpMethod;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;

@Controller("controllerA")
public class ControllerA {
	
	@Action
	public ResponseAction index() {
		return null;
	}
	
	@Action()
	public ResponseAction index(Integer id) {
		// same name as previsous but with parameter
		return null;
	}
	
	@Action(methods=HttpMethod.POST)
	public ResponseAction index(String id) {
		// same name as previsous but with another method
		return null;
	}

	@Action()
	public ResponseAction index(int i, String s) {
		// same name as previsous but with tow parameters
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
