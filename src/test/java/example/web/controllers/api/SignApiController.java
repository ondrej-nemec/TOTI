package example.web.controllers.api;

import java.util.HashMap;
import java.util.Map;

import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;
import toti.annotations.inject.Authenticate;
import toti.annotations.inject.ClientIdentity;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Method;
import toti.annotations.url.Param;
import toti.annotations.url.Secured;
import toti.authentication.AuthentizationException;
import toti.response.Response;
import toti.security.Authenticator;
import toti.security.Identity;

@Controller("sign")
public class SignApiController {

	@ClientIdentity
	private Identity identity;
	
	@Authenticate
	private Authenticator authenticator;
	
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	
	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}
	
	@Method({HttpMethod.POST})
	@Action("in")
	public Response login(@Param("username") String username) {
		try {
			return generateToken(username);
		} catch (Exception e) {
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}

	@Method({HttpMethod.POST})
	@Action("out")
	@Secured
	public Response logout() {
		authenticator.logout(identity);
		return Response.getJson(new HashMap<>());
	}
	
	@Method({HttpMethod.POST})
	@Action("refresh")
	@Secured // own active token used
	public Response refresh() {
		return generateToken(identity.getContent());
	}
	
	private Response generateToken(String username) {
		try {
			String bearer = authenticator.login(username, identity);
			Map<String, Object> json = new HashMap<>();
			json.put("access_token", bearer);
			json.put("token_type", "bearer");
			json.put("refresh_token", bearer);
			json.put("expires_in", authenticator.getExpirationTime());
			return Response.getJson(json);
		} catch (AuthentizationException e) {
			e.printStackTrace();
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}

}
