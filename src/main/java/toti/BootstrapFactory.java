package toti;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import common.Logger;
import helper.AuthorizationHelper;
import interfaces.AclUser;
import socketCommunication.ServerSecuredCredentials;
import toti.authentication.Identity;
import translator.Translator;

public class BootstrapFactory {
	
	// todo maybe regist here too
	private Router router = new Router();
	private ResponseHeaders headers = new ResponseHeaders(Arrays.asList(
			"Access-Control-Allow-Origin: *"
	));
	
	// private Map<String, String> folders;
	
	// TODO solve this section
	private AuthorizationHelper authorizator;
	private Function<Identity, AclUser> identityToUser;
	private Logger securityLogger;
	
	private Function<Locale, Translator> translator;
	private Logger logger;
	
	private int port = 80;
	private int threadPool = 5;
	private String tokenSalt = "";
	private long tokenExpirationTime = 1000 * 60 * 10;
	private long readTimeout = 60000;
	private Optional<ServerSecuredCredentials> certs = Optional.empty();
	private String tempPath = "temp";
	private String charset = "UTF-8";
	private String defLang = Locale.getDefault().toString();
	private String resourcesPath = "www";
	private boolean deleteDir = false;
	private boolean dirResponseAllowed = true;
	private int maxUploadFileSize = 0;
	private Optional<List<String>> allowedUploadFileTypes = Optional.of(new LinkedList<>());
	
	public Bootstrap get(Map<String, String> folders) throws Exception {
		return new Bootstrap(
				port, threadPool, readTimeout, headers,
				certs, tempPath, folders, resourcesPath, router,
				translator, authorizator, identityToUser,
				maxUploadFileSize, allowedUploadFileTypes,
				charset, defLang, tokenSalt, tokenExpirationTime,
				logger, securityLogger, deleteDir, dirResponseAllowed
		);
	}

	public BootstrapFactory setDirResponseAllowed(boolean dirResponseAllowed) {
		this.dirResponseAllowed = dirResponseAllowed;
		return this;
	}

	public BootstrapFactory setMaxUploadFileSize(int maxUploadFileSize) {
		this.maxUploadFileSize = maxUploadFileSize;
		return this;
	}

	public BootstrapFactory setAllowedUploadFileTypes(Optional<List<String>> allowedUploadFileTypes) {
		this.allowedUploadFileTypes = allowedUploadFileTypes;
		return this;
	}

	public BootstrapFactory setRouter(Router router) {
		this.router = router;
		return this;
	}

	public BootstrapFactory setHeaders(ResponseHeaders headers) {
		this.headers = headers;
		return this;
	}

	public BootstrapFactory setAuthorizator(AuthorizationHelper authorizator) {
		this.authorizator = authorizator;
		return this;
	}

	public BootstrapFactory setIdentityToUser(Function<Identity, AclUser> identityToUser) {
		this.identityToUser = identityToUser;
		return this;
	}

	public BootstrapFactory setSecurityLogger(Logger securityLogger) {
		this.securityLogger = securityLogger;
		return this;
	}

	public BootstrapFactory setTranslator(Function<Locale, Translator> translator) {
		this.translator = translator;
		return this;
	}

	public BootstrapFactory setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	public BootstrapFactory setPort(int port) {
		this.port = port;
		return this;
	}

	public BootstrapFactory setThreadPool(int threadPool) {
		this.threadPool = threadPool;
		return this;
	}

	public BootstrapFactory setTokenSalt(String tokenSalt) {
		this.tokenSalt = tokenSalt;
		return this;
	}

	public BootstrapFactory setTokenExpirationTime(long tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
		return this;
	}

	public BootstrapFactory setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public BootstrapFactory setCerts(Optional<ServerSecuredCredentials> certs) {
		this.certs = certs;
		return this;
	}

	public BootstrapFactory setTempPath(String tempPath) {
		this.tempPath = tempPath;
		return this;
	}

	public BootstrapFactory setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public BootstrapFactory setDefLang(String defLang) {
		this.defLang = defLang;
		return this;
	}

	public BootstrapFactory setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
		return this;
	}

	public BootstrapFactory setDeleteDir(boolean deleteDir) {
		this.deleteDir = deleteDir;
		return this;
	}

}
