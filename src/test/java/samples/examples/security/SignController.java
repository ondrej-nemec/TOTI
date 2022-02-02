package samples.examples.security;

import java.util.HashMap;
import java.util.Map;

import ji.common.Logger;
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

@Controller("sign")
public class SignController {

	private final Identity identity;
	private final Authenticator authenticator;
	private final Logger logger;
	
	public SignController(Identity identity, Authenticator authenticator, Logger logger) {
		this.identity = identity;
		this.authenticator = authenticator;
		this.logger = logger;
	}

	/**
	 * Returns default page with login form and action buttons
	 * @return http://localhost:8080/examples/sign/index
	 */
	@Action("index")
	public Response indexPage() {
		return Response.getTemplate("/login.jsp", new HashMap<>());
	}

	/**
	 * Login user. Here, user is loaded by username. Database or LDAP authentication can be used
	 * @return http://localhost:8080/examples/sign/in
	 
	@Method({HttpMethod.POST})
	@Action("in")
	public Response login(@Param("username") String username) {
		try {
			String bearer = authenticator.login(null, identity); // TODO user
			logger.info("User loged in " + username);
			return generateToken(bearer);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}
*/
	/**
	 * Log out user
	 * @return http://localhost:8080/examples/sign/out
	 */
	@Method({HttpMethod.POST})
	@Action("out")
	@Secured
	public Response logout() {
		logger.info("User loged out " + identity.getUser().getId());
		authenticator.logout(identity);
		return Response.getJson(new HashMap<>());
	}
	
	/**
	 * Refresh user token. The token is active only for short time.
	 * Simple request on any page does not increase token timeout.
	 * @return http://localhost:8080/examples/sign/refresh
	 
	@Method({HttpMethod.POST})
	@Action("refresh")
	@Secured
	public Response refresh() throws AuthentizationException {
		try {
			String bearer = authenticator.refresh(identity);
			logger.info("User refresh " + identity.getUser().getId());
			return generateToken(bearer);
		} catch (Exception e) {
			return Response.getJson(StatusCode.INTERNAL_SERVER_ERROR, new HashMap<>());
		}
	}
	*/
	/**
	 * login and refresh response for TOTI JS
	 * If TOTI JS is not used, response can be another - fe. simple bearer
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
