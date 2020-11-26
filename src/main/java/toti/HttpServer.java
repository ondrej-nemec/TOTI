package toti;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import common.Logger;
import logging.LoggerFactory;
import socketCommunication.Server;
import socketCommunication.ServerSecuredCredentials;
import toti.authentication.Language;
import toti.authentication.UserSecurity;
import toti.templating.TemplateFactory;
import translator.PropertiesTranslator;
import translator.Translator;

public class HttpServer {
	
	private final Server server;
	
	public HttpServer(
			int port,
			int threadPool,
    		long readTimeout,
    		ResponseHeaders headers, // TODO only list ??
    		Optional<ServerSecuredCredentials> certs,
    		String tempPath,
    		List<Module> modules,
    		String resourcesPath,
    		Function<Locale, Translator> translator,
    		UserSecurity security,
    		int maxUploadFileSize,
    		Optional<List<String>> allowedUploadFileTypes,
    		String charset,
    		String defLang,
    		Logger logger,
    		boolean deleteDir,
    		boolean dirResponseAllowed) throws Exception {

		Router router = new Router();
		Map<String, TemplateFactory> controllers = new HashMap<>();
		Map<String, TemplateFactory> templateFactories = new HashMap<>();
		String[] trans = new String[modules.size()];
		int i = 0;
		for (Module module : modules) {
			module.addRoutes(router);
			TemplateFactory templateFactory = new TemplateFactory(
					tempPath, module.getTemplatesPath(), templateFactories, deleteDir
			);
			controllers.put(module.getControllersPath(), templateFactory);
			templateFactories.put(module.getName(), templateFactory);
			trans[i++] = module.getTranslationPath();
		};
		if (translator == null) {
			translator = new Function<Locale, Translator>() {
				private final Map<Locale, Translator> translators = new HashMap<>();
				@Override
				public Translator apply(Locale locale) {
					if (translators.get(locale) == null) {
						translators.put(
							locale,
							new PropertiesTranslator(LoggerFactory.getLogger("translator"), locale, trans)
						);
					}
					return translators.get(locale);
				}
			};
		}
		ResponseFactory response = new ResponseFactory(
				headers,
				new Language(defLang),
				resourcesPath,
				router,
				controllers,
				translator,
				security,
				charset,
				dirResponseAllowed,
				logger
		);
				
		this.server = Server.create(
				port,
				threadPool,
				readTimeout,
				response,
				certs,
				maxUploadFileSize,
				allowedUploadFileTypes,
				charset,
				logger
		);
	}
	
	public void start() {
		server.start();
	}
	
	public void stop() throws InterruptedException {
		server.stop();
	}
	
}
