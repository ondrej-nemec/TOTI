package toti.examples.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;

import toti.core.Application;
import toti.core.ApplicationFactory;
import toti.core.TotiServer;
import toti.core.TotiServerFactory;
import toti.examples.demo.modules.ApplicationModule;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.SslCredentials;

public class TotiExamplesRunner {

	// TODO spravne def nastaveni hostname a path + example jak pouzit

	public static void main(String[] args) {
		createWithDefaultSettings();
		createAndSetProgrammatically();
		createWithXmlFileSettings();
		createWithPropertiesFileSettings();
	}


	private static Application initApplication(ApplicationFactory applicationFactory) {
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
	}


	private static void createWithDefaultSettings() {
		try {
			TotiServerFactory serverFactory = new TotiServerFactory();

			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("demo", (env, applicationFactory)->{
				return initApplication(applicationFactory);
			}, null, null);
			server.start();
		} catch (Exception e) {
			LogManager.getLogger("toti").error("Default init fails", e);
		}
	}
	
	private static void createWithPropertiesFileSettings() {
		try {
			TotiServerFactory serverFactory = new TotiServerFactory("fileConfiguration.properties");
			
			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("demo", (env, applicationFactory)->{
				return initApplication(applicationFactory);
			}, null, null);
			server.start();
		} catch (Exception e) {
			LogManager.getLogger("toti").error("Properties init fails", e);
		}
	}
	
	private static void createWithXmlFileSettings() {
		try {
			TotiServerFactory serverFactory = new TotiServerFactory("fileConfiguration.xml");
			
			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("demo", (env, applicationFactory)->{
				return initApplication(applicationFactory);
			}, null, null);
			server.start();
		} catch (Exception e) {
			LogManager.getLogger("toti").error("Xml init fails", e);
		}
	}

	private static void createAndSetProgrammatically() {
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
			server.addApplication("demo", (env, applicationFactory)->{
				// set
				applicationFactory.setAutoStart(true);
				applicationFactory.setDirDefaultFile("index.html");
				applicationFactory.setDirResponseAllowed(true);
				applicationFactory.setHeaders(
					new MapInit<String, List<Object>>()
					.append("Access-Control-Allow-Origin", Arrays.asList("*"))
					.append("Server", Arrays.asList("TOTI", "Program settings"))
					.toMap()
				);
				applicationFactory.setLogsPath("logs");
				applicationFactory.setResourcesPath("www");

				return initApplication(applicationFactory);
			}, null, null);
			server.start();
		} catch (Exception e) {
			LogManager.getLogger("toti").error("Program init fails", e);
		}
	}
}
