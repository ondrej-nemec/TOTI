package samples.tutorial1.web.api;

import java.util.Map;

import ji.common.structures.MapInit;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import samples.tutorial1.services.EdgeControlPermissions;
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
	
	public static final Map<String, User> USERS = new MapInit<String, User>()
			.append("user1", new User("user1-id", new EdgeControlPermissions()).setProperty("name", "User 1"))
			.toMap();
	private final Authenticator authenticator;
	private final Translator translator;
	private final Identity identity;
	
	public SignApiController(Authenticator authenticator, Translator translator, Identity identity) {
		this.authenticator = authenticator;
		this.translator = translator;
		this.identity = identity;
	}

	@Action("in")
	@Method(HttpMethod.POST)
	public Response login(@Param("username") String username, @Param("password") String password) {
		try {
			// simple user validation
			if (!USERS.containsKey(username) || !"1234".equals(password)) {
				return Response.getJson(
					StatusCode.BAD_REQUEST, 
					new MapInit<>()
					.append("form", translator.translate("messages.sign.invalid-credentials"))
					.toMap()
				);
			}
			return Response.getJson(authenticator.login(USERS.get(username), identity));
		} catch (AuthentizationException e) {
			e.printStackTrace();
			return Response.getText(StatusCode.INTERNAL_SERVER_ERROR, translator.translate("messages.sign.login-failed"));
		}
	}
	
	@Action("out")
	@Method(HttpMethod.POST)
	@Secured
	public Response logout() {
		authenticator.logout(identity);
		return Response.getText(""); // 202 response
	}
	
	@Action("refresh")
	@Secured
	public Response refresh() {
		return Response.getText(""); // 202 response
	}
}
