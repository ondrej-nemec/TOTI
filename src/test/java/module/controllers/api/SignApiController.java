package module.controllers.api;

import java.util.HashMap;
import java.util.Map;

import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import toti.security.Authenticator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.Secured;
import toti.authentication.AuthentizationException;
import toti.security.Identity;
import toti.security.User;
import toti.response.Response;

@Controller("sign")
public class SignApiController {

	private Identity identity;
	
	private Authenticator authenticator;
	
	public SignApiController(Identity identity, Authenticator authenticator) {
		this.authenticator = authenticator;
		this.identity = identity;
	}
/*
	@Method({HttpMethod.POST})
	@Action("in")
	public Response login(@Param("username") String username) {
		try {
			return generateToken(authenticator.login(new User(username, null), identity));
		} catch (Exception e) {
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}
*/
	@Method({HttpMethod.POST})
	@Action("out")
	@Secured
	public Response logout() {
		authenticator.logout(identity);
		return Response.getJson(new HashMap<>());
	}
/*
	@Method({HttpMethod.POST})
	@Action("refresh")
	@Secured // own active token used
	public Response refresh() {
		try {
			return generateToken(authenticator.refresh(identity));
		} catch (AuthentizationException e) {
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}
*/
	private Response generateToken(String bearer) {
		Map<String, Object> json = new HashMap<>();
		json.put("access_token", bearer);
		json.put("token_type", "bearer");
		json.put("refresh_token", bearer);
		json.put("expires_in", authenticator.getExpirationTime());
		return Response.getJson(json);
	}

}
