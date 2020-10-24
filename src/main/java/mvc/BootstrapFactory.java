package mvc;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import common.Logger;
import helper.AuthorizationHelper;
import interfaces.AclUser;
import mvc.authentication.Identity;
import socketCommunication.ServerSecuredCredentials;
import translator.Translator;

public class BootstrapFactory {
	
	// todo maybe regist here too
	private Router router = new Router();
	private ResponseHeaders headers = new ResponseHeaders(new LinkedList<>());
	
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
	
	public Bootstrap get(Map<String, String> folders) throws Exception {
		return new Bootstrap(port, threadPool, readTimeout, headers, certs, tempPath, folders, resourcesPath, router, translator, authorizator, identityToUser, charset, defLang, tokenSalt, tokenExpirationTime, logger, securityLogger, deleteDir);
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public void setHeaders(ResponseHeaders headers) {
		this.headers = headers;
	}

	public void setAuthorizator(AuthorizationHelper authorizator) {
		this.authorizator = authorizator;
	}

	public void setIdentityToUser(Function<Identity, AclUser> identityToUser) {
		this.identityToUser = identityToUser;
	}

	public void setSecurityLogger(Logger securityLogger) {
		this.securityLogger = securityLogger;
	}

	public void setTranslator(Function<Locale, Translator> translator) {
		this.translator = translator;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setThreadPool(int threadPool) {
		this.threadPool = threadPool;
	}

	public void setTokenSalt(String tokenSalt) {
		this.tokenSalt = tokenSalt;
	}

	public void setTokenExpirationTime(long tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
	}

	public void setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setCerts(Optional<ServerSecuredCredentials> certs) {
		this.certs = certs;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setDefLang(String defLang) {
		this.defLang = defLang;
	}

	public void setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
	}

	public void setDeleteDir(boolean deleteDir) {
		this.deleteDir = deleteDir;
	}

}
