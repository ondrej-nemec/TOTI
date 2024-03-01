package toti.samples;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;

import ji.common.structures.MapInit;
import toti.ApplicationFactory;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.samples.application.ApplicationModule;
import toti.samples.templating.TemplatingModule;
import toti.samples.ui.UiModule;
import toti.ui.UiExtension;

public class SamplesMain {

	public static void main(String[] args) {
		try {
			SamplesMain sampleMain = getMain(-1);
			
			HttpServerFactory serverFactory = sampleMain.getServerFactory();
			// can be set only in code, optional
			serverFactory.setLoggerFactory((loggerName)->LogManager.getLogger(loggerName));
			
			HttpServer server = serverFactory.create();
			server.addApplication("samples", (env, applicationFactory)->{
				// set
				sampleMain.setApplicationFactory(applicationFactory);
				// can be set only in code, optional
				applicationFactory.setLoggerFactory((appName, loggerName)->{
					return LogManager.getLogger(appName + "_" + loggerName);
				});
				// TODO applicationFactory.setUrlPattern(null);
				applicationFactory.setTranslator(null); // TODO
				
				// optional: add extensions, always in code
				applicationFactory.addExtension(new UiExtension());
				
				return applicationFactory.create(Arrays.asList(
					new ApplicationModule(),
					new UiModule(),
					new TemplatingModule()
				));
			}, "localhost", "127.0.0.1");
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static SamplesMain getMain(int i) throws Exception {
		switch (i) {
			case 0: return createAndSetProgrammatically();
			case 1: return createWithFileSettings();
			default: return createWithDefaultSettings();
		}
	}
	
	private static SamplesMain createWithDefaultSettings() throws Exception {
		return new SamplesMain(new HttpServerFactory(), (applicationFactory)->{});
	}
	
	private static SamplesMain createWithFileSettings() throws Exception {
		return new SamplesMain(
			new HttpServerFactory("toti/samples/fileConfiguration.properties"),
			(applicationFactory)->{}
		);
	}
	
	private static SamplesMain createAndSetProgrammatically() throws Exception {
		HttpServerFactory serverFactory = new HttpServerFactory();
		serverFactory.setCharset("utf-8");
		serverFactory.setCerts(null); // TODO
		serverFactory.setMaxRequestBodySize(20*1024); // 20 kB
		serverFactory.setPort(8080);
		serverFactory.setReadTimeout(90000); // 90s
		serverFactory.setThreadPool(100);
		return new SamplesMain(serverFactory, (applicationFactory)->{
			applicationFactory.setUseProfiler(true);
			
			applicationFactory.setDatabase(null); // TODO
			applicationFactory.setLanguageSettings(null); // TODO
			
			applicationFactory.setAutoStart(true);
			applicationFactory.setDeleteTempJavaFiles(true);
			applicationFactory.setDevelopIpAdresses(Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1"));
			applicationFactory.setDirDefaultFile("index.html");
			applicationFactory.setDirResponseAllowed(true);
			applicationFactory.setHeaders(
				new MapInit<String, List<Object>>()
				.append("Access-Control-Allow-Origin", Arrays.asList("*"))
				.toMap()
			);
			applicationFactory.setLogsPath("logs");
			applicationFactory.setMinimalize(false);
			applicationFactory.setResourcesPath("www");
			applicationFactory.setTempPath("temp");
		});
	}
	
	private final HttpServerFactory serverFactory;
	private final Consumer<ApplicationFactory> setApplicationFactory;
	
	public SamplesMain(HttpServerFactory serverFactory, Consumer<ApplicationFactory> setApplicationFactory) {
		this.serverFactory = serverFactory;
		this.setApplicationFactory = setApplicationFactory;
	}
	
	public HttpServerFactory getServerFactory() {
		return serverFactory;
	}
	
	public void setApplicationFactory(ApplicationFactory applicationFactory) {
		setApplicationFactory.accept(applicationFactory);
	}
	
}
