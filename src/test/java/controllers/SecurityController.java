package controllers;

import java.util.HashMap;
import java.util.Map;

import mvc.annotations.inject.Authenticate;
import mvc.annotations.inject.ClientIdentity;
import mvc.annotations.url.Action;
import mvc.annotations.url.Controller;
import mvc.annotations.url.Method;
import mvc.annotations.url.Param;
import mvc.annotations.url.Secured;
import mvc.authentication.Authenticator;
import mvc.authentication.AuthentizationException;
import mvc.authentication.Identity;
import mvc.control.Form;
import mvc.control.inputs.Submit;
import mvc.control.inputs.Text;
import mvc.response.Response;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;

@Controller("security")
public class SecurityController {
	
	@Action("index")
	public Response index() {
		Form form = new Form("loginForm", "/security/login", true);
		form.setFormMethod("post");
		form.addInput(Text.input("username", true));
		
		form.addInput(Submit.create("Log in", "submit").setOnResponseFunction("loginUser"));
		Map<String, Object> params = new HashMap<>();
		params.put("loginForm", form);
		return Response.getTemplate("login.jsp", params);
	}
	
	/***********************/

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
	@Action("login")
	public Response login(@Param("username") String username) {
		return generateToken(username);
	}

	@Method({HttpMethod.POST})
	@Action("logout")
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
