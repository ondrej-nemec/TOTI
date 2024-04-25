package toti.samples.application.controllers;

import toti.annotations.Controller;
import toti.samples.application.SessionUserProviderExample;

@Controller("users")
public class UserController {
	
	private final SessionUserProviderExample sipe;

	public UserController(SessionUserProviderExample sipe) {
		this.sipe = sipe;
	}
	
	// TODO prihlaseni + prava + prace s identity
	
}
