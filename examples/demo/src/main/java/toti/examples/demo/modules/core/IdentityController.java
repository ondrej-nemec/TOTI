package toti.examples.demo.modules.core;

import java.time.LocalDateTime;
import java.util.Random;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.request.Identity;
import toti.core.answers.response.Response;
import toti.core.answers.router.Link;
import toti.core.answers.session.DefaultSession;
import toti.lib.common.functions.Implode;
import toti.lib.tcpip.enums.HttpMethod;

@Controller("identity")
public class IdentityController {

	private final Link link;

	public IdentityController(Link link) {
		this.link = link;
	}

	@Action(path="print")
	public ResponseAction print() {
		return (request, identity)->{
			return Response.OK().getText(createIdentityInfo(identity));
		};
	}

	private String createIdentityInfo(Identity identity) {
		return String.format(
			"Identity:\nIP:%s\nCSRF:%s\nUser:%s\nSpace:%s\nFlash:%s",
			identity.getIP(), identity.getCsrfToken(), identity.getUser(), identity.getSessionSpace(),
			Implode.implode(" \n", identity.getFlashMessages())
		);
	}

	@Action(path="set")
	public ResponseAction set() {
		return (request, identity)->{
			int random = new Random().nextInt(10);
			LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
			identity.getSessionSpace().put("A", now);
			identity.getSessionSpace().put("B", random);

			identity.addFlashMessage("info", "Info 1: " + random);
			identity.addFlashMessage("info", "Info 2: " + random);
			identity.addFlashMessage("error", "Error: " + random);

			return Response.OK().getText(String.format("A:%s\nB:%s", now, random));
		};
	}

	@Action(path="login")
	public ResponseAction login() {
		return (request, identity)->{
			int random = new Random().nextInt(10);
			identity.login(random);

			return Response.OK().getText(String.format("User:%s", random));
		};
	}

	@Action(path="logout")
	public ResponseAction logout() {
		return (request, identity)->{
			identity.logout();
			return Response.OK().getText(createIdentityInfo(identity));
		};
	}

	@Action(path="form")
	public ResponseAction form() {
		return (request, identity)->{
			String sendLink = link.create(getClass(), c->c.formProcess());
			String token = identity.getCsrfToken();
			return Response.OK()
			.addHeader("content-type", "text/html")
			.getText(String.format("""
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8" />
	<title>TOTI | Examples | Identity Form</title>
</head>
<body>
	<div>%s</div>
	<form action="%s" method="post">
		<input type="submit" value="Send sync without CSRF">
	</form>
	<form action="%s" method="post">
		<input type="hidden" name="%s" value="%s">
		<input type="submit" value="Send sync with CSRF">
	</form>
	<div id="result"></div>
	<script type="text/javascript">
		function send(useToken) {
			var container = document.getElementById("result");
			container.innerText = "";

			fetch("%s", {
				method: "POST",
				headers: {
					"Content-Type": "application/x-www-form-urlencoded"
				},
				body: useToken ? new URLSearchParams({ %s: "%s"}).toString() : ""
			}).then((response)=>{
				return response.text();
			}).then((text)=>{
				container.innerText = text;
			}).catch((e)=>{
				console.error(e);
				container.innerText = "Something is wrong!";
			});
		}
	</script>
	<button onclick="send(false)">Send async without CSRF</button>
	<button onclick="send(true)">Send async with CSRF</button>
</body>
</html>
""",
				token, sendLink, sendLink,
				DefaultSession.CSRF_TOKEN_NAME, token,
				sendLink,
				DefaultSession.CSRF_TOKEN_NAME, token
			));
		};
	}

	@Action(path="process-form", methods={HttpMethod.POST})
	public ResponseAction formProcess() {
		return (request, identity)->{
			return Response.OK()
			//.addHeader("content-type", "text/html")
			.getText(
				"Cookie: " + request.getHeaders().getCookieValue(DefaultSession.SESSION_COOKIE_NAME)
				+ "\n" +
				"Header: " + request.getHeaders().getHeader(DefaultSession.SESSION_HEADER_NAME)
				+ "\n" +
				"CSRF: " + identity.getCsrfToken() + " " + identity.isCsrfTokenVerified()
			);
		};
	}

}
