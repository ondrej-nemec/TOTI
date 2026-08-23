package toti.examples.demo.modules.core;

import java.time.LocalDateTime;
import java.util.Random;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.response.Response;

@Controller("identity")
public class IdentityController {

	@Action(path="print")
	public ResponseAction print() {
		return (request, identity)->{
			return Response.OK().getText(String.format(
				"Identity:\nIP:%s\nCSRF:%s\nUser:%s\nSpace:%s",
				identity.getIP(), identity.getCsrfToken(), identity.getUser(), identity.getSessionSpace()
			));
		};
	}

	@Action(path="set")
	public ResponseAction set() {
		return (request, identity)->{
			int random = new Random().nextInt(10);
			LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
			identity.getSessionSpace().put("A", now);
			identity.getSessionSpace().put("B", random);

			return Response.OK().getText(String.format("A:%s\nB:%s", now, random));
		};
	}

}
