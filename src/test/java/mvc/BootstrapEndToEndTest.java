package mvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;

import controllers.LoginController;
import controllers.TestController;
import helper.Action;
import helper.AuthorizationHelper;
import helper.Rules;
import interfaces.AclDestination;
import interfaces.AclUser;
import interfaces.RulesDao;
import logging.LoggerFactory;
import mvc.authentication.Authenticator;
import mvc.registr.Registr;
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

			Authenticator authenticator = new Authenticator(
					120000,
					"secretSalt",
					LoggerFactory.getLogger("security")
			);
			
			AuthorizationHelper authorizator = new AuthorizationHelper(
					new RulesDao() {						
						@Override
						public Rules getRulesForUser(AclUser user, AclDestination domain) {
							return new Rules(Action.UNDEFINED, Action.UNDEFINED, Arrays.asList(Action.UNDEFINED));
						}
					},
					LoggerFactory.getLogger("security")
			);
			
			Router router = new Router();
			router.addUrl("/jsgrid", "/base/grid");
			router.addUrl("", "/index.html");
			router.addUrl("/grid/index.html", "/grid");
			
			Map<String, String> folders = new HashMap<>();
			folders.put("controllers", "templates");
			folders.put("controllers2", "templates2");
			
			Bootstrap b = new Bootstrap(
					80, 10, 60000,
					new ResponseHeaders(
						RandomStringUtils.randomAlphanumeric(50),
						Arrays.asList(
							"Access-Control-Allow-Origin: *"
						)
					),
					Optional.empty(),
					"temp", folders, "www",
					router,
					(loc)->new PropertiesTranslator(LoggerFactory.getLogger("translator"), "messages"),
					authenticator,
					authorizator,
					(ident)->{
						return null;
					},
					"utf-8",
					"cs_CZ",
					LoggerFactory.getLogger("server"), false
			);
			b.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
