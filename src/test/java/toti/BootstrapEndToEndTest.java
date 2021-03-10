package toti;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import database.DatabaseConfig;
import example.ExampleModule;
import acl.Action;
import acl.structures.Rules;
import acl.structures.AclDestination;
import acl.structures.AclRole;
import acl.structures.AclUser;
import acl.RulesDao;
import logging.LoggerFactory;
import module.EntityModule;
import toti.HttpServerFactory;
import toti.application.Application;
import toti.authentication.UserSecurity;
import toti.registr.Registr;
import utils.Env;

public class BootstrapEndToEndTest {

	public static void main(String[] args) {
		try {
			List<Module> modules = Arrays.asList(
				new EntityModule(),
				new ExampleModule()
			);
			Application.APP_CONFIG_FILE = null;
			Application a = new Application(modules) {
				@Override
				public HttpServerFactory createServerFactory(Env env, Registr registr) throws Exception {
					HttpServerFactory factory = new HttpServerFactory();
					factory.setPort(81);
					factory.setThreadPool(10);
					factory.setReadTimeout(60000);
					factory.setLogger(LoggerFactory.getLogger("server"));
					factory.setMinimalize(false);
					factory.setDeleteTempJavaFiles(false);
					factory.setHeaders(new ResponseHeaders(Arrays.asList(
						"CSP:frame-ancestors 'none'" // nacteni stranky ve framu
						, "Content-Security-Policy-Report-Only"
							+ " script-src 'strict-dynamic' 'nonce-{nonce}' 'unsafe-inline' http: https:;"
							+ " object-src 'none';"
							+ " form-action 'self';"
							+ " report-uri '/entity/api/entity/reporting'"
						, "Access-Control-Allow-Origin: *"
					)));
					factory.setAllowedUploadFileTypes(Optional.empty());
					factory.setMaxUploadFileSize(1000000);
				//	factory.setDevelopIpAdresses(Arrays.asList());
					
					// TODO little bit another way
					try {
						factory.setUserSecurity(new UserSecurity(
							"/example-module/sign/in",
							(identity)->new AclUser() {
								@Override public List<AclRole> getRoles() { return new LinkedList<>(); }
								@Override public int getRank() { return 0; }
								@Override public String getId() { return ""; }
							},
							new RulesDao() {
								@Override public Rules getRulesForUserAndGroups(AclUser user, AclDestination domain) {
									return Rules.forUserWithOwner(Action.ADMIN, Arrays.asList());
								}
							},
							1000*60*10,
							"",
							LoggerFactory.getLogger("auth")
						));
					} catch (RuntimeException ignored) {}
					return factory;
				}
				
				@Override
				public DatabaseConfig createDatabaseConfig(Env env, List<String> migrations) {
					return new DatabaseConfig(
						"postgresql",
						"//localhost:5432",
						true,
						"toti",
						"postgres",
						"1234",
						migrations,
						"Europe/Prague",
						5
					);
				}
			};
			a.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
