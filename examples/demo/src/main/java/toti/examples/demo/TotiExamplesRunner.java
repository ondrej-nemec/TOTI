package toti.examples.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;

import toti.core.TotiServer;
import toti.core.TotiServerFactory;
import toti.examples.demo.modules.ApplicationModule;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.SslCredentials;

public class TotiExamplesRunner {

	// TODO spravne def nastaveni hostname a path + example jak pouzit

	public static void main(String[] args) {
	//	createWithDefaultSettings();
		createAndSetProgrammatically();
	//	createWithFileSettings();
	}

	protected static void createWithDefaultSettings() {
		try {
			TotiServerFactory serverFactory = new TotiServerFactory();

			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("examples", (env, applicationFactory)->{
				applicationFactory.setDevModeFunc(ip->true); // dev mode
				// TODO applicationFactory.setUrlPattern(null);
				
				// optional: add extensions, always in code
				// TODO applicationFactory.addExtension(new AuthenticationExtension());
				// TODO applicationFactory.addExtension(new UiExtension());
				// TranslatorExtension require settings
				// TemplateExtension require settings
				// DatabaseExtension require settings
				
				return applicationFactory.create(Arrays.asList(
					new ApplicationModule()/*,
					new UiModule(),
					new TemplatingModule()*/
				), LogManager.getLogger("toti"));
			}, null, null);
			server.start();
		} catch (Exception e) {
			LogManager.getLogger("toti").error("Init fails", e);
		}
	}
	
	protected static void createWithFileSettings() {
		try {
			//TotiServerFactory serverFactory = new TotiServerFactory("fileConfiguration.properties");
			TotiServerFactory serverFactory = new TotiServerFactory("fileConfiguration.xml");
			
			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("examples", (env, applicationFactory)->{
				applicationFactory.setDevModeFunc(ip->true); // dev mode
				// TODO applicationFactory.setUrlPattern(null);
				
				// optional: add extensions, always in code
				/*applicationFactory.addExtension(new AuthenticationExtension());
				applicationFactory.addExtension(new UiExtension());
				applicationFactory.addExtension(new TranslatorExtension(env, LogManager.getLogger("translate")));
				applicationFactory.addExtension(new TemplateExtension(env, LogManager.getLogger("template")));
				applicationFactory.addExtension(new DatabaseExtension(LogManager.getLogger("database")));*/
				
				return applicationFactory.create(Arrays.asList(
					new ApplicationModule()/*,
					new UiModule(),
					new TemplatingModule()*/
				), LogManager.getLogger("toti"));
			}, null, null);
			server.start();
		} catch (Exception e) {
			LogManager.getLogger("toti").error("Init fails", e);
		}
	}

	protected static void createAndSetProgrammatically() {
		try {
			TotiServerFactory serverFactory = new TotiServerFactory();
			serverFactory.setCharset("utf-8");
			serverFactory.setMaxRequestBodySize(20*1024); // 20 kB
			serverFactory.setHttpPort(81);
			serverFactory.setHttpsPort(444);
			serverFactory.setReadTimeout(90000); // 90s
			serverFactory.setThreadPool(100);
			
			SslCredentials cred = new SslCredentials();
			cred.setCertificateStore("certificates/cert.p12", "betasecret", "PKCS12");
			cred.setSniHostCheck(false);
			serverFactory.setCerts(Optional.of(cred));
			
			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("examples", (env, applicationFactory)->{
				// set
				applicationFactory.setAutoStart(true);
				applicationFactory.setDevModeFunc(ip->true); // dev mode
				applicationFactory.setDirDefaultFile("index.html");
				applicationFactory.setDirResponseAllowed(true);
				applicationFactory.setHeaders(
					new MapInit<String, List<Object>>()
					.append("Access-Control-Allow-Origin", Arrays.asList("*"))
					.toMap()
				);
				applicationFactory.setLogsPath("logs");
				applicationFactory.setResourcesPath("www");
				// TODO applicationFactory.setUrlPattern(null);
				
				// optional: add extensions, always in code
				/*applicationFactory.addExtension(new AuthenticationExtension());
				applicationFactory.addExtension(new UiExtension());
				applicationFactory.addExtension(new TranslatorExtension(
					new LanguageSettings("en", Arrays.asList(new Locale("en", true, Arrays.asList("en_GB")))),
					LogManager.getLogger("translate")
				));
				applicationFactory.addExtension(new TemplateExtension(
					"temp/samples", false, false, LogManager.getLogger("template")
				));
				applicationFactory.addExtension(new DatabaseExtension(LogManager.getLogger("database")));*/
				// validation extension doesn't need register
				
				return applicationFactory.create(Arrays.asList(
					new ApplicationModule()/*,
					new UiModule(),
					new TemplatingModule()*/
				), LogManager.getLogger("toti"));
			}, null, null);
			server.start();
		} catch (Exception e) {
			LogManager.getLogger("toti").error("Init fails", e);
		}
	}
}
