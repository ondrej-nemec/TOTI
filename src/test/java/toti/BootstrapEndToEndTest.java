package toti;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ji.database.DatabaseConfig;
import example.ExampleModule;
import ji.common.functions.Env;
import ji.common.functions.InputStreamLoader;
import ji.common.structures.DictionaryValue;
import ji.common.structures.MapDictionary;
import ji.files.text.Text;
import ji.files.text.basic.ReadText;
import module.EntityModule;
import toti.HttpServerFactory;
import ji.translator.LanguageSettings;
import ji.translator.Locale;

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
				public HttpServerFactory createServerFactory(Env env) throws Exception {
					HttpServerFactory factory = new HttpServerFactory();
					factory.setPort(81);
					factory.setThreadPool(10);
					factory.setReadTimeout(60000);
					// factory.setLogger(LoggerFactory.getLogger("toti"));
					factory.setMinimalize(false);
					factory.setDeleteTempJavaFiles(false);
					factory.setHeaders(Arrays.asList(
						"CSP:frame-ancestors 'none'" // nacteni stranky ve framu
						, "Content-Security-Policy-Report-Only"
							+ " script-src 'strict-dynamic' 'nonce-{nonce}' 'unsafe-inline' http: https:;"
							+ " object-src 'none';"
							+ " form-action 'self';"
							+ " report-uri '/entity/api/entity/reporting'"
						, "Access-Control-Allow-Origin: *"
					));
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
					/*
					factory.setDevelopIpAdresses(Arrays.asList());
					/*/
					factory.setUseProfiler(true);
					//*/
					
					
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
					//*
					return null;
					/*/
					return new DatabaseConfig(
						"postgresql",
						"//localhost:5432",
						true,
						"toti",
						"postgres",
						"1234",
						migrations,
						5
					);
					//*/
				}
			};
			a.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
