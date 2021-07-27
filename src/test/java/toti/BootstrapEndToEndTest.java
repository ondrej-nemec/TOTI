package toti;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import database.DatabaseConfig;
import example.ExampleModule;
import common.functions.Env;
import common.functions.InputStreamLoader;
import common.structures.DictionaryValue;
import common.structures.MapDictionary;
import core.text.Text;
import core.text.basic.ReadText;
import module.EntityModule;
import toti.HttpServerFactory;
import toti.application.Application;
import toti.logging.TotiLogger;
import toti.registr.Registr;
import toti.security.Action;
import toti.security.User;
import toti.security.permissions.Permissions;
import toti.security.permissions.Rule;
import toti.security.permissions.Rules;
import translator.LanguageSettings;
import translator.Locale;

public class BootstrapEndToEndTest {

	public static void main(String[] args) {
		try {
			List<Module> modules = Arrays.asList(
				new EntityModule(),
				new ExampleModule()
			);
			Application.APP_CONFIG_FILE = null;
			Application a = new Application(modules, (content, registr)->{
				return new User("User: " + content, new Permissions() {
					
					@Override
					public Rules getRulesForDomain(String domain) {
						if (domain.equals("test1")) {
							return new Rules(null, Arrays.asList(
								new Rule(Action.UPDATE, ()->Arrays.asList())
							));
						}
						if (domain.equals("test2")) {
							return new Rules(null, Arrays.asList(
								new Rule(Action.UPDATE, ()->Arrays.asList())
							));
						}
						return new Rules(new Rule(Action.ADMIN, ()->Arrays.asList()), Arrays.asList());
					}
				});
			}, "/example-module/sign/in") {
				
				@Override
				public HttpServerFactory createServerFactory(Env env, Registr registr, String redirect) throws Exception {
					HttpServerFactory factory = new HttpServerFactory(TotiLogger.getLogger("toti"));
					factory.setRedirectNoLoggerdUser(redirect);
					factory.setPort(81);
					factory.setThreadPool(10);
					factory.setReadTimeout(60000);
					// factory.setLogger(LoggerFactory.getLogger("toti"));
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
					
					MapDictionary<String, Object> config = new DictionaryValue(Text.get().read((br)->{
						return ReadText.get().asString(br);
					}, InputStreamLoader.createInputStream(getClass(), "conf/lang.json"))).getDictionaryMap();
					List<Locale> locales = new LinkedList<>();
					config.getDictionaryMap("locales").forEach((locale, setting)->{
						locales.add(new Locale(
							locale.toString(), 
							setting.getDictionaryMap().getBoolean("isLeftToRight"),
							setting.getDictionaryMap().getList("substitutions")
						));
					});
					factory.setLanguageSettings(new LanguageSettings(
						config.getString("default"),
						locales
					));
					
				//	factory.setDevelopIpAdresses(Arrays.asList());
					
					// TODO little bit another way
					/*try {
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
					} catch (RuntimeException ignored) {}*/
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
