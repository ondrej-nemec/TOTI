package toti.samples;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;

import ji.common.structures.MapInit;
import ji.translator.LanguageSettings;
import ji.translator.Locale;
import toti.TotiServer;
import toti.TotiServerFactory;
import toti.database.DatabaseExtension;
import toti.extension.templating.TemplateExtension;
import toti.extensions.auth.AuthenticationExtension;
import toti.http.SslCredentials;
import toti.samples.application.ApplicationModule;
import toti.samples.templating.TemplatingModule;
import toti.samples.ui.UiModule;
import toti.translation.TranslatorExtension;
import toti.ui.UiExtension;

public class SamplesMain {

	public static void main(String[] args) {
		// createWithDefaultSettings();
		createAndSetProgrammatically();
		// createWithFileSettings();
	}

	protected static void createWithDefaultSettings() {
		try {
			TotiServerFactory serverFactory = new TotiServerFactory();

			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("samples", (env, applicationFactory)->{
				// TODO applicationFactory.setUrlPattern(null);
				
				// optional: add extensions, always in code
				applicationFactory.addExtension(new AuthenticationExtension());
				applicationFactory.addExtension(new UiExtension());
				// TranslatorExtension require settings
				// TemplateExtension require settings
				// DatabaseExtension require settings
				
				return applicationFactory.create(Arrays.asList(
					new ApplicationModule(),
					new UiModule(),
					new TemplatingModule()
				), LogManager.getLogger("samples-toti"));
			}, Arrays.asList("localhost", "127.0.0.1"), null);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static void createWithFileSettings() {
		try {
			TotiServerFactory serverFactory = new TotiServerFactory("toti/samples/fileConfiguration.properties");
			
			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("samples", (env, applicationFactory)->{
				// TODO applicationFactory.setUrlPattern(null);
				
				// optional: add extensions, always in code
				applicationFactory.addExtension(new AuthenticationExtension());
				applicationFactory.addExtension(new UiExtension());
				applicationFactory.addExtension(new TranslatorExtension(env, LogManager.getLogger("translate")));
				applicationFactory.addExtension(new TemplateExtension(env, LogManager.getLogger("template")));
				applicationFactory.addExtension(new DatabaseExtension(LogManager.getLogger("database")));
				
				return applicationFactory.create(Arrays.asList(
					new ApplicationModule(),
					new UiModule(),
					new TemplatingModule()
				), LogManager.getLogger("samples-toti"));
			}, Arrays.asList("localhost", "127.0.0.1"), null);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void createAndSetProgrammatically() {
		try {
			TotiServerFactory serverFactory = new TotiServerFactory();
			serverFactory.setCharset("utf-8");
			// TODO to doc
			/* SslCredentials cred = new SslCredentials();
			cred.setCertificateStore("certificates/cert.p12", "betasecret", "PKCS12");
			cred.setSniHostCheck(false);
			serverFactory.setCerts(Optional.of(cred));*/
			serverFactory.setMaxRequestBodySize(20*1024); // 20 kB
			serverFactory.setHttpPort(8080);
			serverFactory.setHttpsPort(8443);
			serverFactory.setReadTimeout(90000); // 90s
			serverFactory.setThreadPool(100);
			
			TotiServer server = serverFactory.create(LogManager.getLogger("toti"));
			server.addApplication("samples", (env, applicationFactory)->{
				// set
				applicationFactory.setAutoStart(true);
				applicationFactory.setDevelopIpAdresses(Arrays.asList("/127.0.0.1", "/0:0:0:0:0:0:0:1"));
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
				applicationFactory.addExtension(new AuthenticationExtension());
				applicationFactory.addExtension(new UiExtension());
				applicationFactory.addExtension(new TranslatorExtension(
					new LanguageSettings("en", Arrays.asList(new Locale("en", true, Arrays.asList("en_GB")))),
					LogManager.getLogger("translate")
				));
				applicationFactory.addExtension(new TemplateExtension(
					"temp/samples", false, false, LogManager.getLogger("template")
				));
				applicationFactory.addExtension(new DatabaseExtension(LogManager.getLogger("database")));
				// validation extension doesn't need register
				
				return applicationFactory.create(Arrays.asList(
					new ApplicationModule(),
					new UiModule(),
					new TemplatingModule()
				), LogManager.getLogger("samples-toti"));
			}, Arrays.asList("localhost", "127.0.0.1"), null);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
