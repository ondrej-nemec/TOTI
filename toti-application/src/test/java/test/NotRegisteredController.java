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
	
}
