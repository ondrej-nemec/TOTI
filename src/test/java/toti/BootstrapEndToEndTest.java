package toti;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import database.DatabaseConfig;
import helper.Action;
import helper.Rules;
import interfaces.AclDestination;
import interfaces.AclRole;
import interfaces.AclUser;
import interfaces.RulesDao;
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
				new EntityModule()
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
					
					// TODO little bit another way
					try {
						factory.setUserSecurity(new UserSecurity(
							"/sign/in",
							(identity)->new AclUser() {
								@Override public List<AclRole> getRoles() { return new LinkedList<>(); }
								@Override public int getRank() { return 0; }
								@Override public String getId() { return ""; }
							},
							new RulesDao() {
								@Override public Rules getRulesForUserAndGroups(AclUser user, AclDestination domain) {
									return new Rules(Action.ADMIN);
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
