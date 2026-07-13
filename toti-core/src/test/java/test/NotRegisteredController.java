package test;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.lib.common.structures.ThrowingSupplier;

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
	
	public <T> T runLinkMethod(ThrowingSupplier<T, ClassNotFoundException> run) throws ClassNotFoundException {
		return run.get();
	}

}
