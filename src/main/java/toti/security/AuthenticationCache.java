package toti.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AuthenticationCache {
	
	private final String cachePath;
	
	private final boolean useCache;
	
	private final ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();
	
	private ScheduledFuture<?> future;
	
	public AuthenticationCache(String tempPath, boolean useCache) {
		this.cachePath = tempPath + "/" + "sessions";
		this.useCache = useCache;
		if (useCache) {
			new File(cachePath).mkdir();
		}
	}
	
	public void start() {
		if (useCache) {
			future = scheduledPool.scheduleWithFixedDelay(()->{
				// TODO automatic delete expired files
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
		File file = new File(getFileName(id));
		if (file.exists()) {
			file.delete();
		}
	}
	
	public void save(String id, User user) throws IOException {
		if (!useCache) {
			return;
		}
		try (FileOutputStream fileOutputStream = new FileOutputStream(getFileName(id));
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
			objectOutputStream.writeObject(user);
			objectOutputStream.flush();
		}
	}
	
	public User get(String id) throws IOException, ClassNotFoundException {
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
		return String.format("%s/%s.session", cachePath, id);
	}
	
}
