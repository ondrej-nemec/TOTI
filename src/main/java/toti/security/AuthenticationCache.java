package toti.security;

import java.io.File;
import java.io.IOException;

import core.text.Text;

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
	
	public void create(Identity identity) throws IOException {
		Text.get().write((bw)->{
			// TODO serialize user - only user
		}, getFileName(identity.getId()), false);
	}
	
	public void get(String id) throws IOException {
		Text.get().read((br)->{
			// TODO deserialize user - only user
			return null;
		}, getFileName(id));
	}
	
	public void getAll() {
		// TODO
	}
	
	private String getFileName(String id) {
		return String.format("%s/%s.session", cachePath, id);
	}
	
}
