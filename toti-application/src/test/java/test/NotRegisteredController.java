package test;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;

@Controller
public class NotRegisteredController {

	@Action
	public ResponseAction index() {
		return null;
	}
	
	@Action
	public ResponseAction index(Integer index) {
		return null;
	}
	
	@Action
	public ResponseAction index(String index) {
		return null;
	}

	@Action
	public ResponseAction moreParams(String param1, Integer param2) {
		return null;
	}
	
	public ResponseAction someMethod() {
		return null;
	}

	public ResponseAction someMethod(String param1, Integer param2) {
		return null;
	}

}
