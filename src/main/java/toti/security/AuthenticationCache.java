package toti.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import ji.common.functions.FileExtension;

public class AuthenticationCache {
	
	class Token {
		long expired;
		String csrfToken;
		
		public Token() {}
		public Token(long expired, String csrf) {
			this.expired = expired;
			this.csrfToken = csrf;
		}
		@Override
		public String toString() {
			return String.format("Token: E=%s,T=%s", expired, csrfToken);
		}
		
	}
	
	private final static String EXT = "session";
	
	private final String cachePath;
	
	private final boolean useCache;
	
	private final ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();
	private final Map<String, Token> activeTokens = new HashMap<>();
	private final Logger logger;
	
	private ScheduledFuture<?> future;
	
	public AuthenticationCache(String tempPath, boolean useCache, Logger logger) {
		// TODO load active from files
		this.logger = logger;
		this.cachePath = tempPath + "/" + "sessions";
		this.useCache = useCache;
		if (useCache) {
			if (!new File(cachePath).mkdirs()) {
				logger.error("Temp session folder cannot be created: " + cachePath);
			}
		}
	}
	
	public void start() {
		if (useCache) {
			future = scheduledPool.scheduleWithFixedDelay(()->{
				logger.debug("Session folder check start");
				try {
					for (File file : new File(cachePath).listFiles()) {
						FileExtension ext = new FileExtension(file.getName());
						if (!EXT.equals(ext.getExtension())) {
							return; // silently ignore
						}
						Token token = activeTokens.get(ext.getName());
						if (token == null) {
							delete(ext.getName());
						} else if (token.expired < new Date().getTime()) {
							delete(ext.getName());
						}
					}
					logger.debug("Session folder check finish");
				} catch (Exception e) {
					logger.warn("Session folder check fail", e);
				}
			}, 0, 1, TimeUnit.HOURS);
		}
	}
	
	public void stop() {
		if (future != null) {
			future.cancel(true);
		}
		scheduledPool.shutdownNow();
	}

	public void delete(String id) {
		activeTokens.remove(id);
		try {
			File file = new File(getFileName(id));
			if (file.exists()) {
				file.delete();
			}
		} catch(Exception e) {
			logger.warn("Session file delete fail", e);
		}
	}
	
	public void refresh(String id, Long expirated) {
		// activeTokens.put(id, expirated);
		activeTokens.get(id).expired = expirated;
	}
	
	public void refresh(String id, User user) throws IOException {
		if (useCache) {
			try (FileOutputStream fileOutputStream = new FileOutputStream(getFileName(id));
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
				objectOutputStream.writeObject(user);
				objectOutputStream.flush();
			}
		}
	}
	
	public void save(String id, Long expirated, User user, String csrfToken) throws IOException {
	//	refresh(id, expirated);
		activeTokens.put(id, new Token(expirated, csrfToken));
		refresh(id, user);
	}
	
	public Long getExpirationTime(String id) {
		if (activeTokens.containsKey(id)) {
			return activeTokens.get(id).expired;
		}
		return null;
	}
	
	public String getCsrfToken(String id) {
		if (activeTokens.containsKey(id)) {
			return activeTokens.get(id).csrfToken;
		}
		return null;
	}
	
	public boolean validateCsrfToken(String id, String token) {
		if (activeTokens.containsKey(id)) {
			return activeTokens.get(id).csrfToken.equals(token);
		}
		return false;
	}
	
	public User get(String id) throws IOException, ClassNotFoundException {
		if (!activeTokens.containsKey(id)) {
			return null;
		}
		if (!useCache) {
			return null;
		}
		try (FileInputStream fileInputStream = new FileInputStream(getFileName(id));
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				) {
			return (User) objectInputStream.readObject();
		}
	}
	
	private String getFileName(String id) {
		return String.format("%s/%s.%s", cachePath, id, EXT);
	}
	
}
