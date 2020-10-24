package mvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import controllers.ControlController;
import controllers.LoginController;
import controllers.SecurityController;
import controllers.TestController;
import helper.Action;
import helper.AuthorizationHelper;
import helper.Rules;
import interfaces.AclDestination;
import interfaces.AclRole;
import interfaces.AclUser;
import interfaces.RulesDao;
import logging.LoggerFactory;
import mvc.registr.Registr;
import mvc.validation.Validator;
import translator.PropertiesTranslator;

public class BootstrapEndToEndTest {

	public static void main(String[] args) {
		try {
			Registr.get().addFactory(TestController.class, ()->{
				return new TestController();
			});
			Registr.get().addFactory(controllers2.TestController.class, ()->{
				return new controllers2.TestController();
			});
			Registr.get().addFactory(LoginController.class, ()->{
				return new LoginController();
			});
			Registr.get().addFactory(SecurityController.class, ()->{
				return new SecurityController();
			});
			Validator val = new Validator(true);
			val.addRule(mvc.validation.ItemRules.forName("a", true)
					.setType(Integer.class).setMinValue(10).setMaxValue(20));
			val.addRule(mvc.validation.ItemRules.forName("b", true)
					.setMaxLength(20).setMinLength(10).setRegex("[0-9]", "Wrong regex"));
			Registr.get().addService("testValidator", val);
			Registr.get().addFactory(ControlController.class, ()->{
				return new ControlController();
			});
			
			
			AuthorizationHelper authorizator = new AuthorizationHelper(
					new RulesDao() {						
						@Override
						public Rules getRulesForUser(AclUser user, AclDestination domain) {
							switch (user.getId()) {
								case "name":
									return new Rules(Action.READ, Action.UNDEFINED, Arrays.asList(Action.UNDEFINED));
								case "name2":
									return new Rules(Action.ADMIN, Action.UNDEFINED, Arrays.asList(Action.UNDEFINED));
								default:
									return new Rules(Action.UNDEFINED, Action.UNDEFINED, Arrays.asList(Action.UNDEFINED));
							}
						}
					},
					LoggerFactory.getLogger("security")
			);
			
			Router router = new Router();
			router.addUrl("/jsgrid", "/base/grid");
			router.addUrl("", "/index.html");
			
			Map<String, String> folders = new HashMap<>();
			folders.put("controllers", "templates");
			folders.put("controllers2", "module2");
			
			Bootstrap b = new Bootstrap(
					80, 10, 60000,
					new ResponseHeaders(
						// RandomStringUtils.randomAlphanumeric(50),
						Arrays.asList(
							"Access-Control-Allow-Origin: *"
						)
					),
					Optional.empty(),
					"temp", folders, "www",
					router,
					(loc)->new PropertiesTranslator(LoggerFactory.getLogger("translator"), "messages"),
					authorizator,
					(ident)->{
						return new AclUser() {
							
							@Override
							public List<AclRole> getRoles() {
								return new LinkedList<>();
							}
							
							@Override
							public int getRank() {
								return 0;
							}
							
							@Override
							public String getId() {
								return ident.getContent();
							}
						};
					},
					"utf-8",
					"cs_CZ",
					"salt",
					90000,
					LoggerFactory.getLogger("server"),
					LoggerFactory.getLogger("security"),
					false
			);
			b.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
