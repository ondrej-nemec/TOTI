package toti.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AuthenticationCache {
	
	private final String cachePath;
	
	public AuthenticationCache(String tempPath) {
		this.cachePath = tempPath + "/" + "sessions";
		new File(cachePath).mkdir();
	}

	public void delete(String id) {
		File file = new File(getFileName(id));
		if (file.exists()) {
			file.delete();
		}
	}
	
	public void save(String id, User user) throws IOException {
		try (FileOutputStream fileOutputStream = new FileOutputStream(getFileName(id));
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
			objectOutputStream.writeObject(user);
			objectOutputStream.flush();
		}
	}
	
	public User get(String id) throws IOException, ClassNotFoundException {
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
