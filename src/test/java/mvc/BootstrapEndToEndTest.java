package mvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;

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
					120000,
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
