package toti;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import adminer.AdminerModule;
import logging.LoggerFactory;
import module.ModuleConfig;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.ResponseHeaders;
import toti.registr.Registr;

public class BootstrapEndToEndTest {

	public static void main(String[] args) {
		try {
			List<Module> configs = Arrays.asList(
				// TODO
				new AdminerModule(),
				new ModuleConfig()
			);
			/*
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
			*/
			for (Module module : configs) {
				module.initInstances(null, Registr.get(), null, null);
			}
			
			HttpServer b = new HttpServerFactory()
					.setPort(81)
					//.setTranslator((loc)->new PropertiesTranslator(LoggerFactory.getLogger("translator"), "messages"))
					// .setAuthorizator(authorizator)
					/*.setIdentityToUser((ident)->{
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
					})*/
					.setLogger(LoggerFactory.getLogger("server"))
					//.setSecurityLogger(LoggerFactory.getLogger("security"))
					.setHeaders(new ResponseHeaders(Arrays.asList(
							"Access-Control-Allow-Origin: *",
							"Access-Control-Allow-Credentials: true"
					)))
					.setMaxUploadFileSize(10*1024)
					.setAllowedUploadFileTypes(Optional.empty())
					.setMinimalize(false)
					.get(configs);
			/*
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
			*/
			b.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
