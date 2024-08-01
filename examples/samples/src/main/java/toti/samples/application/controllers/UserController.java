package toti.samples.application.controllers;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.request.AuthMode;
import toti.answers.response.Response;
import toti.http.enums.StatusCode;
import toti.samples.application.SampleUser;
import toti.samples.application.SessionUserProviderExample;

@Controller("users")
public class UserController {
	
	private final SessionUserProviderExample sipe;

	public UserController(SessionUserProviderExample sipe) {
		this.sipe = sipe;
	}
	
	@Action(path="login")
	// username is in URL - just for easy test
	public ResponseAction login(String username) {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			sipe.login(username);
			return Response.create(StatusCode.OK).getText("AUTH: user logged in: " + username);
		});
	}
	
	@Action(path="logout")
	@Secured
	public ResponseAction logout() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			sipe.logout(identity.getUser(SampleUser.class));
			return Response.create(StatusCode.OK).getText("AUTH: user logged out");
		});
	}
	
	@Action(path="check")
	public ResponseAction check() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			if (identity.isAnonymous()) {
				return Response.create(StatusCode.OK).getText("AUTH: no current user. Logged: " + sipe.getLoggedUsers());
			}
			return Response.create(StatusCode.OK).getText(
				"AUTH: current user: " + identity.getUser(SampleUser.class).getUserName()
				+ " Logged: " + sipe.getLoggedUsers()
			);
		});
	}
	
	@Action(path="not-secured")
	public ResponseAction notSecured() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("AUTH: not secured action");
		});
	}
	
	@Action(path="page")
	@Secured(AuthMode.COOKIE)
	public ResponseAction page() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("AUTH: action requires COOKIE token");
		});
	}
	
	@Action(path="form")
	@Secured(AuthMode.COOKIE_AND_CSRF)
	public ResponseAction form() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("AUTH: action requires COOKIE token and CSRF token");
		});
	}
	
	@Action(path="api")
	@Secured(AuthMode.HEADER)
	public ResponseAction api() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("AUTH: action requires HEADER token");
		});
	}
	
}
