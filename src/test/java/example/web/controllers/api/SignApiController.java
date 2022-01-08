package example.web.controllers.api;

import java.util.HashMap;
import java.util.Map;

import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.Secured;
import toti.authentication.AuthentizationException;
import toti.response.Response;
import toti.security.Authenticator;
import toti.security.Identity;
import toti.security.User;

@Controller("sign")
public class SignApiController {

	private Identity identity;
	
	private Authenticator authenticator;
	
	public SignApiController(Identity identity, Authenticator authenticator) {
		this.identity = identity;
		this.authenticator = authenticator;
	}

	@Method({HttpMethod.POST})
	@Action("in")
	public Response login(@Param("username") String username) {
		try {
			String bearer = authenticator.login(new User("User: " + username, new Perm()), identity);
			return generateToken(bearer);
		} catch (Exception e) {
			e.printStackTrace();
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
	public Response refresh() throws AuthentizationException {
		try {
			String bearer = authenticator.refresh(identity);
			return generateToken(bearer);
		} catch (Exception e) {
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}
	
	private Response generateToken(String bearer) {
		Map<String, Object> json = new HashMap<>();
		json.put("access_token", bearer);
		json.put("token_type", "bearer");
		json.put("refresh_token", bearer);
		json.put("expires_in", authenticator.getExpirationTime());
		return Response.getJson(json);
	}

}
